package utils.collection.list.child;

import utils.collection.list.AbstractList;
import utils.objects.ObjectUtils;

import java.util.Iterator;

/**
 * 单向链表实现的List
 * @param <E>
 */
public class SingleLinkedList<E> extends AbstractList<E> {

    private static class Node<E>{
        E element;
        Node<E> next;

        public Node(E element, Node<E> next) {
            this.element = element;
            this.next = next;
        }
    }

    private Node<E> head;

    @Override
    public boolean add(E element) {
        if (ObjectUtils.isEmpty(element)) return false;
        Node<E> newNode = new Node<>(element,null);
        if (isEmpty()){
            head = newNode;
        }else {
            Node<E> node = head;
            while (node.next != null) node = node.next;
            node.next = newNode;
        }
        ++size;
        return true;
    }

    @Override
    public boolean remove(E element) {
        if (isEmpty() || ObjectUtils.isEmpty(element)) return false;
        Node<E> node = head,pre = null;
        while (node.next != null){
            if (element.equals(node.element)){
                //如果找到需要删除的节点
                if (node == head){
                    //如果删除的节点是头结点
                    head = null;
                }else {
                    if (node.next != null){
                        //如果删除的节点后面还有节点
                        pre.next = node.next;
                    }else {
                        //删除的是末尾节点
                        pre.next = null;
                    }
                }
                --size;
                return true;
            }
            pre = node;
            node = node.next;
        }
        return false;
    }

    @Override
    public E remove(int index) {
        if (index >= size) return null;
        Node<E> removeNode = head;
        Node<E> pre = null;
        for (int i = 0; i < index; ++i){
            pre = removeNode;
            removeNode = removeNode.next;
        }
        if (removeNode.next != null){
            pre.next = removeNode.next;
        }else {
            pre.next = null;
        }
        --size;
        return removeNode.element;
    }

    @Override
    public E get(int index) {
        if (index >= size || isEmpty()) return null;
        Node<E> node = head;
        for (int i = 0; i < index; ++i){
            node = node.next;
        }
        return node.element;
    }

    @Override
    public void reversal() {
        if (isEmpty() || size == 1) return;
        Node<E> node = head.next,
                pre = head,
                prePre = null;
        while (node != null){
            Node<E> temp = node.next;
            node.next = pre;
            pre.next = prePre;
            prePre = pre;
            pre = node;
            node = temp;
        }
        head = pre;
    }

    @Override
    public boolean contains(E element) {
        if (isEmpty() || ObjectUtils.isEmpty(element)) return false;
        Node<E> node = head;
        while (node.next != null){
            if (element.equals(node.element)) return true;
            node = node.next;
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public void clear() {
        head = null;
        size = 0;
    }

    @Override
    public Iterator<E> iterator() {
        return new LinkedIterator<E>(head);
    }

    private static class LinkedIterator<E> implements Iterator<E>{

        private Node<E> node;

        public LinkedIterator(Node<E> node) {
            this.node = node;
        }

        @Override
        public boolean hasNext() {
            return node != null;
        }

        @Override
        public E next() {
            E element = node.element;
            node = node.next;
            return element;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        Iterator<E> iterator = iterator();
        while (iterator.hasNext()){
            sb.append(iterator.next() + ",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("]");
        return sb.toString();
    }
}
