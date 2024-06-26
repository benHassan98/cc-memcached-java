package command;

import record.CommandRecord;

import java.io.OutputStream;
import java.util.Optional;

public class GetsCommand extends Command{
    @Override
    public Optional<String> execute(CommandRecord commandRecord) {

        StringBuilder stringBuilder = new StringBuilder();

        this.cache.get(commandRecord.key()).ifPresent((v)->
                stringBuilder
                        .append("VALUE ")
                        .append(commandRecord.key())
                        .append(" ")
                        .append(v.flags())
                        .append(" ")
                        .append(v.byteCount())
                        .append(" ")
                        .append(v.casKey())
                        .append("\r\n")
                        .append(v.data())
                        .append("\r\n")
        );
        stringBuilder.append("END\r\n");

        return Optional.of(stringBuilder.toString());
    }
}
