package main.model.datastructure;

import java.util.Iterator;

public class LinkedList<T> implements Iterable<T>{
    private Node<T> firstNode;
    private Node<T> lastNode;
    private Node<T> currNode;
    private String name;

    public LinkedList() {
        this("List");
    }

    public LinkedList(String name) {
        this.name = name;
        firstNode = lastNode = currNode = null;
    }


    public boolean isEmpty() {
        return firstNode == null;
    }

    public void insertAtFront(T item) {
        if(isEmpty())
            firstNode = lastNode = new Node<>(item);
        else
            firstNode = new Node<>(item, firstNode);
    }

    public T removeFromFront() throws UnsupportedOperationException {
        if(isEmpty()) throw new UnsupportedOperationException("Empty List!");

        T removingItem = firstNode.data;

        if(firstNode.equals(lastNode)) {
            firstNode = lastNode = null;
        } else {
            firstNode = firstNode.next;
        }

        return removingItem;
    }

    public void insertAtBack(T item) {
        if(isEmpty())
            firstNode = lastNode = new Node<>(item);
        else
            lastNode = lastNode.next = new Node<>(item);
    }

    public T removeFromBack() throws UnsupportedOperationException {
        if(isEmpty()) throw new UnsupportedOperationException("Empty List!");

        T removingItem = lastNode.data;

        if(firstNode.equals(lastNode)) {
            firstNode = lastNode = null;
        } else {
            Node<T> current = firstNode;

            while(current.next != lastNode) {
                current = current.next;
            }

            lastNode = current;
            lastNode.next = null;
        }

        return removingItem;
    }

    public T getNext() {
        if(currNode == lastNode) return null;

        if(currNode == null)
            currNode = firstNode;
        else
            currNode = currNode.next;

        return currNode.data;
    }

    public T getFirst() { return firstNode.data; }
    public T getLast() { return lastNode.data; }
    public void clear() { firstNode = lastNode = currNode = null; }
    Node<T> getFirstNode() { return firstNode; }


    @Override
    public Iterator<T> iterator() {
        return new LinkedListIterator<>(this);
    }
}
