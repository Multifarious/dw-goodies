package io.ifar.goodies;

/**
 * An immutable 3-tuple.
 */
public class Triple<S, T, U> extends Tuple {
    public final S left;
    public final T middle;
    public final U right;

    public Triple(S left, T middle, U right) {
        this.left = left;
        this.middle = middle;
        this.right = right;
    }

    public S getLeft() {
        return left;
    }

    public U getRight() {
        return right;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Triple triple = (Triple) o;

        if (left != null ? !left.equals(triple.left) : triple.left != null) return false;
        if (middle != null ? !middle.equals(triple.middle) : triple.middle != null) return false;
        if (right != null ? !right.equals(triple.right) : triple.right != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = left != null ? left.hashCode() : 0;
        result = 31 * result + (middle != null ? middle.hashCode() : 0);
        result = 31 * result + (right != null ? right.hashCode() : 0);
        return result;
    }

    @Override
    public Object[] getValues() {
        return new Object[]{left,middle,right};
    }

    @Override
    public int getArity() {
        return 3;
    }
}