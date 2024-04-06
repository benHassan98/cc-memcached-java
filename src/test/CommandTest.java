package test;

import cache.Cache;
import command.*;
import enums.CommandType;
import org.apache.commons.collections4.Get;
import org.junit.Test;
import record.CommandRecord;
import record.DataRecord;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

        StringWriter stringWriter = new StringWriter();
        getCommand.excute(commandRecord, new PrintWriter(stringWriter));

        List<String> resList = Arrays.asList(stringWriter.toString().split("\n"));


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

        StringWriter stringWriter = new StringWriter();
        setCommand.excute(commandRecord, new PrintWriter(stringWriter));

        String res = stringWriter.toString();

        assertEquals(res, "STORED\n");

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

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        addCommand.excute(commandRecord, printWriter);
        addCommand.excute(commandRecord, printWriter);

        String[] res = stringWriter.toString().split("\n");

        assertEquals(res[0], "STORED");
        assertEquals(res[1], "NOT_STORED");




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

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);

        replaceCommand.excute(commandRecord, printWriter);
        addCommand.excute(commandRecord, new PrintWriter(new StringWriter()));
        replaceCommand.excute(commandRecord, printWriter);


        String[] res = stringWriter.toString().split("\n");

        assertEquals(res[0], "NOT_STORED");
        assertEquals(res[1], "STORED");

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

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);

        appendCommand.excute(commandRecord1, printWriter);
        addCommand.excute(commandRecord1, new PrintWriter(new StringWriter()));
        appendCommand.excute(commandRecord2, printWriter);


        String[] res = stringWriter.toString().split("\n");

        assertEquals(res[0], "NOT_STORED");
        assertEquals(res[1], "STORED");

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

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);

        prependCommand.excute(commandRecord1, printWriter);
        addCommand.excute(commandRecord1, new PrintWriter(new StringWriter()));
        prependCommand.excute(commandRecord2, printWriter);


        String[] res = stringWriter.toString().split("\n");

        assertEquals(res[0], "NOT_STORED");
        assertEquals(res[1], "STORED");


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


        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);

        addCommand.excute(commandRecord, new PrintWriter(new StringWriter()));
        incrementCommand.excute(commandRecord2, printWriter);


        String res = stringWriter.toString();

        assertEquals(res, "60\n");



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


        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);

        addCommand.excute(commandRecord, new PrintWriter(new StringWriter()));
        decrementCommand.excute(commandRecord2, printWriter);


        String res = stringWriter.toString();

        assertEquals(res, "40\n");

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

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);


        deleteCommand.excute(commandRecord, printWriter);
        addCommand.excute(commandRecord, new PrintWriter(new StringWriter()));
        deleteCommand.excute(commandRecord, printWriter);


        String[] res = stringWriter.toString().split("\n");

        assertEquals(res[0], "NOT_FOUND");
        assertEquals(res[1], "DELETED");

    }


}
