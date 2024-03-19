package test;

import enums.CommandType;
import lru.LRU;
import org.junit.Test;
import static org.junit.Assert.*;
import record.DataRecord;

public class LRUTest {


    @Test
    public void addTest(){

        LRU lru = new LRU();

        DataRecord dataRecord = new DataRecord(CommandType.SET,
                "Hello",
                0L,
                0L,
                0L,
                false,
                "World");



        assertTrue( lru.add(dataRecord) );


    }

    @Test
    public void getTest(){

        LRU lru = new LRU();

        DataRecord dataRecord1 = new DataRecord(CommandType.SET,
                "key1",
                0L,
                0L,
                0L,
                false,
                "World");

        DataRecord dataRecord2 = new DataRecord(CommandType.SET,
                "key2",
                0L,
                0L,
                0L,
                false,
                "World");

        boolean isAdded1 = lru.add(dataRecord1);
        boolean isAdded2 = lru.add(dataRecord2);

        var lastAddedRecord = lru.get();

        assertTrue(isAdded1);
        assertTrue(isAdded2);

        assertEquals(lastAddedRecord.key(), dataRecord1.key());

    }

    @Test
    public void incrementTest(){

        LRU lru = new LRU();

        DataRecord dataRecord1 = new DataRecord(CommandType.SET,
                "key1",
                0L,
                0L,
                0L,
                false,
                "World");

        DataRecord dataRecord2 = new DataRecord(CommandType.SET,
                "key2",
                0L,
                0L,
                0L,
                false,
                "World");

        boolean isAdded1 = lru.add(dataRecord1);
        boolean isAdded2 = lru.add(dataRecord2);

        lru.increment("key1");

        var lastAddedRecord = lru.get();

        assertTrue(isAdded1);
        assertTrue(isAdded2);

        assertEquals(lastAddedRecord.key(), dataRecord2.key());

    }

    @Test
    public void deleteTest(){

        LRU lru = new LRU();

        DataRecord dataRecord1 = new DataRecord(CommandType.SET,
                "key1",
                0L,
                0L,
                0L,
                false,
                "World");

        DataRecord dataRecord2 = new DataRecord(CommandType.SET,
                "key2",
                0L,
                0L,
                0L,
                false,
                "World");

        boolean isAdded1 = lru.add(dataRecord1);
        boolean isAdded2 = lru.add(dataRecord2);



        var lastAddedRecord = lru.delete();

        assertTrue(isAdded1);
        assertTrue(isAdded2);

        assertEquals(lastAddedRecord.key(), dataRecord1.key());

    }


}
