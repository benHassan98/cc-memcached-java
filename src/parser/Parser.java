package parser;

import enums.CommandType;
import record.CommandRecord;


import java.util.*;

public class Parser {

    private final HashMap<String, CommandType> typeMap = new HashMap<>(
            Map.ofEntries(
                    Map.entry("set",CommandType.SET),
                    Map.entry("get",CommandType.GET),
                    Map.entry("gets",CommandType.GETS),
                    Map.entry("add",CommandType.ADD),
                    Map.entry("replace",CommandType.REPLACE),
                    Map.entry("prepend",CommandType.PREPEND),
                    Map.entry("append",CommandType.APPEND),
                    Map.entry("cas",CommandType.CAS),
                    Map.entry("delete",CommandType.DELETE),
                    Map.entry("incr",CommandType.INCR),
                    Map.entry("decr",CommandType.DECR)
            )

    );



    public CommandRecord parse(List<String> inputList) throws Exception {

        if(!typeMap.containsKey(inputList.get(0).split(" ")[0])){
            throw new Exception("Command not found");
        }
        if(inputList.get(0).split(" ").length < 2){
            throw new Exception("too few arguments");
        }

        String[] line1Arr = inputList.get(0).split(" ");

        String commandName = line1Arr[0];
        String[] args = Arrays.copyOfRange(line1Arr, 1, line1Arr.length);

        if("get".equals(commandName)){
            return new CommandRecord(
                    typeMap.get(commandName),
                    null,
                    Arrays.asList(args),
                    null,
                    null
            );
        }


        if("gets".equals(commandName)){

            if(args.length != 1){
                throw new Exception("Please enter the correct format : gets <key>");
            }

            return new CommandRecord(
                    typeMap.get(commandName),
                    args[0],
                    null,
                    null,
                    null
            );
        }

        if("delete".equals(commandName)){

            if(args.length < 1 || args.length > 2){
                throw new Exception("Please enter the correct format : delete <key> [noreply]");
            }

            if(args.length == 2 && !"noreply".equals(args[1])){
                throw new Exception("Please enter the correct format : delete <key> [noreply]");
            }


            return new CommandRecord(
                    typeMap.get(commandName),
                    args[0],
                    null,
                    null,
                    args.length != 2
            );
        }

        if(List.of("incr", "decr").contains(commandName)){

            if(args.length != 2){
                throw new Exception("Please enter the correct format : "+ commandName +" <key> <delta> [noreply] ");
            }

            long delta;

            try{
                delta = Long.parseLong(args[1]);
            }
            catch (NumberFormatException exception){
                throw new Exception("Please enter a delta(number) greater than 0");
            }


            if(delta <= 0){
                throw new Exception("Please enter a delta greater than 0");
            }

            return new CommandRecord(
                    typeMap.get(commandName),
                    args[0],
                    null,
                    delta,
                    null
            );
        }

        if("cas".equals(commandName) && (args.length < 5 || args.length > 6)){
            throw new Exception("Please enter the correct format : cas <key> <flags> <exptime> <bytes> <unique_cas_key> [noreply]");
        }

        if(!"cas".equals(commandName) && (args.length < 4 || args.length > 5) ){
            throw new Exception("Please enter the correct format : <command> <key> <flags> <exptime> <bytes> [noreply]");
        }

        int argsIndx = args.length - 1;

        boolean reply = true;

        if((!"cas".equals(commandName) && args.length == 5) || ("cas".equals(commandName) && args.length == 6) ){

            if(!"noreply".equals(args[ argsIndx ])){
                throw new Exception("Unknown last argument");
            }

            reply = false;
            argsIndx--;

        }

        long casKey = 0;

        if("cas".equals(commandName)){

            try{
                casKey = Long.parseLong(args[ argsIndx-- ]);
            }
            catch (NumberFormatException exception){
                throw new Exception("Please enter an unique_cas_key(number)");
            }

            if(casKey < 0){
                throw new Exception("Please enter a positive unique_cas_key ");
            }



        }

        long bytes;

        try{
            bytes = Long.parseLong(args[ argsIndx ]);
        }
        catch (NumberFormatException exception){
            throw new Exception("Please enter a bytes(number)");
        }

        if(bytes < 0){
            throw new Exception("Please enter a bytes count greater than 0");
        }
        if(bytes > 250){
            throw new Exception("Please enter a bytes count less than 250");
        }

        argsIndx--;

        long expTime;

        try{
            expTime = Long.parseLong(args[ argsIndx]);
        }
        catch (NumberFormatException exception){
            throw new Exception("Please enter an exptime(number)");
        }

        argsIndx--;

        long flags;

        try{
            flags = Long.parseLong(args[ argsIndx]);
        }
        catch (NumberFormatException exception){
            throw new Exception("Please enter a flags(number)");
        }

        if(flags < 0){
            throw new Exception("Please enter a flags greater than 0");
        }

        if(flags > 65535){
            throw new Exception("Please enter a flags less than 65535");
        }

        argsIndx--;

        String key = args[argsIndx];


        if( inputList.size() == 2  && inputList.get(1).length() != bytes){
            throw new Exception("data block size is not equal to bytes count");
        }


        return new CommandRecord(
                typeMap.get(commandName),
                key,
                null,
                casKey,
                flags,
                expTime,
                bytes,
                reply,
                inputList.size() == 2? inputList.get(1) : "",
                null
        );
    }

    public boolean hasNextLine(String line) {

        String commandName = line.split(" ")[0];

        if(!typeMap.containsKey(commandName)){
            return false;
        }

        return !List.of("get", "gets", "delete", "incr", "decr").contains(commandName);
    }



}
