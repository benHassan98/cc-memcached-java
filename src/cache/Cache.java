package cache;

import lru.LRU;
import record.DataRecord;



import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiFunction;


public class Cache {

    private final Long size;
    private final AtomicLong freeSpace;
    private final ConcurrentHashMap<String, DataRecord> dataRecordContainer = new ConcurrentHashMap<>();
    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);
    private final ReentrantReadWriteLock.WriteLock writeLock = readWriteLock.writeLock();
    private final PriorityQueue<DataRecord> dataRecordPriorityQueue = new PriorityQueue<>(Comparator.comparing(DataRecord::expTime));
    private final ReentrantReadWriteLock priorityQueueReadWriteLock = new ReentrantReadWriteLock(true);
    private final ReentrantReadWriteLock.WriteLock priorityQueueWriteLock = priorityQueueReadWriteLock.writeLock();
    private final LRU lru = new LRU();


    public Cache(Long size){
        this.size = size;
        this.freeSpace = new AtomicLong(size);

        new ScheduledThreadPoolExecutor(1).
                scheduleWithFixedDelay(this::claimSpaceFromExpiredData,
                        1000,
                        1000,
                        TimeUnit.MICROSECONDS);

    }

    public void set(DataRecord dataRecord){

        this.delete(dataRecord.key());

        dataRecordContainer.put(dataRecord.key(), this.saveRecord(dataRecord));

    }
    public boolean add(DataRecord dataRecord){

        if(dataRecordContainer.containsKey(dataRecord.key())){
            return false;
        }

        dataRecordContainer.putIfAbsent(dataRecord.key(), this.saveRecord(dataRecord));
        return true;


    }
    public Optional<DataRecord> get(String key){

        if(this.deleteByKeyIfExpired(key)){
            return Optional.empty();
        }

        lru.promote(key);

        return Optional.ofNullable(
                this.dataRecordContainer.computeIfPresent(key, (k,v)-> v)
        );

    }
    public Optional<DataRecord> update(String key, BiFunction<String, DataRecord, DataRecord> dataRecordBiFunction){

        if(this.deleteByKeyIfExpired(key)){
            return Optional.empty();
        }

        BiFunction<String, DataRecord, DataRecord> callbackFun =  (k, v)->{

            var newDataRecord = dataRecordBiFunction.apply(k, v);

            this.freeSpace.updateAndGet((curr)->{

                long newVal = curr + v.byteCount();

                if(newVal < newDataRecord.byteCount()){
                    Long target = newDataRecord.byteCount() - newVal;
                    newVal += this.claimSpaceWithEviction(target);
                }
                newVal -= newDataRecord.byteCount();
                return newVal;
            });


            return newDataRecord;
        };


        lru.promote(key);

        return Optional.ofNullable(
                this.dataRecordContainer.computeIfPresent(
                        key,
                        callbackFun
                )
        );
    }
    public boolean delete(String key){

        writeLock.lock();

        if(!dataRecordContainer.containsKey(key)){
            writeLock.unlock();
            return false;
        }

        var dataRecord = this.dataRecordContainer.remove(key);
        this.freeSpace.addAndGet(dataRecord.byteCount());

        writeLock.unlock();

        return true;

    }
    private Long claimSpaceWithEviction(Long target){

        long claimedSpace = 0;

        while(!lru.isEmpty() && target > 0){

            var dataRecord = lru.evict();
            if(dataRecordContainer.containsKey(dataRecord.key())){
                claimedSpace += dataRecord.byteCount();
                target -= dataRecord.byteCount();
                dataRecordContainer.remove(dataRecord.key());
            }

        }

        return claimedSpace;
    }

    private void claimSpaceFromExpiredData(){

        long claimedSpace = 0;
        Long currTime = new Date().getTime();

        priorityQueueWriteLock.lock();

        while(!dataRecordPriorityQueue.isEmpty() && dataRecordPriorityQueue.peek().expTime() <= currTime){

            var polledRecord = dataRecordPriorityQueue.poll();
            var savedRecordOptional = Optional.ofNullable(
                    dataRecordContainer.computeIfPresent(polledRecord.key(), (k, v)->v)
            );
            if(savedRecordOptional.isPresent() && savedRecordOptional.get().casKey().equals(polledRecord.casKey())   ){
                claimedSpace += polledRecord.byteCount();
                this.delete(polledRecord.key());
            }

        }

        priorityQueueWriteLock.unlock();

        this.freeSpace.addAndGet(claimedSpace);


    }


    private DataRecord saveRecord(DataRecord dataRecord){

        this.freeSpace.updateAndGet((curr)->{

            long newVal = curr;

            if(curr < dataRecord.byteCount()){
                Long target = dataRecord.byteCount() - curr;
                newVal = curr + this.claimSpaceWithEviction(target);
            }
            newVal -= dataRecord.byteCount();
            return newVal;
        });




        var expTime = dataRecord.expTime() * 1000L;
        var savedRecord = new DataRecord(
                dataRecord.key(),
                1L,
                dataRecord.flags(),
                expTime,
                dataRecord.byteCount(),
                dataRecord.data()
        );

        priorityQueueWriteLock.lock();

        if(savedRecord.expTime() > 0){
            dataRecordPriorityQueue.add(savedRecord);
        }

        priorityQueueWriteLock.unlock();

        lru.add(savedRecord);

        return savedRecord;
    }

    private boolean isExpired(DataRecord dataRecord){
        return  Objects.nonNull(dataRecord) && dataRecord.expTime() <= new Date().getTime() && dataRecord.expTime() != 0;
    }

    private boolean deleteByKeyIfExpired(String key){

        writeLock.lock();

        if(this.isExpired(this.dataRecordContainer.get(key))){
            this.delete(key);
            writeLock.unlock();
            return true;
        }

        writeLock.unlock();
        return false;
    }

    public Long getSize(){
        return this.size;
    }
}
