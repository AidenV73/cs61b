package deque;

public class ArrayDeque<T> implements Deque<T>{
    // Initialize items, size，middle and array
    T []items;
    int size;
    int first; // Points to the first element
    int rear; // Points to the last element
    public ArrayDeque(){
        items = (T[]) new Object[8];
        size = 0;
        first = items.length / 2;
        rear = first;
    }
    public int movePtr(int ptr){
        return (ptr + items.length) % items.length;
    }
    /** Adds an item of type T to the back of the deque.  (O(1)) */
    @Override
    public void addLast(T item){
        if (size == items.length){
            resize(size * 2);
        }
        rear = movePtr(rear + 1);
        if (isEmpty()){
            first = rear;
        }
        items[rear] = item;
        size += 1;
    }

    /** Adds an item of type T to the front of the deque.  (O(1)) */
    @Override
    public void addFirst(T item){
        if (size == items.length){
            resize(size * 2);
        }
        first = movePtr(first - 1);
        if (isEmpty()){
            rear = first;
        }
        items[first] = item;
        size += 1;
    }

    /** Gets the item at the given index, where 0 is the front, 1 is the next item, and so forth. If no such item exists, returns null. */
    @Override
    public T get(int i){
        int index = movePtr(first + i);
        return items[index];
    }

    /** Removes and returns the item at the last of the deque. If no such item exists, returns null. */
    @Override
    public T removeLast(){
        if (!isEmpty()) {
            T tmp = items[rear];
            items[rear] = null;
            size -= 1;
            rear = movePtr(rear - 1);
            return tmp;
         } return null;
    }

    /** Removes and returns the item at the front of the deque. If no such item exists, returns null. */
    @Override
    public T removeFirst(){
        if (!isEmpty()) {
            T tmp = items[first];
            items[first] = null;
            size -= 1;
            first = movePtr(first + 1);
            return tmp;
        } return null;

    }

    /** Returns the number of items in the deque. (O(1)) */
    @Override
    public int size(){
        return size;
    }

    /** Resize if current size too small (do size * 2) / size too big (do size / 4) */
    public void resize(int capacity){
        T[] a = (T[]) new Object[capacity];

        for (int i = 0; i < size; i += 1){
            a[i] = get(i);
        }
        first = 0;
        rear = size - 1;
        items = a;
    }

    @Override
    public void printDeque(){
        if (isEmpty()){
            return;
        }
        for(int i = first; i < size; i += 1){
            System.out.println(get(i));
        }
    }
}

