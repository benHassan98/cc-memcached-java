package command;

import record.CommandRecord;
import record.DataRecord;

import java.io.PrintWriter;


public class ReplaceCommand extends Command{
    @Override
    public void execute(CommandRecord commandRecord, PrintWriter out) {

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
            res
                    .ifPresentOrElse(v->out.print("STORED\n"),
                            ()->out.print("NOT_STORED\n"));
        }



    }
}
