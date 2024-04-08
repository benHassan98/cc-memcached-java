package command;

import record.CommandRecord;
import record.DataRecord;

import java.util.Optional;


public class AppendCommand extends Command{
    @Override
    public Optional<String> execute(CommandRecord commandRecord) {

        var res = this.cache.update(commandRecord.key(),
                (k, v)->new DataRecord(
                        commandRecord.key(),
                        1L,
                        commandRecord.flags(),
                        commandRecord.expTime(),
                        v.byteCount() + commandRecord.byteCount(),
                        v.data() + commandRecord.data()
                ));
        if(commandRecord.reply()){
            return res
                    .map(v->"STORED\n")
                    .or(()->Optional.of("NOT_STORED\n"));
        }

        return Optional.empty();

    }
}
