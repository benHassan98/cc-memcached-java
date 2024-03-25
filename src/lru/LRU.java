package lru;

import record.DataRecord;


import java.util.*;


public class LRU {
    private final LinkedList<String> keyList = new LinkedList<>();
    private final HashMap<String, DataRecord> dataRecordMap = new HashMap<>();

    public boolean add(DataRecord dataRecord){

        keyList.addFirst(dataRecord.key());
        dataRecordMap.put(dataRecord.key(), dataRecord);


        return true;
    }

    public void promote(String key){

        keyList.remove(key);
        keyList.addFirst(key);

    }

    public DataRecord get(){
        return dataRecordMap.get(keyList.getLast());
    }

    public DataRecord delete(){

        String key = keyList.removeLast();

        return dataRecordMap.remove(key);
    }

    public boolean isEmpty(){
        return dataRecordMap.isEmpty();
    }
    
}
