package command;

import record.CommandRecord;
import record.DataRecord;


import java.util.Optional;

public class PrependCommand extends Command{
    @Override
    public Optional<String> execute(CommandRecord commandRecord) {
        var res = this.cache.update(commandRecord.key(),
                (k, v)->new DataRecord(
                        commandRecord.key(),
                        1L,
                        commandRecord.flags(),
                        commandRecord.expTime(),
                        v.byteCount() + commandRecord.byteCount(),
                        commandRecord.data() + v.data()
                ));
        if(commandRecord.reply()){
            return res
                    .map(v->"STORED\r\n")
                    .or(()->Optional.of("NOT_STORED\r\n"));
        }
        return Optional.empty();
    }
}
