package main.model.datastructure;

public class Node<T> {
    T data;
    Node<T> next;

    Node(T data) {
        this(data, null);
    }

    Node(T data, Node<T> next) {
        this.data = data;
        this.next = next;
    }

    public T getData() { return data; }
    public Node<T> getNext() { return next; }
}
