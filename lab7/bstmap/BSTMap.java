package bstmap;

import org.w3c.dom.Node;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {

    int size = 0;

    /** Create Node Structure To Support BST Map */
    private class BSTNode {
        public K key;
        public V value;
        public BSTNode left;  // This Node is smaller than parent
        public BSTNode right;  // This Node is bigger than parent

        public BSTNode(K k, V v) {
            key = k;
            value = v;
        }
    }

    /** Create a root as an entry to the map */
    BSTNode root;

    /** Removes all of the maps of this tree */
    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    /** Check if key exist in BSTMap */
    @Override
    public boolean containsKey(K key) {
        // If it is a empty tree then contains nothing
        if (root == null) {
            return false;
        } else {
            return containsKey(root, key);
        }
    }

    /** Helper method to check key exist in map */
    private boolean containsKey(BSTNode node, K key) {
        // If key is not inside tree
        if (node.key == null) {
            return false;
        }
        // If key equals to current node then return true
        if (key.equals(node.key)) {
            return true;
        }
        // Else check if it is smaller than current node then go left
        else if (key.compareTo(node.key) < 0) {
            return containsKey(node.left, key);
        }
        // Else check if it is greater than current node then go right
        else {
            return containsKey(node.right, key);
        }
    }

    /** Get value of the key */
    @Override
    public V get(K key) {
        // If it is empty tree then can only get null
        if (root == null) {
            return null;
        } else {
            return get(root, key);
        }
    }

    /** Helper method to get key value pair */
    private V get(BSTNode node, K key) {
        // If not found
        if (node.key == null) {
            return null;
        }
        // If key equals to current node
        if (key.equals(node.key)) {
            return node.value;
        }

        // If given is smaller than current node then is left
        else if (key.compareTo(node.key) < 0) {
            return get(node.left, key);
        }

        // If given is bigger than current node then is right
        else {
            return get(node.right, key);
        }
    }

    /** Return size of the tree */
    @Override
    public int size() {
        return size;
    }


    /** Put key value pair into tree */
    @Override
    public void put(K key, V value) {
        if (root == null) {
            root = new BSTNode(key, value);
            size += 1;
        } else {
            put(root, key, value);
        }
    }

    /** Helper method to put key value pair */
    private void put(BSTNode node, K key, V value) {
        // If equals than current node then update value
        if (key.equals(node.key)) {
            node.value = value;
        }
        // If smaller than current node then put left
        else if (key.compareTo(node.key) < 0) {
            if (node.left == null) {
                node.left = new BSTNode(key, value);
                size += 1;
            } else {
                put(node.left, key, value);
            }
        }
        // If greater than current node then put right
        else {
            if (node.right == null) {
                node.right = new BSTNode(key, value);
                size += 1;
            } else {
                put(node.right, key, value);
            }
        }
    }

    /** Return all of the key in HashSet */
    @Override
    public Set<K> keySet() {
        if (root == null) {
            return new HashSet<>();
        } else {
            return keySet(root);
        }
    }

    /** Helper method to get keySet */
    private Set<K> keySet(BSTNode node) {
        HashSet<K> keys = new HashSet<>();
        // Iterate all of the key
        // Base Case: No node
        if (node == null) {
            return new HashSet<>();
        } else {
            // Add current node
            keys.add(node.key);
            // Add left node
            HashSet leftKeys = (HashSet) keySet(node.left);
            if (!leftKeys.isEmpty()) {
                keys.addAll(leftKeys);
            }
            HashSet rightKeys = (HashSet) keySet(node.right);
            if (!rightKeys.isEmpty()) {
                keys.addAll(rightKeys);
            }
        }
        return keys;
    }


    /** Remove node in BST and return its value */
    @Override
    public V remove(K key) {
        return remove(root, key);
    }

    private V remove (BSTNode node, K key) {
        // Somehow need to maintain balance (Find the node that have no child ?? left->right leaf or right -> left leaf)
        // If remove node that have single child
        // If remove node that have two child
    }
    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }
}
