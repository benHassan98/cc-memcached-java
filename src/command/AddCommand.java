package command;

import record.CommandRecord;
import record.DataRecord;

import java.io.PrintWriter;

public class AddCommand extends Command{
    @Override
    public void execute(CommandRecord commandRecord, PrintWriter out) {

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

        if(res && commandRecord.reply()){
            out.print("STORED\n");
        }else if (!res && commandRecord.reply()){
            out.print("NOT_STORED\n");
        }


    }
}
