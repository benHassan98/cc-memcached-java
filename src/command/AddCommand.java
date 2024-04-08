package command;

import record.CommandRecord;
import record.DataRecord;

import java.util.Optional;

public class AddCommand extends Command{
    @Override
    public Optional<String> execute(CommandRecord commandRecord) {

        var res = this.cache.add(
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
            return res ? Optional.of("STORED\n"): Optional.of("NOT_STORED\n");

        }

        return Optional.empty();
    }
}
