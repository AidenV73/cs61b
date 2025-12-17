package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque {
    private Comparator<T> comparator;

    public MaxArrayDeque(Comparator<T> c){
        super();
        comparator = c;
    }

    public T max(){
        return max(comparator);
    }

    public T max(Comparator<T> c) {
        if (isEmpty()) {
            return null;
        }

        T maxItem = (T) get(0);
        for (int i = 1; i < size; i += 1){
            T curr = (T) get(i);
            if (comparator.compare(maxItem, curr) < 0){
                maxItem = curr;
            }
        }
        return maxItem;
    }
}