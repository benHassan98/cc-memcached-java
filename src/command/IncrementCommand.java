package command;

import record.CommandRecord;
import record.DataRecord;

import java.io.OutputStream;
import java.util.Optional;

public class IncrementCommand extends Command{
    @Override
    public Optional<String> execute(CommandRecord commandRecord) {

        try{

            return this.cache.update(commandRecord.key(),
                    (k, v)-> {
                        long newData = Long.parseLong(v.data()) + commandRecord.delta();
                        return new DataRecord(
                                commandRecord.key(),
                                v.casKey(),
                                v.flags(),
                                v.expTime(),
                                (long) Long.toString(newData).length(),
                                Long.toString(newData)
                        );
                    })
                    .map(v->v.data()+"\n")
                    .or(()->Optional.of("NOT_FOUND\n"));

        }
        catch (RuntimeException exception){
            exception.printStackTrace();
            return Optional.of("CLIENT_ERROR cannot increment non-numeric value\n");
        }


    }
}
