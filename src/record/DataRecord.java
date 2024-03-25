package record;

import enums.CommandType;

public record DataRecord(CommandType commandType,
                         String key,
                         Long casKey,
                         Long flags,
                         Long expTime,
                         Long byteCount,
                         Boolean reply,
                         String data) {
}
