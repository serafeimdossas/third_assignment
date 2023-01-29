package jp.vmi.selenium.selenese.command;

/**
 * Default implementation for BlockStart.
 */
public abstract class BlockStartImpl extends AbstractCommand implements BlockStart {

    protected BlockEnd blockEnd;

    BlockStartImpl(int index, String name, String[] args, ArgumentType... argTypes) {
        super(index, name, args, argTypes);
    }

    @Override
    public boolean mayUpdateScreen() {
        return false;
    }

    @Override
    public void setBlockEnd(BlockEnd blockEnd) {
        this.blockEnd = blockEnd;
    }

    @SuppressWarnings("javadoc")
    public static long getLongValue(Context context, String s) {
        return ((Number) context.executeScript("return (" + s + ")")).longValue();
    }

    @SuppressWarnings("javadoc")
    public static long getLoopLimit(Context context, String[] args, int index) {
        if (index >= args.length)
            return Long.MAX_VALUE;
        String s = args[index];
        if (s == null || s.isEmpty())
            return Long.MAX_VALUE;
        return getLongValue(context, s);
    }
}
