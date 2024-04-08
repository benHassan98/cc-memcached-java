package command;

import record.CommandRecord;

import java.util.Optional;

public class DeleteCommand extends Command{
    @Override
    public Optional<String> execute(CommandRecord commandRecord) {

        var res = this.cache.delete(commandRecord.key());

        if(commandRecord.reply()){
            return res ? Optional.of("DELETED\r\n"): Optional.of("NOT_FOUND\r\n");

        }
        return Optional.empty();
    }
}
