package deque;

public class ArrayDeque<T> {
    // Initialize items, size，middle and array
    T []items;
    int size;
    int nextFirst;
    int nextLast;
    public ArrayDeque(){
        items = (T[]) new Object[8];
        size = 0;
        nextFirst = items.length / 2;
        nextLast = items.length / 2;
    }
    public int movePtr(int ptr){
        return (ptr + items.length) % items.length;
    }
    /** Adds an item of type T to the back of the deque.  (O(1)) */
    public void addLast(T item){
        if (size == items.length){
            resize(size * 2);
        }
        items[nextLast] = item;
        if (nextFirst == nextLast){
            nextFirst = movePtr(nextFirst - 1);
        }
        nextLast = movePtr(nextLast + 1);
        size += 1;
    }
    /** Adds an item of type T to the front of the deque.  (O(1)) */
    public void addFirst(T item){
        if (size == items.length){
            resize(size * 2);
        }
        items[nextFirst] = item;
        if (nextFirst == nextLast) {
            nextLast = movePtr(nextLast + 1);
        }
        nextFirst = movePtr(nextFirst - 1);
        size += 1;
    }
    /** Gets the item at the given index, where 0 is the front, 1 is the next item, and so forth. If no such item exists, returns null. */
    public T get(int i){
        int index = movePtr(nextFirst + i + 1);
        return items[index];
    }
    /** Removes and returns the item at the last of the deque. If no such item exists, returns null. */
    public T removeLast(){
        if (!isEmpty()) {
            nextLast = movePtr(nextLast - 1);
            T tmp = items[nextLast];
            items[nextLast] = null;
            size -= 1;
            if (size < items.length / 4) {
                resize(items.length / 2);
            }
            return tmp;
        } return null;
    }
    /** Removes and returns the item at the front of the deque. If no such item exists, returns null. */
    public T removeFirst(){
        if (!isEmpty()) {
            nextFirst = movePtr(nextFirst + 1);
            T tmp = items[nextFirst];
            items[nextFirst] = null;
            size -= 1;
            if (size < items.length / 4) {
                resize(items.length / 2);
            }
            return tmp;
        } return null;

    }
    /** Returns the number of items in the deque. (O(1)) */
    public int size(){
        return size;
    }
    /** Returns true if deque is empty, false otherwise. */
    public boolean isEmpty(){
        return size() == 0;
    }
    /** Resize if current size too small (do size * 2) / size too big (do size / 4) */
    public void resize(int capacity){
        T[] a = (T[]) new Object[capacity];

        for (int i = 0; i < size; i += 1){
            a[i] = get(i);
        }
        nextFirst = capacity - 1; // Back of the line array
        nextLast = size;
        items = a;
    }
}

