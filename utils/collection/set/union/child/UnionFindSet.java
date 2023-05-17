package utils.collection.set.union.child;


import utils.collection.Collection;
import utils.collection.set.union.UnionSet;
import utils.objects.ObjectUtils;

import java.util.*;
import java.util.function.Function;

/**
 * 并查集结构
 */
public class UnionFindSet<V> implements UnionSet<V> {

    /**
     * 内部节点类（包裹每个元素的集合）
     */
    private static class Node<V>{
        V value;        //元素内容
        Node<V> pre;    //向上指针（父指针）
        int size;       //集合元素个数
        Map<V,Node<V>> nodes; //记录当前节点有多少个节点关联着

        public Node(V value) {
            this.value = value;
            this.pre = this;
            this.size = 1;
            nodes = new HashMap<>();
        }
    }

    //注册表
    private Map<V,Node<V>> nodeMap;

    public UnionFindSet() {
        nodeMap = new HashMap<>();
    }

    //在初始化的过程中，要求用户将所有元素传进来
    public UnionFindSet(List<V> values) {
        nodeMap = new HashMap<>();
        for (V value : values) {
            //注册到节点上，并且为每个元素包装一个集合节点
            nodeMap.put(value,new Node<>(value));
        }
    }

    @Override
    public boolean add(V value) {
        if (contains(value)) return false;
        nodeMap.put(value,new Node<>(value));
        return true;
    }

    @Override
    public boolean remove(V value) {
        if (contains(value)){
            Node<V> node = nodeMap.get(value);
            //node是代表节点
            if (node.pre == node){
                Iterator<Map.Entry<V, Node<V>>> iterator = node.nodes.entrySet().iterator();
                Node<V> preNode = null; //下一任代表节点
                if (iterator.hasNext()){
                    preNode = iterator.next().getValue();
                }
                while (iterator.hasNext()){
                    Map.Entry<V, Node<V>> next = iterator.next();
                    next.getValue().pre = preNode; //更新其他节点代表节点指针
                    preNode.nodes.put(next.getKey(),next.getValue());
                }
                preNode.size = (node.size-1) + preNode.size;
                preNode.pre = preNode;
            }else {
                //如果node不是代表节点，则将node节点以下的其他节点链接到父节点上
                if (node.nodes.size() > 0){
                    Iterator<Map.Entry<V, Node<V>>> iterator = node.nodes.entrySet().iterator();
                    while (iterator.hasNext()){
                        Map.Entry<V, Node<V>> next = iterator.next();
                        node.pre.nodes.put(next.getKey(),next.getValue());
                    }
                    node.pre.size = node.pre.size + node.size;
                }
                node.pre.nodes.remove(value);
                node.pre.size--;
            }
            nodeMap.remove(value);
            return true;
        }
        return false;
    }

    /**
     * 获取某个节点的最顶端节点（代表节点）
     */
    private Node<V> findHead(V v){
        //创建一个栈，用于记录在查找最上节点的过程中的每个节点（优化向上查找的过程）
        Stack<Node<V>> stack = new Stack<>();
        //获取查找的节点
        Node<V> node = nodeMap.get(v);
        checkNode(node,v);
        //如果查找的节点的父节点不是最顶节点，则记录当前节点，并且向上找到最顶端节点
        while (node != node.pre){
            stack.push(node);
            node = node.pre;
        }
        //将这段过程的每个节点的顶端节点都设置成node（因为node在这里就已经是顶端节点了）
        while (!stack.isEmpty()){
            stack.pop().pre = node;
        }
        return node;
    }

    /**
     * 判断两个元素是否已经注册在并查集中
     */
    private boolean isRegister(V a,V b){
        return nodeMap.get(a) != null && nodeMap.get(b) != null;
    }

    @Override
    public boolean isSameSet(V a, V b) {
        if (isRegister(a,b)){
            //判断两个元素的代表节点是否为同一个即可
            return findHead(a) == findHead(b);
        }
        return false;
    }

    @Override
    public boolean contains(V value) {
        return nodeMap.get(value) != null;
    }

    @Override
    public void union(V a, V b) {
        //如果两个集合没有进行合并过，才进行合并
        if (isRegister(a,b) && !isSameSet(a,b)){
            Node<V> aNode = nodeMap.get(a); //获取节点A的代表节点（最顶部节点）
            Node<V> bNode = nodeMap.get(b); //获取节点B的代表节点（最顶部节点）
            //选出较大的集合
            Node<V> bigNode = aNode.size >= bNode.size ? aNode : bNode;
            //选出较小的集合
            Node<V> smallNode = bigNode == aNode ? bNode : aNode;
            //获取较大集合的代表元素（让合并后的元素全部合并到代表元素上）
            Node<V> bigHead = findHead(bigNode.value);
            //让较小集合的父指针指向代表节点
            smallNode.pre = bigHead;
            //更新代表节点集合的元素个数
            bigHead.size = bigHead.size + smallNode.size;
            bigHead.nodes.put(smallNode.value,smallNode); //将较小的集合纳入到集合大的列表中
            bigHead.nodes.putAll(smallNode.nodes);  //将较小集合的其他子元素，合并到较大集合中
            smallNode.size = 1; //将较小集合的元素集合设置为1
            smallNode.nodes.clear(); //清空较小集合

        }
    }

    private void checkNode(Node node,V value){
        if (node == null) throw new RuntimeException("No value is specified in the collection value：" + value);
    }

    @Override
    public List<V> getSameSetValue(V value) {
        Node<V> node = findHead(value);
        List<V> list = new ArrayList<>(node.size);
        node.nodes
                .entrySet()
                .stream()
                .map(new Function<Map.Entry<V, Node<V>>, V>() {
                    @Override
                    public V apply(Map.Entry<V, Node<V>> vNodeEntry) {
                        return vNodeEntry.getKey();
                    }
                })
                .forEach(e->{
                    list.add(e);
                });
        return list;
    }

    @Override
    public void clear() {
        nodeMap.clear();
    }

    @Override
    public boolean isEmpty() {
        return nodeMap.isEmpty();
    }

    @Override
    public int size() {
        return nodeMap.size();
    }

    @Override
    public Iterator<V> iterator() {
        return new UnionFindSetIterator();
    }

    @Override
    public boolean addAll(Collection<? extends V> collection) {
        Iterator<? extends V> iterator = collection.iterator();
        if (ObjectUtils.isEmpty(iterator)) return false;
        while (iterator.hasNext()){
            add(iterator.next());
        }
        return true;
    }


    final class UnionFindSetIterator implements Iterator<V>{

        Iterator<Map.Entry<V, Node<V>>> iterator;

        public UnionFindSetIterator() {
            iterator = nodeMap.entrySet().iterator();
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public V next() {
            return iterator.next().getKey();
        }
    }

    @Override
    public Iterator<V> iterator(V value) {
        return new UnionFindSetNodeIterator(value);
    }

    final class UnionFindSetNodeIterator implements Iterator<V>{

        Iterator<Map.Entry<V, Node<V>>> iterator;

        public UnionFindSetNodeIterator(V value) {
            /**
             * 获取当前value的代表节点，遍历整个代表节点下的元素
             */
            Node<V> head = findHead(value);
            checkNode(head,value);
            this.iterator = head.nodes.entrySet().iterator();
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public V next() {
            return iterator.next().getKey();
        }
    }
}
