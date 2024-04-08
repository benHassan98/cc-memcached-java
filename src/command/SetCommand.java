package command;

import record.CommandRecord;
import record.DataRecord;

import java.io.OutputStream;
import java.util.Optional;

public class SetCommand extends Command{
    @Override
    public Optional<String> execute(CommandRecord commandRecord) {

        this.cache.set(
                new DataRecord(
                        commandRecord.key(),
                        1L,
                        commandRecord.flags(),
                        commandRecord.expTime(),
                        commandRecord.byteCount(),
                        commandRecord.data()
                )
        );

        if(commandRecord.reply()){
            return Optional.of("STORED\n");
        }
        return Optional.empty();
    }
}
