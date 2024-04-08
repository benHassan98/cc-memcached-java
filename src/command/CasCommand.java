package command;

import record.CommandRecord;
import record.DataRecord;

import java.io.OutputStream;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class CasCommand extends Command{
    @Override
    public Optional<String> execute(CommandRecord commandRecord) {
        AtomicBoolean validOp = new AtomicBoolean();

        var res = this.cache.update(commandRecord.key(), (k, v)->{
            validOp.set(v.casKey().equals( commandRecord.casKey()));
            return !v.casKey().equals( commandRecord.casKey()) ? v:
                    new DataRecord(
                            commandRecord.key(),
                            (commandRecord.casKey() + 1) % Long.MAX_VALUE,
                            commandRecord.flags(),
                            commandRecord.expTime(),
                            commandRecord.byteCount(),
                            commandRecord.data()
                    );

                }

        );

        if(commandRecord.reply()){

            return res
                    .map(v->validOp.get()?"STORED\n":"EXISTS\n")
                    .or(()->Optional.of("NOT_FOUND\n"));

        }

        return Optional.empty();
    }
}
