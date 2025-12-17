import deque.ArrayDeque;
import deque.LinkedListDeque;
import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;

import static org.junit.Assert.*;

public class ArrayDequeTest {
    // YOUR TESTS HERE

    @Test
    /** Add element to both the list and remove test if there are same */
    public void testAddElement(){
        LinkedListDeque<Integer> nrLst = new LinkedListDeque<>();
        ArrayDeque<Integer> bugLst = new ArrayDeque<>();
        // Add element into both list
        for (int i = 0; i < 3; i += 1){
            nrLst.addLast(i + 1);
            bugLst.addLast(i + 1);
        }

        // Remove element and check if the result are same
        for (int i = 0; i < 3; i+= 1){
            assertEquals(nrLst.removeLast(), bugLst.removeLast());
        }
    }

    @Test
    /** Randomly calls addLast ,resize and removeLast on an AListNoResizing for a total N times to one of these functions */
    public void testRandomizedCall(){
        LinkedListDeque<Integer> L = new LinkedListDeque<>();
        ArrayDeque<Integer> B = new ArrayDeque<>();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 3);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                B.addLast(randVal);
                assertEquals(L.get(L.size()), B.get(B.size()));
            } else if (operationNumber == 1) {
                // size
                int size = L.size();
            } else{
                // removeLast
                if (L.size() > 0 &&
                        B.size() > 0){
                    int lastL = L.removeLast();
                    int lastB = B.removeLast();
                    assertEquals(lastL, lastB);
                }
            }
        }
    }
    @Test
    /** Adds a few things to the list, checking isEmpty() and size() are correct,
     * finally printing the results.
     *
     * && is the "and" operation. */
    public void addIsEmptySizeTest() {

        ArrayDeque<String> lld1 = new ArrayDeque<>();

        assertTrue("A newly initialized LLDeque should be empty", lld1.isEmpty());
        lld1.addFirst("front");

        // The && operator is the same as "and" in Python.
        // It's a binary operator that returns true if both arguments true, and false otherwise.
        assertEquals(1, lld1.size());
        assertFalse("lld1 should now contain 1 item", lld1.isEmpty());

        lld1.addLast("middle");
        assertEquals(2, lld1.size());

        lld1.addLast("back");
        assertEquals(3, lld1.size());
    }

    @Test
    /** Adds an item, then removes an item, and ensures that dll is empty afterwards. */
    public void addRemoveTest() {

        ArrayDeque<Integer> lld1 = new ArrayDeque<>();
        // should be empty
        assertTrue("lld1 should be empty upon initialization", lld1.isEmpty());

        lld1.addFirst(10);
        // should not be empty
        assertFalse("lld1 should contain 1 item", lld1.isEmpty());

        lld1.removeFirst();
        // should be empty
        assertTrue("lld1 should be empty after removal", lld1.isEmpty());
    }

    @Test
    /** Add 5 item and remove */
    public void addFiveRemoveTest() {
        ArrayDeque<Integer> lld = new ArrayDeque<>();
        for (int i = 1; i <= 5; i += 1) {
            lld.addFirst(i);
        }
        for (int i = 1; i <= 5; i += 1) {
            int item = lld.removeLast();
            assertEquals(i, item);
        }
    }

    @Test
    /* Tests removing from an empty deque */
    public void removeEmptyTest() {

        ArrayDeque<Integer> lld1 = new ArrayDeque<>();
        lld1.addFirst(3);

        lld1.removeLast();
        lld1.removeFirst();
        lld1.removeLast();
        lld1.removeFirst();

        int size = lld1.size();
        String errorMsg = "  Bad size returned when removing from empty deque.\n";
        errorMsg += "  student size() returned " + size + "\n";
        errorMsg += "  actual size() returned 0\n";

        assertEquals(errorMsg, 0, size);
    }

    @Test
    /* Check if you can create LinkedListDeques with different parameterized types*/
    public void multipleParamTest() {

        ArrayDeque<String>  lld1 = new ArrayDeque<>();
        ArrayDeque<Double>  lld2 = new ArrayDeque<>();
        ArrayDeque<Boolean> lld3 = new ArrayDeque<>();

        lld1.addFirst("string");
        lld2.addFirst(3.14159);
        lld3.addFirst(true);

        String s = lld1.removeFirst();
        double d = lld2.removeFirst();
        boolean b = lld3.removeFirst();
    }

    @Test
    /* check if null is return when removing from an empty LinkedListDeque. */
    public void emptyNullReturnTest() {

        LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>();

        boolean passed1 = false;
        boolean passed2 = false;
        assertEquals("Should return null when removeFirst is called on an empty Deque,", null, lld1.removeFirst());
        assertEquals("Should return null when removeLast is called on an empty Deque,", null, lld1.removeLast());

    }

    @Test
    /* Add large number of elements to deque; check if order is correct. */
    public void bigLLDequeTest() {

        ArrayDeque<Integer> lld1 = new ArrayDeque<Integer>();
        for (int i = 0; i < 100000; i++) {
            lld1.addLast(i);
        }

        for (double i = 0; i < 50000; i ++) {
            assertEquals("Should have the same value", i, (double) lld1.removeFirst(), 0.0);
        }

        for (double i = 99999; i > 50000; i--) {
            assertEquals("Should have the same value", i, (double) lld1.removeLast(), 0.0);
        }    }

    @Test
    /** Add and remove and resize */
    public void resizeTest(){
        ArrayDeque<Integer> lld1 = new ArrayDeque<>();
        for (int i = 0; i < 3; i += 1){
            lld1.addLast(i);
        }
        lld1.resize(4);
    }
}
