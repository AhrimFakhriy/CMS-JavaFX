package main.model.datastructure;

public class BinarySearchTree<T> {
    private Node<T> root;

    public void put(String key, T data) {
        if(root == null)
            root = new Node<>(key, data);
        else
            root.put(key, data);
    }

    public T get(String key) {
        return root == null ? null : root.get(key);
    }

    private static class Node<T> {
        private String key;
        private T data;
        private Node<T> left, right;

        Node(String key, T data) {
            this.key = key;
            this.data = data;
        }

        void put(String key, T data) {
            if(key.compareTo(this.key) < 0) {
                if (left == null)
                    new Node<>(key, data);
                else
                    left.put(key, data);

            } else if(key.compareTo(this.key) > 0){
                if(right == null)
                    new Node<>(key, data);
                else
                    right.put(key, data);
            } else {
                this.data = data;
            }
        }

        T get(String key) {
            if(this.key.equals(key))
                return data;

            if(key.compareTo(this.key) < 0)
                return left == null ? null : left.get(key);

            return right == null ? null : right.get(key);
        }
    }

}
