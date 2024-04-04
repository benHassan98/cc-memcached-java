package record;


public record DataRecord(String key,
                         String casKey,
                         Long flags,
                         Long expTime,
                         Long byteCount,
                         Boolean reply,
                         String data) {
}
