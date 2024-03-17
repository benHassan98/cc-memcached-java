package lru;

import record.DataRecord;


import java.util.HashMap;
import java.util.LinkedList;

public class LRU {
    private final LinkedList<String> keyLinkedList = new LinkedList<>();
    private final HashMap<String, DataRecord> dataMap = new HashMap<>();


    public boolean add(DataRecord dataRecord){

        dataMap.put(dataRecord.key(), dataRecord);
        keyLinkedList.addFirst(dataRecord.key());

        return true;
    }

    public void increment(String key){

        keyLinkedList.remove(key);
        keyLinkedList.addFirst(key);

    }

    public DataRecord get(){

        var key = keyLinkedList.getLast();

        return dataMap.get(key);
    }

    public DataRecord delete(){

        var key = keyLinkedList.removeLast();

        return dataMap.remove(key);
    }


}
