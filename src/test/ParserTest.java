package test;

import org.junit.jupiter.api.Test;
import parser.Parser;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;


public class ParserTest {

    @Test
    public void hasNextWithCommands() {

        Parser parser = new Parser();

        assertTrue(parser.hasNextLine("set"));
        assertFalse(parser.hasNextLine("bar"));

    }

    @Test
    public void hasNextWithNextLineCommands() throws Exception {

        Parser parser = new Parser();

        assertTrue(parser.hasNextLine("add"));
        assertTrue(parser.hasNextLine("replace"));
        assertFalse(parser.hasNextLine("get"));
        assertFalse(parser.hasNextLine("incr"));


    }

    @Test
    public void parseNotExistingCommand() {

        Parser parser = new Parser();

        assertThrows(Exception.class, ()->parser.parse(List.of("bar")));

    }

    @Test
    public void parseCommandWithNoArgs() {

        Parser parser = new Parser();

        assertThrows(Exception.class, ()->parser.parse(List.of("get")));

    }

    @Test
    public void parseGetCommand() throws Exception{

        Parser parser = new Parser();

        var rec = parser.parse(List.of("get k1 k2 k3"));


        assertEquals(rec.keyList().get(0), "k1");
        assertEquals(rec.keyList().get(1), "k2");
        assertEquals(rec.keyList().get(2), "k3");


    }

    @Test
    public void parseGetsCommand() throws Exception {

        Parser parser = new Parser();

        var rec = parser.parse(List.of("gets k1"));
        assertEquals(rec.key(), "k1");

        assertThrows(Exception.class, ()->parser.parse(List.of("gets")));

    }

    @Test
    public void parseDeleteCommand() throws Exception {

       Parser parser = new Parser();

       var rec = parser.parse(List.of("delete k1 noreply"));
       assertEquals(rec.key(), "k1");
       assertFalse(rec.reply());


       assertThrows(Exception.class, ()->parser.parse(List.of("delete k1 k2 k3")));

       assertThrows(Exception.class, ()->parser.parse(List.of("delete k1 reply")) );



    }

    @Test
    public void parseIncrDecrCommands() throws Exception {

        Parser parser = new Parser();

        var rec1 = parser.parse(List.of("incr k1 10"));
        var rec2 = parser.parse(List.of("decr k1 10"));

        assertEquals(rec1.key(), "k1");
        assertEquals(rec2.delta().longValue(), 10L);


        assertThrows(Exception.class, ()->parser.parse(List.of("incr k1 10 20")));
        assertThrows(Exception.class, ()->parser.parse(List.of("decr k1 -10")));
        assertThrows(Exception.class, ()->parser.parse(List.of("decr k1 k2")));

    }

    @Test
    public void parseCasCommand() throws Exception {

        Parser parser = new Parser();

        var rec = parser.parse(List.of("cas k1 100 0 5 0", "Hello"));
        assertEquals(rec.key(), "k1");
        assertEquals(rec.data(), "Hello");
        assertEquals(rec.flags(), 100L);
        assertEquals(rec.casKey(), 0);
        assertTrue(Objects.isNull(rec.delta()));


        assertThrows(Exception.class, ()->parser.parse(List.of("cas k1 100 0 5 0 reply", "Hello")));
        assertThrows(Exception.class, ()->parser.parse(List.of("cas k1 k2 0 5 0", "Hello")));
        assertThrows(Exception.class, ()->parser.parse(List.of("cas k1 100 0 3 0", "Hello")));
        assertThrows(Exception.class, ()->parser.parse(List.of("cas k1 100 0 3 0 3 2", "Hello")));
        assertThrows(Exception.class, ()->parser.parse(List.of("cas k1 100 0", "Hello")));


    }

}
