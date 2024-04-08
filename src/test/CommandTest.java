package test;

import cache.Cache;
import command.*;
import enums.CommandType;
import org.junit.Test;
import record.CommandRecord;
import record.DataRecord;

import java.util.Arrays;
import java.util.List;


import static org.junit.Assert.assertEquals;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class CommandTest {



    @Test
    public void getCommandTest(){
        Cache cache = new Cache(10L);
        DataRecord dataRecord1 = new DataRecord(
                "k1",
                null,
                0L,
                0L,
                5L,
                "Hello");
        DataRecord dataRecord2 = new DataRecord(
                "k2",
                null,
                0L,
                0L,
                5L,
                "World");

        cache.add(dataRecord1);
        cache.add(dataRecord2);

        GetCommand getCommand = new GetCommand();
        getCommand.setCache(cache);

        var commandRecord = new CommandRecord(
                CommandType.GET,
                null,
                List.of("k1", "k2", "k3","k4"),
                null,
                0L,
                0L,
                0L,
                true,
                null,
                null
        );

        var res = getCommand.execute(commandRecord).get();

        List<String> resList = Arrays.asList(res.split("\r\n"));


        var val1 = resList.stream().filter(elem->List.of("Hello", "World").contains(elem)).toList().size();
        var val2 = resList.stream().filter(elem->elem.equals("END")).toList().size();

        assertEquals(val1, 2);
        assertEquals(val2, 4);

    }

    @Test
    public void setCommandTest(){
        Cache cache = new Cache(10L);

        SetCommand setCommand = new SetCommand();
        setCommand.setCache(cache);

        var commandRecord = new CommandRecord(
                CommandType.SET,
                "k1",
                null,
                null,
                0L,
                0L,
                5L,
                true,
                "Hello",
                null
        );


        var res = setCommand.execute(commandRecord).get();

        assertEquals(res, "STORED\r\n");

    }

    @Test
    public void addCommandTest(){
        Cache cache = new Cache(10L);

        AddCommand addCommand = new AddCommand();
        addCommand.setCache(cache);

        var commandRecord = new CommandRecord(
                CommandType.SET,
                "k1",
                null,
                null,
                0L,
                0L,
                5L,
                true,
                "Hello",
                null
        );


        String res = "";
        res += addCommand.execute(commandRecord).get();
        res += addCommand.execute(commandRecord).get();

        String[] resArr = res.split("\r\n");

        assertEquals(resArr[0], "STORED");
        assertEquals(resArr[1], "NOT_STORED");




    }

    @Test
    public void replaceCommandTest(){
        Cache cache = new Cache(10L);

        ReplaceCommand replaceCommand = new ReplaceCommand();
        AddCommand addCommand = new AddCommand();

        replaceCommand.setCache(cache);
        addCommand.setCache(cache);

        var commandRecord = new CommandRecord(
                CommandType.REPLACE,
                "k1",
                null,
                null,
                0L,
                0L,
                5L,
                true,
                "Hello",
                null
        );


        String res = "";
        res += replaceCommand.execute(commandRecord).get();
        addCommand.execute(commandRecord);
        res += replaceCommand.execute(commandRecord).get();


        String[] resArr = res.split("\r\n");

        assertEquals(resArr[0], "NOT_STORED");
        assertEquals(resArr[1], "STORED");

    }

    @Test
    public void appendCommandTest(){

        Cache cache = new Cache(10L);

        AppendCommand appendCommand = new AppendCommand();
        AddCommand addCommand = new AddCommand();

        appendCommand.setCache(cache);
        addCommand.setCache(cache);


        var commandRecord1 = new CommandRecord(
                CommandType.REPLACE,
                "k1",
                null,
                null,
                0L,
                0L,
                5L,
                true,
                "Hello",
                null
        );

        var commandRecord2 = new CommandRecord(
                CommandType.REPLACE,
                "k1",
                null,
                null,
                0L,
                0L,
                5L,
                true,
                "World",
                null
        );


        String res = "";
        res += appendCommand.execute(commandRecord1).get();
        addCommand.execute(commandRecord1);
        res += appendCommand.execute(commandRecord2).get();


        String[] resArr = res.split("\r\n");

        assertEquals(resArr[0], "NOT_STORED");
        assertEquals(resArr[1], "STORED");

    }

    @Test
    public void prependCommandTest(){

        Cache cache = new Cache(10L);

        PrependCommand prependCommand = new PrependCommand();
        AddCommand addCommand = new AddCommand();

        prependCommand.setCache(cache);
        addCommand.setCache(cache);


        var commandRecord1 = new CommandRecord(
                CommandType.REPLACE,
                "k1",
                null,
                null,
                0L,
                0L,
                5L,
                true,
                "Hello",
                null
        );

        var commandRecord2 = new CommandRecord(
                CommandType.REPLACE,
                "k1",
                null,
                null,
                0L,
                0L,
                5L,
                true,
                "World",
                null
        );

        String res = "";

        res += prependCommand.execute(commandRecord1).get();
        addCommand.execute(commandRecord1);
        res += prependCommand.execute(commandRecord2).get();


        String[] resArr = res.split("\r\n");

        assertEquals(resArr[0], "NOT_STORED");
        assertEquals(resArr[1], "STORED");


    }

    @Test
    public void incrementCommandTest(){

        Cache cache = new Cache(10L);

        IncrementCommand incrementCommand = new IncrementCommand();
        AddCommand addCommand = new AddCommand();

        incrementCommand.setCache(cache);
        addCommand.setCache(cache);

        var commandRecord = new CommandRecord(
                CommandType.ADD,
                "k1",
                null,
                null,
                0L,
                0L,
                5L,
                true,
                "50",
                null
        );

        var commandRecord2 = new CommandRecord(
                CommandType.INCR,
                "k1",
                null,
                null,
                0L,
                0L,
                5L,
                true,
                "50",
                10L
        );



        String res = "";

        addCommand.execute(commandRecord);
        res += incrementCommand.execute(commandRecord2).get();

        assertEquals(res, "60\r\n");

    }

    @Test
    public void decrementCommandTest(){
        Cache cache = new Cache(10L);

        DecrementCommand decrementCommand = new DecrementCommand();
        AddCommand addCommand = new AddCommand();

        decrementCommand.setCache(cache);
        addCommand.setCache(cache);

        var commandRecord = new CommandRecord(
                CommandType.ADD,
                "k1",
                null,
                null,
                0L,
                0L,
                5L,
                true,
                "50",
                null
        );

        var commandRecord2 = new CommandRecord(
                CommandType.INCR,
                "k1",
                null,
                null,
                0L,
                0L,
                5L,
                true,
                "50",
                10L
        );

        String res = "";

        addCommand.execute(commandRecord);
        res += decrementCommand.execute(commandRecord2).get();

        assertEquals(res, "40\r\n");

    }

    @Test
    public void deleteCommandTest(){

        Cache cache = new Cache(10L);

        DeleteCommand deleteCommand = new DeleteCommand();
        AddCommand addCommand = new AddCommand();

        deleteCommand.setCache(cache);
        addCommand.setCache(cache);

        var commandRecord = new CommandRecord(
                CommandType.DELETE,
                "k1",
                null,
                null,
                0L,
                0L,
                5L,
                true,
                "50",
                null
        );

        String res = "";

        res += deleteCommand.execute(commandRecord).get();
        addCommand.execute(commandRecord);
        res += deleteCommand.execute(commandRecord).get();


        String[] resArr = res.split("\r\n");

        assertEquals(resArr[0], "NOT_FOUND");
        assertEquals(resArr[1], "DELETED");

    }


}
