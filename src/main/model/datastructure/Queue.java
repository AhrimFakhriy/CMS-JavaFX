package main.model.datastructure;

public class Queue<T> extends LinkedList<T> {
    public Queue() {
        super();
    }

    public Queue(String name) {
        super(name);
    }

    public void enqueue(T item) {
        insertAtBack(item);
    }

    public T dequeue() {
        return removeFromFront();
    }
}
