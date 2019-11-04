package main.model.datastructure;

public class Stack<T> extends LinkedList<T> {
    public Stack() {
        super();
    }

    public Stack(String name) {
        super(name);
    }

    public void push(T item) {
        insertAtFront(item);
    }

    public T pop() {
        return removeFromFront();
    }

    public T peek() {
        return getFirst();
    }
}
