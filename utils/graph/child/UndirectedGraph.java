package utils.graph.child;


import utils.graph.BaseAbstractGraph;
import utils.graph.Graph;
import utils.collection.set.union.UnionSet;
import utils.collection.set.union.child.UnionFindSet;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 无向图实现
 */
public class UndirectedGraph<E> extends BaseAbstractGraph<E> {

    //图表
    private Map<E,Node<E>> graph;

    //并查集
    private UnionSet<Node<E>> unionFindSet;

    public UndirectedGraph() {
        this(null);
    }

    public UndirectedGraph(List<E> data){
        graph = new HashMap<>();
        unionFindSet = new UnionFindSet<>();
        if (data != null){
            data.stream().forEach(e->{
                addNode(e);
            });
        }
    }

    @Override
    public void addNode(E element) {
        checkElement(element);
        if (containsElement(element)) return;
        Node<E> newNode = new Node<>(element);
        graph.put(element,newNode);
        unionFindSet.add(newNode);
    }

    @Override
    public void removeNode(E element) {
        checkElement(element);
        if (containsElement(element)){
            Node<E> removeNode = graph.get(element);
            Iterator<Map.Entry<E, Edge<E>>> iterator = removeNode.edges.entrySet().iterator();
            while (iterator.hasNext()){
                //获取删除节点所有的边
                BaseAbstractGraph.Node<E> to = iterator.next().getValue().to;
                //让边的另一端删除当前节点记录
                to.edges.remove(removeNode.element);
            }
            graph.remove(element);
            //将删除节点清除
            unionFindSet.remove(removeNode);
        }
    }

