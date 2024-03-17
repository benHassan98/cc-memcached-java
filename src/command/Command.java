package command;

import record.DataRecord;

import java.io.PrintWriter;

public abstract class Command {

    public abstract void excute(DataRecord dataRecord, PrintWriter out);
}
