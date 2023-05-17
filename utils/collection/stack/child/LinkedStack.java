package utils.collection.stack.child;

import utils.collection.list.child.DoubleLinkedList;
import utils.collection.stack.AbstractStack;

import java.util.Iterator;

public class LinkedStack<E> extends AbstractStack<E> {

    private DoubleLinkedList<E> list;

    public LinkedStack() {
        list = new DoubleLinkedList<>();
    }

    public LinkedStack(E... objects) {
        list = new DoubleLinkedList<>(objects);
    }

    @Override
    public boolean add(E element) {
        return list.add(element);
    }

    @Override
    public boolean remove(E element) {
        return list.remove(element);
    }

    @Override
    public boolean contains(E element) {
        return list.contains(element);
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public void clear() {
        list.clear();
    }

    @Override
    public Iterator<E> iterator() {
        return list.iterator();
    }

    @Override
    public boolean push(E element) {
        return list.add(element);
    }

    @Override
    public E pop() {
        if (isEmpty()) throw new RuntimeException("Stack element size == 0");
        return list.remove(list.size() - 1);
    }

    @Override
    public E peekFirst() {
        return list.first();
    }

    @Override
    public E peekLast() {
        return list.last();
    }

    @Override
    public String toString() {
        return list.toString();
    }
}
