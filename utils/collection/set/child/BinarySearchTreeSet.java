package utils.collection.set.child;

import utils.collection.Collection;
import utils.collection.set.AbstractSet;
import utils.collection.map.Map;
import utils.collection.map.tree.BinarySearchTreeMap;

import java.util.Iterator;

/**
 * 使用二叉搜索树实现的Set
 * @param <E>
 */

public class BinarySearchTreeSet<E> extends AbstractSet<E> {

    private Map<E,Object> map;

    /**
     * 默认Value对象
     */
    private static final Object PRESENT = new Object();

    public BinarySearchTreeSet() {
        map = new BinarySearchTreeMap<>();
    }

    public BinarySearchTreeSet(E... elements){
        this();
        for (int i = 0; i < elements.length; i++) {
            add(elements[i]);
        }
    }

    public BinarySearchTreeSet(Collection<E> collection){
        this();
        addAll(collection);
    }

    @Override
    public boolean add(E element) {
        map.put(element,PRESENT);
        return true;
    }

    @Override
    public boolean remove(E element) {
        return map.remove(element) != null;
    }

    @Override
    public boolean contains(E element) {
        return map.contains(element);
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Iterator<E> iterator() {
        return new BinarySetIterator(map.iterator());
    }

    final class BinarySetIterator implements Iterator<E>{

        private Iterator<Map.Entry<E,Object>> iterator;

        public BinarySetIterator(Iterator<Map.Entry<E,Object>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public E next() {
            return iterator.next().getKey();
        }
    }
}
