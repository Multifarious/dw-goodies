package io.ifar.goodies;

import org.skife.jdbi.v2.ResultIterator;

import java.lang.AutoCloseable;import java.lang.Exception;import java.lang.Iterable;import java.lang.Override;import java.util.Iterator;

/**
 * Wraps Iterators, providing AutoCloseable and Iterable interfaces.
 *
 * Particularly intended for use with JDBI's ResultIterator; failing to exhaust
 * such an iterator will leak a database connection.
 */
public class AutoCloseableIterator<T> implements Iterator<T>, Iterable<T>, AutoCloseable {
    private final Iterator<T> wrapped;
    private final AutoCloseable closeHook;

    public AutoCloseableIterator(final Iterator<T> wrapped) {
        this.wrapped = wrapped;
        if (wrapped instanceof ResultIterator) {
            closeHook = new AutoCloseable() {
                @Override
                public void close() throws Exception {
                    ((ResultIterator<T>) wrapped).close();
                }
            };
        } else {
            closeHook = new AutoCloseable() {
                @Override
                public void close() throws Exception {
                    //do nothing
                }
            };
        }
    }

    @Override
    public void close() throws Exception {
        closeHook.close();
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
