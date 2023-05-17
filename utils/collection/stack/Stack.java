package utils.collection.stack;

import utils.collection.Collection;

public interface Stack<E> extends Collection<E> {

    /**
     * 添加元素
     */
    boolean push(E element);

    /**
     * 弹出元素
     * @return
     */
    E pop();

    /**
     * 返回栈顶，但不弹出
     */
    E peekFirst();

    /**
     * 返回栈底，但不弹出
     * @return
     */
    E peekLast();
}
