package utils.graph;

import java.util.Iterator;
import java.util.List;

public interface Graph<E> {
    /**
     * 向图中添加节点
     * @param element 元素
     */
    void addNode(E element);

    /**
     * 删除节点
     * @param element
     */
    void removeNode(E element);

    /**
     * 获取节点数量
     * @return
     */
    int size();

    /**
     * 图是否为空
     * @return
     */
    boolean isEmpty();

    /**
     * 图中是否包含该节点
     * @param element
     * @return
     */
    boolean containsElement(E element);

    /**
     * 清空图
     */
    void clear();

    /**
     * 连接两个节点
     * @param e1  元素1
     * @param e2  元素2
     * @param weight 边的权重
     */
    void connectionNode(E e1,E e2,double weight);

    /**
     * 判断两个节点是否相连
     * @param e1   元素1
     * @param e2   元素2
     * @return
     */
    boolean isConnected(E e1,E e2);

    /**
     * 获取一个节点其他相连的节点
     */
    List<E> getConnectedList(E e1);

    /**
     * 获取整个图的迭代器
     * @return
     */
    Iterator<E> iterator();

    /**
     * 获取指定某个节点的迭代器
     * @param e1
     * @return
     */
    Iterator<E> iterator(E e1);

    /**
     * 获取两个点的距离
     */
    Distance<E> distance(E e1,E e2);

    interface Distance<E>{
        List<E> getPath(); //获取路径
        double getDistance(); //获取距离
    }
}
