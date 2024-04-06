package command;

import record.CommandRecord;
import record.DataRecord;

import java.io.PrintWriter;

public class SetCommand extends Command{
    @Override
    public void excute(CommandRecord commandRecord, PrintWriter out) {

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
            out.print("STORED\n");
        }

    }
}
