package IntList;

import static org.junit.Assert.*;
import org.junit.Test;

public class SquarePrimesTest {

    /**
     * Here is a test for isPrime method. Try running it.
     * It passes, but the starter code implementation of isPrime
     * is broken. Write your own JUnit Test to try to uncover the bug!
     */
    @Test
    public void testSquarePrimesSimple() {
        IntList lst = IntList.of(14, 15, 16, 17, 18);
        boolean changed = IntListExercises.squarePrimes(lst);
        assertEquals("14 -> 15 -> 16 -> 289 -> 18", lst.toString());
        assertTrue(changed);
    }
    @Test
    public void testSquarePrimesSimple2(){
        IntList lst2 = IntList.of(13, 3, 12, 16, 23);
        boolean changed2 = IntListExercises.squarePrimes(lst2);
        assertEquals("169 -> 9 -> 12 -> 16 -> 529", lst2.toString());
        assertTrue(changed2);
    }
}

