package main.model.datastructure.binarytree;

import main.model.datastructure.Stack;

import java.util.Iterator;

class BinarySearchTreeIterator<K extends Comparable<K>, T> implements Iterator<T> {
    private Stack<TreeNode<K, T>> stack;

    BinarySearchTreeIterator(TreeNode<K, T> root) {
        this.stack = new Stack<>();

        while(root != null) {
            stack.push(root);
            root = root.left;
        }
    }

    @Override
    public boolean hasNext() {
        return !stack.isEmpty();
    }

    @Override
    public T next() {
        TreeNode<K, T> current = stack.pop();
        TreeNode<K, T> right = current.right;

        while (right != null) {
            stack.push(right);
            right = right.left;
        }

        return current.data;
    }
}
