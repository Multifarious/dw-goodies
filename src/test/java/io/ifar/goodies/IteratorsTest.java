package io.ifar.goodies;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 */
public class IteratorsTest {
    private static final List<Integer> BIG_LIST = new ArrayList<Integer>(1000);
    static {
        for (int i = 1; i <= 1000; i ++)
            BIG_LIST.add(i);
    }

    @Test
    public void topNReturnsNullWhenIteratorEmpty() {
        List<String> l = new ArrayList<>(0);
        Pair<String, ? extends Iterator<String>> result = Iterators.takeOneFromTopN(l.iterator(), 5);
        Assert.assertNull(result.left);
        assertEquals(l, ImmutableList.copyOf(result.right));
    }

    @Test
    public void topNReturnsNullWhenNIsLess1() {
        List<String> l = Lists.newArrayList("foo","bar");
        Pair<String,? extends Iterator<String>> result = Iterators.takeOneFromTopN(l.iterator(), 0);
        Assert.assertNull(result.left);
        assertEquals(l, ImmutableList.copyOf(result.right));
    }

    @Test
    public void topNReturnsFirstWhenNIs1() {
        Pair<Integer,? extends Iterator<Integer>> result = Iterators.takeOneFromTopN(BIG_LIST.iterator(), 1);
        assertEquals(Integer.valueOf(1), result.left);
        assertEquals(BIG_LIST.subList(1, 1000), ImmutableList.copyOf(result.right));
    }

    @Test
    /**
     * This is the basic test of expected behavior
     */
    public void topNRespectsNAndOmitsSelectedFromIterator() {
        int timesFirstItemWasReturned = 0;
        for(int i = 1; i <= 1000; i++ ) {
            Pair<Integer,? extends Iterator<Integer>> result = Iterators.takeOneFromTopN(BIG_LIST.iterator(), i);
            assertTrue(String.format("Expected result from top %d but got index %d", i, result.left), result.left >= 1 && result.left <= i);
            Set<Integer> remainingItems = ImmutableSet.copyOf(result.right);
            assertFalse("Selected item should not appear in the remaining iterator.", remainingItems.contains(result.left));
            assertEquals(999,remainingItems.size());
            if (result.left == i) {
                timesFirstItemWasReturned++;
            }
        }
        assertTrue(timesFirstItemWasReturned < 200); //200 is not magical; just conservative swag
    }

    @Test
    public void topNWithNGreaterIteratorSize() {
        List<String> l = Lists.newArrayList("foo","bar");
        Pair<String,? extends Iterator<String>> result = Iterators.takeOneFromTopN(l.iterator(), 5);
        if ("foo".equals(result.left)) {
            assertEquals(ImmutableList.of("bar"), ImmutableList.copyOf(result.right));
        } else if ("bar".equals(result.left)) {
            assertEquals(ImmutableList.of("foo"), ImmutableList.copyOf(result.right));
        } else {
            fail("Either 'foo' or 'bar' should have been selected.");
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void topNCannotRemoveFromFirstN() {
        List<String> l = Lists.newArrayList("foo","bar","baz");
        Pair<String,? extends Iterator<String>> result = Iterators.takeOneFromTopN(l.iterator(), 2);
        result.right.next(); //"foo" or "bar"
        result.right.remove(); //cannot
        assertEquals(Lists.newArrayList("foo","bar","baz"), l);
    }

    @Test
    public void topNCanRemoveFromRemaining() {
        List<String> l = Lists.newArrayList("foo","bar","baz");
        Pair<String,? extends Iterator<String>> result = Iterators.takeOneFromTopN(l.iterator(), 2);
        result.right.next(); //"foo" or "bar"
        result.right.next(); //"baz"
        result.right.remove();
        assertFalse(l.contains("baz"));
    }
}
