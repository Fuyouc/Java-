package utils.collection;

import java.util.Iterator;

/**
 * 集合接口
 */
public interface Collection<E> {

    /**
     * 添加元素
     */
    boolean add(E element);

    /**
     * 删除元素
     */
    boolean remove(E element);

    /**
     * 元素是否存在
     * @param element
     */
    boolean contains(E element);

    default boolean containsAll(Collection<? extends E> collection){
        Iterator<? extends E> iterator = collection.iterator();
        while (iterator.hasNext()){
            if (!contains(iterator.next())){
                return false;
            }
        }
        return true;
    }

    /**
     * 元素个数
     */
    int size();

    /**
     * 集合是否为空
     * @return
     */
    default boolean isEmpty(){
        return size() == 0;
    }

    /**
     * 清空集合
     */
    void clear();

    /**
     * 集合迭代器
     * @return
     */
    Iterator<E> iterator();

    /**
     * 批量添加
     */
    default boolean addAll(Collection<? extends E> collection){
        Iterator<? extends E> iterator = collection.iterator();
        if (iterator == null) return false;
        while (iterator.hasNext()){
            add(iterator.next());
        }
        return true;
    }

    default E[] toArray(){
        E[] e = (E[]) new Object[size()];
        Iterator<E> iterator = iterator();
        int index = 0;
        while (iterator.hasNext()){
            e[index++] = iterator.next();
        }
        return e;
    }

    String toString();
}
