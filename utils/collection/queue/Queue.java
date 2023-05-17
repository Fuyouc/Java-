package utils.collection.queue;

import utils.collection.Collection;

/**
 * 队列接口
 */
public interface Queue<E> extends Collection<E> {

    /**
     * 添加元素
     * @param element
     * @return
     */
    boolean add(E element);

    /**
     * 删除顶部元素
     * @return
     */
    E remove();

    /**
     * 弹出元素
     */
    E poll();

    /**
     * 返回顶部元素，但不弹出
     */
    E peekFirst();

    /**
     * 返回尾部元素，但不弹出
     * @return
     */
    E peekLast();
}
