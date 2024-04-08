package command;

import record.CommandRecord;
import record.DataRecord;

import java.io.OutputStream;
import java.util.Optional;


public class ReplaceCommand extends Command{
    @Override
    public Optional<String> execute(CommandRecord commandRecord) {

        var res = this.cache.update(
                commandRecord.key(),
                (k, v)-> new DataRecord(
                        commandRecord.key(),
                        1L,
                        commandRecord.flags(),
                        commandRecord.expTime(),
                        commandRecord.byteCount(),
                        commandRecord.data()
                )
        );

        if (commandRecord.reply()){
            return res
                    .map(v->"STORED\r\n")
                    .or(()->Optional.of("NOT_STORED\r\n"));

        }


        return Optional.empty();
    }
}
