package test;

import cache.Cache;
import enums.CommandType;
import org.junit.Test;
import record.DataRecord;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CacheTest {

    @Test
    public void addTest(){
        Cache cache = new Cache(10L);
        DataRecord dataRecord = new DataRecord(CommandType.SET,
                "key",
                null,
                0L,
                0L,
                5L,
                false,
                "World");
        boolean isAdded = cache.add(dataRecord);
        DataRecord addedRecord = cache.getByKey(dataRecord.key()).get();

        assertTrue(isAdded);
        assertEquals(addedRecord.key(), dataRecord.key());


    }

    @Test
    public void setTest(){
        Cache cache = new Cache(10L);
        DataRecord dataRecord = new DataRecord(CommandType.SET,
                "key",
                null,
                0L,
                0L,
                5L,
                false,
                "World");
        cache.set(dataRecord);
        DataRecord addedRecord = cache.getByKey(dataRecord.key()).get();


        assertEquals(addedRecord.key(), dataRecord.key());


    }

    @Test
    public void getTest(){
        Cache cache = new Cache(10L);
        DataRecord dataRecord = new DataRecord(CommandType.SET,
                "key",
                null,
                0L,
                0L,
                5L,
                false,
                "World");
        cache.set(dataRecord);
        DataRecord addedRecord1 = cache.getByKey(dataRecord.key()).get();
        DataRecord addedRecord2 = cache.getByCasKey(0L).get();


        assertEquals(addedRecord1.key(), dataRecord.key());
        assertEquals(addedRecord2.key(), dataRecord.key());

    }

    @Test
    public void deleteTest(){
        Cache cache = new Cache(10L);
        DataRecord dataRecord = new DataRecord(CommandType.SET,
                "key",
                null,
                0L,
                0L,
                5L,
                false,
                "World");
        cache.set(dataRecord);
        cache.delete(dataRecord.key());

        boolean addedRecord = cache.getByKey(dataRecord.key()).isEmpty();

        assertTrue(addedRecord);


    }



}
