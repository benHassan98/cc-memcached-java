package factory;

import command.*;
import enums.CommandType;


import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class CommandFactory {

    private final HashMap<CommandType, Supplier<Command>> typeSupplier = new HashMap<>(
            Map.ofEntries(
                    Map.entry(CommandType.SET, SetCommand::new),
                    Map.entry(CommandType.GET, GetCommand::new),
                    Map.entry(CommandType.GETS, GetsCommand::new),
                    Map.entry(CommandType.ADD, AddCommand::new),
                    Map.entry(CommandType.REPLACE, ReplaceCommand::new),
                    Map.entry(CommandType.PREPEND, PrependCommand::new),
                    Map.entry(CommandType.APPEND, AppendCommand::new),
                    Map.entry(CommandType.CAS, CasCommand::new),
                    Map.entry(CommandType.DELETE, DeleteCommand::new),
                    Map.entry(CommandType.INCR, IncrementCommand::new),
                    Map.entry(CommandType.DECR, DecrementCommand::new)

            )
    );


    public Command getCommandByType(CommandType commandType){
        return typeSupplier.get(commandType).get();
    }

}
