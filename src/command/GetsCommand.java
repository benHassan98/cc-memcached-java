package command;

import record.CommandRecord;

import java.io.PrintWriter;

public class GetsCommand extends Command{
    @Override
    public void execute(CommandRecord commandRecord, PrintWriter out) {
        this.cache.get(commandRecord.key()).ifPresent((v)->{
            out.print("VALUE "+commandRecord.key()+" "+v.flags()+" "+v.byteCount()+" "+v.casKey()+"\n");
            out.print(v.data()+"\n");
        });
        out.print("END\n");

    }
}
