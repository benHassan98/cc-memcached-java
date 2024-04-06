package test;

import cache.Cache;
import org.junit.Test;
import record.DataRecord;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CacheTest {

    @Test
    public void addTest(){
        Cache cache = new Cache(10L);
        DataRecord dataRecord = new DataRecord(
                "key",
                null,
                0L,
                0L,
                5L,
                "World");
        boolean isAdded = cache.add(dataRecord);
        DataRecord addedRecord = cache.get(dataRecord.key()).get();

        assertTrue(isAdded);
        assertEquals(addedRecord.key(), dataRecord.key());


    }

    @Test
    public void setTest(){
        Cache cache = new Cache(10L);
        DataRecord dataRecord = new DataRecord(
                "key",
                null,
                0L,
                0L,
                5L,
                "World");
        cache.set(dataRecord);
        DataRecord addedRecord = cache.get(dataRecord.key()).get();


        assertEquals(addedRecord.key(), dataRecord.key());


    }

    @Test
    public void getTest(){
        Cache cache = new Cache(10L);
        DataRecord dataRecord = new DataRecord(
                "key",
                null,
                0L,
                0L,
                5L,
                "World");
        cache.set(dataRecord);
        DataRecord addedRecord = cache.get(dataRecord.key()).get();



        assertEquals(addedRecord.key(), dataRecord.key());


    }

    @Test
    public void deleteTest(){
        Cache cache = new Cache(10L);
        DataRecord dataRecord = new DataRecord(
                "key",
                null,
                0L,
                0L,
                5L,
                "World");
        cache.set(dataRecord);
        boolean isDeleted = cache.delete(dataRecord.key());

        boolean addedRecord = cache.get(dataRecord.key()).isEmpty();

        assertTrue(addedRecord);
        assertTrue(isDeleted);


    }

    @Test
    public void updateTest(){

        Cache cache = new Cache(10L);
        DataRecord dataRecord = new DataRecord(
                "key",
                null,
                0L,
                0L,
                5L,
                "World");

        long delta = 5;

        cache.add(dataRecord);
        assertThrows(RuntimeException.class, ()->
                cache.update("key", (k, v)-> new DataRecord(
                        k,
                        null,
                        0L,
                        0L,
                        (long) Long.valueOf(Long.parseLong(v.data()) + delta).toString().length(),
                        Long.valueOf(Long.parseLong(v.data()) + delta).toString()
                ))
        );

        var res = cache.update("key", (k, v)-> new DataRecord(
                k,
                null,
                0L,
                0L,
                5L,
                "Hello"
        ));

        assertEquals(res.get().key(), dataRecord.key());
        assertEquals(res.get().data(), "Hello");

    }

    @Test
    public void concurrentTest(){

    }




}
