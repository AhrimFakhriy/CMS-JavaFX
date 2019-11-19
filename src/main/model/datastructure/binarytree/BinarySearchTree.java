package main.model.datastructure.binarytree;

import java.util.Iterator;

public class BinarySearchTree<K extends Comparable<K>, T> implements Iterable<T> {
    private TreeNode<K, T> root;

    public BinarySearchTree() {
        this.root = null;
    }

    public void put(K key, T data) {
        if(root == null)
            root = new TreeNode<>(key, data);
        else
            root.put(key, data);
    }

    public T get(K key) {
        return root == null ? null : root.get(key);
    }

    public void clear() { root = null; }

    @Override
    public Iterator<T> iterator() {
        return new BinarySearchTreeIterator<>(root);
    }
}
