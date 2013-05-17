package io.ifar.goodies;

import java.util.Iterator;

/**
 * Wraps Iterators, providing AutoCloseable and Iterable interfaces.
 *
 * Particularly intended for use with JDBI's ResultIterator; failing to exhaust
 * such an iterator will leak a database connection.
 */
public class AutoCloseableIterator<T> implements IterableIterator<T>, AutoCloseable {
    private final Iterator<T> wrapped;
    private final AutoCloseable closeHook;

    /**
     * Wraps a standard Iterator with no-op close behavior.
     */
    public AutoCloseableIterator(final Iterator<T> wrapped) {
        this(wrapped,null);
    }

    /**
     * Wraps Iterator and invokes provided closeHook when closed. Typical usage:
     *
     * <pre>
     * new AutoClosableIterator(myIterator, new AutoCloseable() {
     *     &#64;Override
     *     public void close() throws Exception {
     *         myIterator.close();
     *     }
     * });
     * </pre>
     *
     *
     * @param wrapped
     * @param closeHook
     */
    public AutoCloseableIterator(final Iterator<T> wrapped, AutoCloseable closeHook) {
        this.wrapped = wrapped;
        this.closeHook = closeHook;
    }

    @Override
    public void close() throws Exception {
        if (closeHook != null) {
            closeHook.close();
        }
    }

    @Override
    public boolean hasNext() {
        return wrapped.hasNext();
    }

    @Override
    public T next() {
        return wrapped.next();
    }

    @Override
    public void remove() {
        wrapped.remove();
    }

    @Override
    public Iterator<T> iterator() {
        return wrapped;
    }
}