    @Override
    public int size() {
        return graph.size();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean containsElement(E element) {
        return graph.get(element) != null;
    }

    @Override
    public void clear() {
        graph.clear();
        unionFindSet.clear();
    }

    @Override
    public void connectionNode(E e1, E e2,double weight) {
        if (e1 == null){
            addNode(e1);
        }
        if (e2 == null){
            addNode(e2);
        }
        Node<E> e1Node = graph.get(e1);
        Node<E> e2Node = graph.get(e2);
        e1Node.connectionNode(e2Node,weight);
        //使用并查集，合并两个集合
        unionFindSet.union(e1Node,e2Node);
    }


    @Override
    public boolean isConnected(E e1, E e2) {
        checkElement(e1);
        checkElement(e2);
        return unionFindSet.isSameSet(graph.get(e1),graph.get(e2));
    }

    @Override
    public List<E> getConnectedList(E e1) {
        checkElement(e1);
        return graph.get(e1).edges.entrySet().stream().map(new Function<Map.Entry<E, Edge<E>>, E>() {
            @Override
            public E apply(Map.Entry<E, Edge<E>> eEdgeEntry) {
                return eEdgeEntry.getKey();
            }
        }).collect(Collectors.toList());
    }

    @Override
    public Iterator<E> iterator() {
        return new GraphIterable();
    }

    @Override
    public Iterator<E> iterator(E e1) {
        checkElement(e1);
        return new NodeIterable(graph.get(e1));
    }


    @Override
    public Distance<E> distance(E e1, E e2) {
        checkElement(e1);
        checkElement(e2);

        Node<E> from = graph.get(e1);
        Node<E> to = graph.get(e2);

        //两个节点是连通的，才进行计算
        if (unionFindSet.isSameSet(from,to)){
            Map<E,DistanceTable> distanceTable = getDistanceTable(from);
            Queue<Node<E>> queue = new LinkedList<>();
            Set<Node<E>> set = new HashSet<>();
            queue.add(from);
            set.add(from);
            while (!queue.isEmpty()){
                Node<E> poll = queue.poll();
                for (Map.Entry<E, Edge<E>> entry : poll.edges.entrySet()) {
                    Edge<E> edge = entry.getValue();
                    if (!set.contains(edge.to)) {
                        DistanceTable fromDistance = distanceTable.get(poll.element);
                        DistanceTable toDistance = distanceTable.get(entry.getKey());
                        if ((fromDistance.distance + edge.weight) < toDistance.distance) {
                            toDistance.distance = fromDistance.distance + edge.weight;
                            toDistance.pre = fromDistance.element;
                        }
                    }
                }
                //获得下一个出发点
                Node<E> nextNode = getUnlockedNode(set, distanceTable);
                if (nextNode != null){
                    queue.add(nextNode);
                    set.add(nextNode);
                }
            }
            return getDistance(e2,distanceTable);
        }
        return null;
    }

    /**
     * 选出一个未锁定的节点
     */
    private Node<E> getUnlockedNode(Set<Node<E>> set, Map<E,DistanceTable> distanceTable){
        List<Map.Entry<E, DistanceTable>> unlocked = distanceTable.entrySet().stream()
                .filter(new Predicate<Map.Entry<E, DistanceTable>>() {
                    @Override
                    public boolean test(Map.Entry<E, DistanceTable> eDistanceTableEntry) {
                        if (!set.contains(graph.get(eDistanceTableEntry.getKey()))) return true;
                        return false;
                    }
                })
                .collect(Collectors.toList());

        if (unlocked.size() > 0){
            int minIndex = 0;
            for (int i = 1; i < unlocked.size(); i++) {
                if (unlocked.get(i).getValue().distance < unlocked.get(minIndex).getValue().distance) minIndex = i;
            }
            return graph.get(unlocked.get(minIndex).getKey());
        }
        return null;
    }

    /**
     * 根据起始点创建最短路径表
     * @param from
     * @return
     */
    private Map<E,DistanceTable> getDistanceTable(Node<E> from){
        Map<E,DistanceTable> distanceTable = new HashMap<>();
        Set<Node<E>> set = new HashSet<>();
        Queue<Node<E>> queue = new LinkedList<>();
        set.add(from);
        queue.add(from);
        distanceTable.put(from.element,new DistanceTable(from.element,null,0));
        while (!queue.isEmpty()){
            Node<E> poll = queue.poll();
            for (Map.Entry<E, Edge<E>> entry : poll.edges.entrySet()) {
                BaseAbstractGraph.Node<E> to = entry.getValue().to;
                if (!set.contains(to)){
                    queue.add((Node<E>) to);
                    set.add((Node<E>) to);
                    distanceTable.put(to.element,new DistanceTable(to.element,null,Integer.MAX_VALUE));
                }
            }
        }
        return distanceTable;
    }

    /**
     * 获取距离对象
     * @param to
     * @param table
     * @return
     */
    private Distance<E> getDistance(E to,Map<E,DistanceTable> table){
        List<E> path = new ArrayList<>();
        Queue<E> queue = new LinkedList<>();
        queue.add(to);
        while (!queue.isEmpty()){
            E poll = queue.poll();
            DistanceTable distanceTable = table.get(poll);
            path.add(distanceTable.element);
            if (distanceTable.pre != null){
                queue.add(distanceTable.pre);
            }
        }
        Collections.reverse(path); //反转路径
        Distance<E> distance = new GraphDistance<>(path,table.get(to).distance);
        return distance;
    }

    public static class GraphDistance<E> implements Graph.Distance<E>{

        private List<E> path;
        private double distance;

        public GraphDistance(List<E> path, double distance) {
            this.path = path;
            this.distance = distance;
        }

        @Override
        public List<E> getPath() {
            return path;
        }

        @Override
        public double getDistance() {
            return distance;
        }
    }


    /**
     * 最短路径表中的每一行数据项
     */
    final class DistanceTable{
        E element;    //当前节点标志
        E pre;        //前面的节点标志
        double distance; //总距离

        public DistanceTable(E element, E pre, double distance) {
            this.element = element;
            this.pre = pre;
            this.distance = distance;
        }

        @Override
        public String toString() {
            return "DistanceTable{" +
                    "element=" + element +
                    ", pre=" + pre +
                    ", distance=" + distance +
                    '}';
        }
    }


    /**
     * 遍历整个图的所有节点
     */
    final class GraphIterable implements Iterator<E>{

        private Queue<Node<E>> queue;

        public GraphIterable() {
            queue = new LinkedList<>();
            Iterator<Map.Entry<E, Node<E>>> iterator = graph.entrySet().iterator();
            while (iterator.hasNext()){
                queue.add(iterator.next().getValue());
            }
        }

        @Override
        public boolean hasNext() {
            return !queue.isEmpty();
        }

        @Override
        public E next() {
            return queue.poll().element;
        }
    }

    /**
     * 采用广度优先遍历遍历每个相邻节点的元素
     */
    final class NodeIterable implements Iterator<E> {

        private Queue<Node<E>> queue;

        private Set<Node<E>> set;

        public NodeIterable(Node<E> node) {
            queue = new LinkedList<>();
            set = new HashSet<>();
            set.add(node);
            traversal(node);
        }

        @Override
        public boolean hasNext() {
            return !isEmpty();
        }

        @Override
        public E next() {
            return traversal(queue.poll());
        }

        private boolean isEmpty(){
            return queue.isEmpty();
        }

        protected E traversal(Node<E> node) {
            Iterator<Map.Entry<E, Edge<E>>> iterator = node.edges.entrySet().iterator();
            while (iterator.hasNext()){
                Map.Entry<E, Edge<E>> next = iterator.next();
                if (!set.contains(next.getValue().to)){
                    queue.add((Node<E>) next.getValue().to);
                    set.add((Node<E>) next.getValue().to);
                }
            }
            return node.element;
        }

    }


    private static class Node<E> extends BaseAbstractGraph.Node<E>{

        public Node(E element) {
            super(element);
        }

        @Override
        public void connectionNode(BaseAbstractGraph.Node<E> node,double weight) {
            this.in++;
            this.out++;
            node.in++;
            node.out++;
            this.edges.put(node.element,new Edge<E>(weight,this,node));
            node.edges.put(this.element,new Edge<E>(weight,node,this));
        }
    }
}
