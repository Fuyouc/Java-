package utils.collection.list;

import utils.collection.Collection;
import utils.collection.CollectionConstructor;

/**
 * List列表接口
 * @param <E>
 */
public interface List<E> extends Collection<E> {

    /**
     * 添加元素
     */
    boolean add(E element);

    /**
     * 删除指定下标的元素
     * @param index
     * @return
     */
    E remove(int index);

    /**
     * 根据索引查找元素
     */
    E get(int index);

    /**
     * 元素是否存在
     * @param element
     */
    boolean contains(E element);

    /**
     * 反转集合
     */
    void reversal();

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

    static <E> List<E> of(E... objects){
        return CollectionConstructor.buildList(objects);
    }

}
