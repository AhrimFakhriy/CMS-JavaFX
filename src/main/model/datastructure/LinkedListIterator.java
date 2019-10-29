package main.model.datastructure;

import java.util.Iterator;

public class LinkedListIterator<T> implements Iterator<T> {
    private LinkedList<T> list;
    private Node<T> current;

    LinkedListIterator(LinkedList<T> list) {
        this.list = list;
        current = null;
    }

    @Override
    public boolean hasNext() {
        if(list.isEmpty()) return false;

        if(current == null)
            return (list.getFirstNode() != null);

        return current.next != null;
    }

    @Override
    public T next() {
        if(current == null)
            current = list.getFirstNode();
        else
            current = current.next;

        return current.data;
    }
}
