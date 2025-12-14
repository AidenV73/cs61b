package randomizedtest;

import afu.org.checkerframework.checker.igj.qual.I;
import edu.princeton.cs.algs4.StdRandom;
import org.checkerframework.checker.units.qual.A;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE

    @Test
    /** Add element to both the list and remove test if there are same */
    public void testAddElement(){
        AListNoResizing<Integer> nrLst = new AListNoResizing<>();
        BuggyAList<Integer> bugLst = new BuggyAList<>();
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
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> B = new BuggyAList<>();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 3);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                B.addLast(randVal);
                assertEquals(L.getLast(), B.getLast());
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

}
