package lru;

import record.DataRecord;


import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class LRU {
    private final LinkedList<String> keyList = new LinkedList<>();
    private final HashMap<String, DataRecord> dataRecordMap = new HashMap<>();
    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);
    private final ReentrantReadWriteLock.WriteLock writeLock = readWriteLock.writeLock();
    private final ReentrantReadWriteLock.ReadLock readLock = readWriteLock.readLock();

    public boolean add(DataRecord dataRecord){

        writeLock.lock();

        keyList.addFirst(dataRecord.key());
        dataRecordMap.put(dataRecord.key(), dataRecord);

        writeLock.unlock();

        return true;
    }

    public void promote(String key){

        writeLock.lock();

        if(keyList.remove(key)){
            keyList.addFirst(key);
        }

        writeLock.unlock();

    }

    public DataRecord get(){

        readLock.lock();

        var val = dataRecordMap.get(keyList.getLast());

        readLock.unlock();

        return val;
    }

    public DataRecord evict(){

        writeLock.lock();

        String key = keyList.removeLast();
        var val = dataRecordMap.remove(key);

        writeLock.unlock();

        return val;
    }

    public void remove(String key){

        writeLock.lock();

        keyList.remove(key);
        dataRecordMap.remove(key);

        writeLock.unlock();
    }

    public boolean isEmpty(){

        readLock.lock();

        var val = dataRecordMap.isEmpty();

        readLock.unlock();

        return val;
    }
    
}
