package utils.collection.list.child;

import utils.collection.list.AbstractList;
import utils.objects.ObjectUtils;

import java.util.Iterator;

/**
 * 双向链表
 * @param <E>
 */
public class DoubleLinkedList<E> extends AbstractList<E> {

    private static class Node<E>{
        Node<E> pre;
        Node<E> next;
        E element;

        public Node(Node<E> pre, Node<E> next, E element) {
            this.pre = pre;
            this.next = next;
            this.element = element;
        }
    }

    //头结点
    private Node<E> head;
    //尾结点
    private Node<E> last;

    public DoubleLinkedList() {

    }

    public DoubleLinkedList(E[] objects) {
        if (!ObjectUtils.isEmpty(objects)){
            for (int i = 0; i < objects.length; i++) {
                add(objects[i]);
            }
        }
    }

    @Override
    public boolean add(E element) {
        if (ObjectUtils.isEmpty(element)) return false;
        Node<E> newNode = new Node<>(null,null,element);
        if (isEmpty()){
            head = newNode;
            last = head;
        }else {
            Node<E> temp = last;
            temp.next = newNode;
            newNode.pre = temp;
            last = newNode;
        }
        ++size;
        return true;
    }

    @Override
    public boolean remove(E element) {
        if (ObjectUtils.isEmpty(element)) return false;
        Node<E> removeNode = getNode(element);
        if (removeNode == head){
            if (removeNode.next != null){
                Node<E> next = removeNode.next;
                head = next;
                removeNode.next = null;
                head.pre = null;
            }else {
                //如果只有一个头结点
                head = null;
                last = null;
            }

        }else {
            Node<E> pre = removeNode.pre;
            if (removeNode.next != null){
                pre.next = removeNode.next;
                removeNode.next.pre = pre;
            }else {
                pre.next = null;
            }
        }
        --size;
        return true;
    }

    @Override
    public E remove(int index) {
        if (index >= size || isEmpty()) return null;
        Node<E> removeNode = null;
        if (index <= (size - 2)){
            //如果删除的节点在前半段
            removeNode = head;
            if (index == 0){
                //如果删除的是head节点
                if (removeNode.next != null){
                    head = removeNode.next;
                    removeNode.next = null;
                    head.pre = null;
                }else {
                    head = null;
                    last = null;
                }
            }else {
                removeNode = removeNode.next;
                for (int i = 1; i < index;++i){
                    removeNode = removeNode.next;
                }
                Node<E> pre = removeNode.pre;
                pre.next = removeNode.next;
                removeNode.next.pre = pre;
            }
        }else {
            //删除的节点在后半段
            removeNode = last;
            if (index == size - 1){
                //如果删除的是last节点
                removeNode.pre.next = null;
                last = removeNode.pre;
                removeNode.pre = null;
            }else {
                removeNode = removeNode.pre;
                index = (size - index) - 2;
                for (int j = 0; index > j; --j){
                    removeNode = removeNode.pre;
                }
                removeNode.pre.next = removeNode.next;
                removeNode.next.pre = removeNode.pre;
            }
        }
        removeNode.next = null;
        removeNode.pre = null;
        --size;
        return removeNode.element;
    }

    private Node<E> getNode(E element){
        if (ObjectUtils.isEmpty(element)) return null;
        Node<E> node = head;
        while (node != null){
            if (element.equals(node.element)) return node;
            node = node.next;
        }
        return null;
    }

    @Override
    public E get(int index) {
        if (index >= size) return null;
        Node<E> node;
        if (index <= (size / 2)){
            node = head;
            for (int i = 0; i < index; i++) {
                node = node.next;
            }
        }else {
            node = last;
            index = (size - index) - 1;
            for (int j = 0; index > j; --index){
                node = node.pre;
            }
        }
        return node.element;
    }

    @Override
    public void reversal() {
        if (isEmpty() || size == 1) return;
        Node<E> node = head.next,
                pre = head;
        last = pre;
        while (node != null){
            Node<E> temp = node.next;
            pre.next = pre.pre;
            pre.pre = node;
            node.next = pre;
            pre = node;
            node = temp;
        }
        head = pre;
    }

    @Override
    public boolean contains(E element) {
        return getNode(element) != null;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public void clear() {
        head = null;
        last = null;
        size = 0;
    }

    @Override
    public Iterator<E> iterator() {
        return new LinkedIterator<>(head);
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

    public E first(){
        return head == null ? null : head.element;
    }

    public E last(){
        return last == null ? null : last.element;
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
