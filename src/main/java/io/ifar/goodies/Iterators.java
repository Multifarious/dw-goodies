package io.ifar.goodies;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 */
public class Iterators {

    public static <T> IterableIterator<T> iterable(final Iterator<T> iterator) {
        return new IterableIterator<T>() {
            @Override
            public Iterator<T> iterator() {
                return iterator;
            }

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public T next() {
                return iterator.next();
            }

            @Override
            public void remove() {
                iterator.remove();
            }
        };
    }

    /**
     * Takes one of the first n items of the iterator, returning that item an a new Iterator which excludes that item.
     * Remove operation is not supported on the first n items of the returned iterator. For the remaining items,
     * remove delegates to the original Iterator.
     *
     * Useful to obtain somewhat random selection with bounded performance risk when iterator may contain a large number of items.
     *
     * @param iterator
     * @param n
     * @param <T>
     * @return Pair of selected item (or null if iterator was empty or n <= 0) and new Iterator over the remaining items.
     */
    public static <T> Pair<T,IterableIterator<T>> takeOneFromTopN(Iterator<T> iterator, int n) {
        if (!iterator.hasNext() || n < 1) {
            return new Pair<T,IterableIterator<T>>(null,iterable(iterator));
        }
        List<T> firstN = new ArrayList<>(n);
        int i = 0;
        while(i < n && iterator.hasNext()) {
            firstN.add(iterator.next());
            i++;
        }

        return new Pair<>(
                firstN.remove(ThreadLocalRandom.current().nextInt(0,firstN.size())),
                iterable(
                        com.google.common.collect.Iterators.concat(
                                com.google.common.collect.Iterators.unmodifiableIterator(firstN.iterator()),
                                iterator))
        );
    }
}
