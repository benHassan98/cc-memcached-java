package command;

import record.CommandRecord;
import record.DataRecord;

import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicBoolean;

public class CasCommand extends Command{
    @Override
    public void excute(CommandRecord commandRecord, PrintWriter out) {
        AtomicBoolean validOp = new AtomicBoolean();

        this.cache.update(commandRecord.key(), (k, v)->{
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

        )
                .ifPresentOrElse((v)->{

                    if(commandRecord.reply()){

                        if(validOp.get()){
                            out.print("STORED\n");
                        }else{
                            out.print("EXISTS\n");
                        }

                    }

                },()->{

                    if(commandRecord.reply()){
                        out.print("NOT_FOUND\n");
                    }

                });
    }
}
