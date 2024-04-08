package command;

import record.CommandRecord;

import java.io.OutputStream;
import java.util.Optional;

public class GetCommand extends Command{
    @Override
    public Optional<String> execute(CommandRecord commandRecord) {

        StringBuilder stringBuilder = new StringBuilder();

        commandRecord.keyList().forEach((key)->{

            this.cache.get(key).ifPresent((v)->
                    stringBuilder
                            .append("VALUE ")
                            .append(key)
                            .append(" ")
                            .append(v.flags())
                            .append(" ")
                            .append(v.byteCount())
                            .append("\r\n")
                            .append(v.data())
                            .append("\r\n")
            );
            stringBuilder.append("END\r\n");

        });

        return Optional.of(stringBuilder.toString());

    }
}
