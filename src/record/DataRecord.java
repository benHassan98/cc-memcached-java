package record;


public record DataRecord(String key,
                         Long casKey,
                         Long flags,
                         Long expTime,
                         Long byteCount,
                         String data) {
}
