package lru;

import record.DataRecord;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LRU {
    private final TreeMap<Long, DataRecord> dataRecordMap = new TreeMap<>();
    private final HashMap<String, Long> keyMap = new HashMap<>();
    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);
    private final ReentrantReadWriteLock.WriteLock writeLock = readWriteLock.writeLock();
    private final ReentrantReadWriteLock.ReadLock readLock = readWriteLock.readLock();

    public boolean add(DataRecord dataRecord){

        writeLock.lock();

        Long THRESHOLD = (long) 1e10;
        Long index = (long) dataRecordMap.size() > 0 ? dataRecordMap.lastKey() + 1 : 0;

        keyMap.put(dataRecord.key(), index);
        dataRecordMap.put(index, dataRecord);


        if(dataRecordMap.lastKey() >= THRESHOLD){
            CompletableFuture.runAsync(this::compressIndices);
        }
        
        
        writeLock.unlock();
        
        return true;
    }

    public void increment(String key){

        writeLock.lock();

        var oldIndex = keyMap.get(key);
        keyMap.remove(key);

        var dataRecord = dataRecordMap.get(oldIndex);
        dataRecordMap.remove(oldIndex);

        this.add(dataRecord);

        writeLock.unlock();
    }

    public DataRecord get(){

        readLock.lock();

        var value = dataRecordMap.firstEntry().getValue();

        readLock.unlock();

        return value;
    }

    public DataRecord delete(){

        writeLock.lock();

        var entry = dataRecordMap.firstEntry();

        keyMap.remove(entry.getValue().key());
        dataRecordMap.remove(entry.getKey());

        writeLock.unlock();

        return entry.getValue();
    }

    private void compressIndices(){

        var valueCollection = dataRecordMap.values().stream().toList();

        keyMap.clear();
        dataRecordMap.clear();

        valueCollection.forEach(dataRecord -> {

            keyMap.put(dataRecord.key(), (long) dataRecordMap.size());
            dataRecordMap.put((long) dataRecordMap.size(), dataRecord);
            
        });

    }

    
}
