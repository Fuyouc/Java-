package utils.graph;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseAbstractGraph<E> implements Graph<E>{

    protected void checkElement(E element){
        if (element == null)
            throw new RuntimeException("Elements are not allowed to be null");
    }

    /**
     * 图中的节点
     * @param <E>
     */
    protected abstract static class Node<E>{
        public E element;              //节点的元素
        public int in;                 //节点的入度
        public int out;                //节点的出度
        public Map<E,Edge<E>> edges;    //节点的边

        public Node(E element) {
            this.element = element;
            this.in = 0;
            this.out = 0;
            this.edges = new HashMap<>();
        }

        //让当前节点连接该节点
        public abstract void connectionNode(Node<E> node,double weight);
    }

    /**
     * 节点的边
     * @param <E>
     */
    protected static class Edge<E>{
        public double weight;     //边的权重
        public Node<E> from;   //起始点
        public Node<E> to;     //目标点

        public Edge(Node<E> from, Node<E> to) {
            this(0,from,to);
        }

        public Edge(double weight, Node<E> from, Node<E> to) {
            this.weight = weight;
            this.from = from;
            this.to = to;
        }
    }

}
