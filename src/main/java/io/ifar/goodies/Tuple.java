package io.ifar.goodies;

import com.google.common.base.Joiner;

public abstract class Tuple {
    private static final Joiner JOINER = Joiner.on(", ");

    private static String printValue(Object value) {
        return value == null ? "<null>" : value.toString();
    }

    private static String printTuple(Object... values) {
        StringBuilder builder = new StringBuilder("(");
        String[] printedValues = new String[values.length];
        for (int i=0; i < values.length; i++) {
            printedValues[i] = printValue(values[i]);
        }
        builder.append(JOINER.join(printedValues));
        builder.append(')');
        return builder.toString();
    }

    @Override
    public String toString() {
        return printTuple(getValues());
    }

    public abstract Object[] getValues();

    public abstract int getArity();
}
