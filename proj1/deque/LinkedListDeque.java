package deque;

public class LinkedListDeque<T> {
    /**
     * Construct a "naked" doubly linked list
     */
    private class Node {
        public T item;
        public Node prev;
        public Node next;

        public Node(Node p, T t, Node n) {
            item = t;
            prev = p;
            next = n;
        }
    }

    //Initialize lst, sentinel and size
    Node sentinel = new Node(null, null, null);
    int size = 0;

    /**
     * Adds an item of type T to the back of the deque. (No recursion or loop) (O(1))
     */
    public void addLast(T item) {
        if (!isEmpty()) {
            Node lst = new Node(sentinel.prev, item, sentinel);
            sentinel.prev.next = lst;
            sentinel.prev = lst;
        } else { // Adding the first node
            Node lst = new Node(sentinel, item, sentinel);
            sentinel.next = lst;
            sentinel.prev = lst;
        }
        size += 1;
    }

    /**
     * Adds an item of type T to the first of the deque (No recursion or loop) (O(1))
     */
    public void addFirst(T item) {
        if (!isEmpty()) {
            Node lst = new Node(sentinel, item, sentinel.next);
            sentinel.next.prev = lst;
            sentinel.next = lst;
        } else {
            Node lst = new Node(sentinel, item, sentinel);
            sentinel.next = lst;
            sentinel.prev = lst;
        }
        size += 1;
    }

    /**
     * Returns true if deque is empty, false otherwise.
     */
    public boolean isEmpty() {
        if (size == 0) {
            return true;
        }
        return false;
    }

    /**
     * Returns the number of items in the deque. (O(1))
     */
    public int size() {
        return size;
    }

    /**
     * Prints the items in the deque from first to last, separated by a space. Once all the items have been printed, print out a new line.
     */
    public void printDeque() {
        for (Node ptr = sentinel.next; ptr != sentinel; ptr = ptr.next) {
            System.out.println(ptr.item + " ");
        }
    }

    /**
     * Removes and returns the item at the back of the deque. If no such item exists, returns null.
     */
    public T removeLast() {
        if (!isEmpty()) {
            T item = sentinel.prev.item;
            sentinel.prev = sentinel.prev.prev;
            sentinel.prev.next = sentinel;
            size -= 1;
            return item;
        }
        return null;
    }

    /**
     * Removes and returns the item at the front of the deque. If no such item exists, returns null.
     */
    public T removeFirst() {
        if (!isEmpty()) {
            Node first = sentinel.next;
            T item = first.item;
            sentinel.next = first.next;
            sentinel.next.prev = sentinel;
            size -= 1;
            return item;
        }
        return null;
    }

    /**
     * Gets the item at the given index, where 0 is the front, 1 is the next item, and so forth. If no such item exists, returns null. (Use Iteration)
     */
    public T get(int index) {
        if (index < 0 ||
            index >= size){
                return null;
            }
        Node ptr = sentinel.next;
        for (int i = 0; i < index; i += 1) {
            ptr = ptr.next;
        }
        size -= 1;
        return ptr.item;
    }
}
