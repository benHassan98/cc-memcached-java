package command;

import record.CommandRecord;
import record.DataRecord;

import java.io.PrintWriter;

public class DecrementCommand extends Command{
    @Override
    public void excute(CommandRecord commandRecord, PrintWriter out) {
        try{

            this.cache.update(commandRecord.key(),
                            (k, v)-> {
                                long newData = Long.parseLong(v.data()) - commandRecord.delta();
                                return new DataRecord(
                                        commandRecord.key(),
                                        v.casKey(),
                                        v.flags(),
                                        v.expTime(),
                                        (long) Long.toString(newData).length(),
                                        Long.toString(newData)
                                );
                            })
                    .ifPresentOrElse((v)->{
                        out.print(v.data()+"\n");
                    },()->{
                        out.print("NOT_FOUND\n");
                    });

        }
        catch (RuntimeException exception){
            exception.printStackTrace();
            out.print("CLIENT_ERROR cannot decrement non-numeric value\n");
        }

    }
}