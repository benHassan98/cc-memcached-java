package cache;

import lru.LRU;
import record.DataRecord;


import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiFunction;


public class Cache {

    private final Long size;
    private final AtomicLong freeSpace;
    private final ConcurrentHashMap<String, DataRecord> dataRecordContainer = new ConcurrentHashMap<>();
    private final PriorityQueue<DataRecord> dataRecordPriorityQueue = new PriorityQueue<>(Comparator.comparing(DataRecord::expTime));
    private final LRU lru = new LRU();
    private final PriorityQueue<Long> idQueue = new PriorityQueue<>();
    private final List<DataRecord> dataRecordList = new ArrayList<>();
    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);
    private final ReentrantReadWriteLock.WriteLock writeLock = readWriteLock.writeLock();
    private final ReentrantReadWriteLock.ReadLock readLock = readWriteLock.readLock();
    public Cache(Long size){
        this.size = size;
        this.freeSpace = new AtomicLong(size);
    }

    public void set(DataRecord dataRecord){

        writeLock.lock();

        this.delete(dataRecord.key());

        DataRecord savedRecord = saveRecord(dataRecord);

        writeLock.unlock();

        dataRecordContainer.put(savedRecord.key(), savedRecord);
    }
    public boolean add(DataRecord dataRecord){

        writeLock.lock();

        if(dataRecordContainer.containsKey(dataRecord.key())){
            writeLock.unlock();
            return false;
        }

        DataRecord savedRecord = saveRecord(dataRecord);

        writeLock.unlock();

        dataRecordContainer.putIfAbsent(savedRecord.key(), savedRecord);

        return true;


    }
    public Optional<DataRecord> getByKey(String key){

        if(this.deleteByKeyIfExpired(key)){
            return Optional.empty();
        }

        return Optional.ofNullable(
                this.dataRecordContainer.computeIfPresent(key, (k,v)->v)
        );

    }
    public Optional<DataRecord> getByCasKey(Long casKey){

        if(this.deleteByCasKeyIfExpired(casKey)){
            return Optional.empty();
        }

        readLock.lock();

        var val = Optional.ofNullable(
                casKey >= 0 && casKey < this.dataRecordList.size()?this.dataRecordList.get(casKey.intValue()):null
        );

        readLock.unlock();

        return val;
    }
    public Optional<DataRecord> update(String key, BiFunction<String, DataRecord, DataRecord> dataRecordBiFunction){

        if(this.deleteByKeyIfExpired(key)){
            return Optional.empty();
        }

        return Optional.ofNullable(
                this.dataRecordContainer.computeIfPresent(
                        key,
                        dataRecordBiFunction
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
        this.dataRecordList.set(dataRecord.casKey().intValue(), null);
        this.freeSpace.addAndGet(dataRecord.byteCount());
        this.releaseId(dataRecord.casKey());

        writeLock.unlock();

        return true;

    }
    private Long generateId(){


        return idQueue.isEmpty()? this.dataRecordList.size(): idQueue.poll();

    }
    private void releaseId(long id){

        idQueue.add(id);

    }

    private Long claimSpace(Long target){

        long claimedSpace = 0;
        Long currTime = new Date().getTime();
        while(!dataRecordPriorityQueue.isEmpty() && dataRecordPriorityQueue.peek().expTime() <= currTime && target > 0){

            var dataRecord = dataRecordPriorityQueue.poll();
            if(dataRecordContainer.containsKey(dataRecord.key())){
                claimedSpace += dataRecord.byteCount();
                target -= dataRecord.byteCount();
                dataRecordContainer.remove(dataRecord.key());
            }

        }


        while(!lru.isEmpty() && target > 0){

            var dataRecord = lru.delete();
            if(dataRecordContainer.containsKey(dataRecord.key())){
                claimedSpace += dataRecord.byteCount();
                target -= dataRecord.byteCount();
                dataRecordContainer.remove(dataRecord.key());
            }

        }

        return claimedSpace;
    }

    private DataRecord saveRecord(DataRecord dataRecord){

        if(this.freeSpace.get() < dataRecord.byteCount()){
            Long target = dataRecord.byteCount() - this.freeSpace.get();
            this.freeSpace.addAndGet(this.claimSpace(target));

        }

        this.freeSpace.set(this.freeSpace.get() - dataRecord.byteCount());

        var newId = generateId();
        var expTime = dataRecord.expTime() == -1 ? dataRecord.expTime() : dataRecord.expTime() * 1000L;
        var savedRecord = new DataRecord(
                dataRecord.key(),
                newId,
                dataRecord.flags(),
                expTime,
                dataRecord.byteCount(),
                dataRecord.reply(),
                dataRecord.data()
        );

        if(savedRecord.expTime() != 1){
            dataRecordPriorityQueue.add(savedRecord);
        }

        if(newId == this.dataRecordList.size()){
            this.dataRecordList.add(savedRecord);
        }
        else {
            this.dataRecordList.set(newId.intValue(), savedRecord);
        }

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

    private boolean deleteByCasKeyIfExpired(Long casKey){

        writeLock.lock();

        if(casKey < 0 || casKey >= this.dataRecordList.size() ||
                Objects.isNull(this.dataRecordList.get(casKey.intValue())) ||
                ! this.isExpired(this.dataRecordList.get(casKey.intValue()))

        ){
            writeLock.unlock();
            return false;
        }

        this.delete(this.dataRecordList.get(casKey.intValue()).key());

        writeLock.unlock();

        return true;
    }

    public Long getSize(){
        return this.size;
    }
}
