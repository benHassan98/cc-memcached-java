package command;

import record.CommandRecord;

import java.io.PrintWriter;

public class GetCommand extends Command{
    @Override
    public void excute(CommandRecord commandRecord, PrintWriter out) {

        commandRecord.keyList().forEach((key)->{

            this.cache.get(key).ifPresent((v)->{
                out.print("VALUE "+key+" "+v.flags()+" "+v.byteCount()+"\n");
                out.print(v.data()+"\n");
            });
            out.print("END\n");

        });



    }
}
