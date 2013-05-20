package io.ifar.goodies;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
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
     * Returns an AutoCloseableIterator that iterates over all the provided input iterators.
     *
     * If an Exception is thrown while closing an iterator, the other iterators are closed and the first Exception (only)
     * is rethrown.
     *
     * @see com.google.common.collect.Iterators#concat(java.util.Iterator[])
     * @param inputs
     * @param <T>
     * @return
     */
    public static <T> AutoCloseableIterator<T> concat(final AutoCloseableIterator<T>... inputs) {
        return concat(ImmutableList.copyOf(inputs));
    }

    /**
     * Returns an AutoCloseableIterator that iterates over all the provided input iterators.
     *
     * If an Exception is thrown while closing an iterator, the other iterators are closed and the first Exception (only)
     * is rethrown.
     *
     * @see com.google.common.collect.Iterators#concat(java.util.Iterator[])
     * @param inputs
     * @param <T>
     * @return
     */
    public static <T> AutoCloseableIterator<T> concat(final Collection<AutoCloseableIterator<T>> inputs) {
        return new AutoCloseableIterator<T>(
                com.google.common.collect.Iterators.concat(inputs.iterator()),
                new AutoCloseable() {
                    @Override
                    public void close() throws Exception {
                        Exception firstException = null;
                        for (AutoCloseableIterator<? extends T> i : inputs) {
                            try {
                                i.close();
                            } catch (Exception e) {
                                if (firstException == null) {
                                    firstException = e;
                                }
                            }
                        }
                        if (firstException != null) {
                            throw firstException;
                        }
                    }
                }
        );
    }

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
