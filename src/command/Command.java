package command;

import cache.Cache;
import record.CommandRecord;

import java.io.PrintWriter;

public abstract class Command {

    protected Cache cache;

    public abstract void execute(CommandRecord commandRecord, PrintWriter out);
    public void setCache(Cache cache){
        this.cache = cache;
    }
}
