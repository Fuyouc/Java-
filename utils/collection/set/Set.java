package utils.collection.set;

import utils.collection.Collection;
import utils.collection.CollectionConstructor;

public interface Set<E> extends Collection<E> {

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

    static <E> Set<? extends E> of(E... element){
        return CollectionConstructor.buildSet(element);
    }

}
