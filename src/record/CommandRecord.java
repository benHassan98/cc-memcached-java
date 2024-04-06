package record;

import enums.CommandType;

import java.util.List;

public record CommandRecord(CommandType commandType,
                            String key,
                            List<String> keyList,
                            Long casKey,
                            Long flags,
                            Long expTime,
                            Long byteCount,
                            Boolean reply,
                            String data,
                            Long delta
) {

    public CommandRecord(CommandType commandType,
                         String key,
                         List<String> keyList,
                         Long delta,
                         Boolean reply){
        this(commandType, key, keyList, null, null, null, null, reply, null, delta);

    }


}
