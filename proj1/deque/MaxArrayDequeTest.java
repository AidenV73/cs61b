package deque;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Comparator;
import java.util.Optional;

public class MaxArrayDequeTest {
    private static class IntCmp implements Comparator<Integer> {
        @Override
        public int compare(Integer a, Integer b) {
            return a - b;
        }
    }

    private static class StrLengthCmp implements Comparator<String> {
        @Override
        public int compare(String a, String b) {
            return a.length() - b.length();
        }
    }

    @Test
    public void MaxIntTest() {
        IntCmp intcmp = new IntCmp();
        MaxArrayDeque<Integer> boo = new MaxArrayDeque<>(intcmp);
        for (int i = 0; i < 10; i += 1) {
            boo.addLast(i);
        }
        int maxItem = boo.max();
        assertEquals(9, maxItem);
    }

    @Test
    public void MaxStrLengthTest() {
        StrLengthCmp c = new StrLengthCmp();
        MaxArrayDeque<String> foo = new MaxArrayDeque<>(c);
        foo.addLast("a");
        foo.addLast("aa");
        foo.addLast("aaa");
        foo.addLast("aaaa");
        foo.addLast("aaaaa");
        String maxItem = "aaaaa";
        assertEquals("aaaaa", maxItem);
    }

    @Test
    public void AlphabeticalTest() {
        Comparator<String> c = String::compareTo;
        MaxArrayDeque<String> foo = new MaxArrayDeque<>(c);
        foo.addLast("aa");
        foo.addLast("ba");
        foo.addLast("ca");
        foo.addLast("da");
        foo.addLast("ea");
        String maxItem = foo.max();
        assertEquals("ea", maxItem);
    }
}
