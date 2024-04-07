package command;

import record.CommandRecord;

import java.io.PrintWriter;

public class DeleteCommand extends Command{
    @Override
    public void execute(CommandRecord commandRecord, PrintWriter out) {

        var res = this.cache.delete(commandRecord.key());

        if(commandRecord.reply()){
            if(res){
                out.print("DELETED\n");
            }else{
                out.print("NOT_FOUND\n");
            }
        }

    }
}
