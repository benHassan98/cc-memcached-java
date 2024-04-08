package command;

import cache.Cache;
import record.CommandRecord;

import java.io.OutputStream;
import java.util.Optional;

public abstract class Command {

    protected Cache cache;

    public abstract Optional<String> execute(CommandRecord commandRecord);
    public void setCache(Cache cache){
        this.cache = cache;
    }
}
