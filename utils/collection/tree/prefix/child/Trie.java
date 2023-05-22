package utils.collection.tree.prefix.child;

import utils.collection.list.List;
import utils.collection.list.child.ArrayList;
import utils.collection.list.child.DoubleLinkedList;
import utils.collection.map.Map;
import utils.collection.tree.prefix.PrefixTree;
import utils.objects.ObjectUtils;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/**
 * 单词查找树
 * 只能存储字符类型的数据（a-zA-Z）
 */
public class Trie implements PrefixTree {

    /**
     * 正则校验字符
     */
    private static final String regex = "[a-zA-Z]{0,}";

    /**
     * 字母表
     */
    private static final char[] LETTER_TABLE = {
            'a','b','c','d','e','f','g','h','i','j','k',
            'l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'
    };

    private int size;

    /**
     * 此处有优化，可以在节点上保存真正添加下一个节点的索引，避免过多的循环浪费
     * 但是：空间的占用也会更大
     */
    final static class Node{
        int pass;
        int end;
        Node[] nexts;
        public Node() {
            nexts = new Node[LETTER_TABLE.length];
        }
    }

    private Node root;

    public Trie() {
        root = new Node();
    }

    @Override
    public void add(String value) {
        char[] chars = toCharArray(value);
        Node node = root;
        node.pass++;
        for (int i = 0; i < chars.length; i++) {
            int index = getIndex(chars[i]);
            if (ObjectUtils.isEmpty(node.nexts[index])){
                node.nexts[index] = new Node();
            }
            node = node.nexts[index];
            node.pass++;
        }
        node.end++;
        ++size;
    }

    @Override
    public boolean remove(String value) {
        if (isEmpty() && !contains(value)) return false;
        char[] chars = toCharArray(value);
        Node node = root,
             pre = null; //当前节点的父节点
        int nextIndex = 0; //当前节点在父节点的那个格子上
        for (int i = 0; i < chars.length; i++) {
            int index = getIndex(chars[i]);
            node.pass--;
            //如果pass属性为0，说明以下的所有字符都被删除
            if (node.pass == 0){
                if (pre == null){
                    root = null;
                }else {
                    //将node节点清除
                    pre.nexts[nextIndex] = null;
                }
                --size;
                return true;
            }
            pre = node;
            nextIndex = index;
            node = node.nexts[index];
        }
        node.end--;
        --size;
        return false;
    }

    @Override
    public int removePrefix(String prefix) {
        if (isEmpty()) return 0;

        char[] chars = toCharArray(prefix);

        //被删除的节点
        Node removeNode = root;

        /**
         * 记录查找过程中所经过的节点以及每个节点的下标
         * 不包含根节点
         */
        Queue<Entry> queue = new LinkedList<>();

        /**
         * 遍历查找要删除的节点
         */
        for (int i = 0; i < chars.length; ++i){
            int index = getIndex(chars[i]);
            if (ObjectUtils.isEmpty(removeNode.nexts[index])) return 0;
            removeNode = removeNode.nexts[index];
            queue.add(new Entry(removeNode,index));
        }

        //删除的单词个数
        int removePass = removeNode.pass;

        /**
         * 记录每个节点的父节点
         */
        Node parent = null;

        /**
         * 比较顶部节点的 pass=0，如果等于直接删除，不需要再向下查找
         */
        if (!queue.isEmpty()){
            Entry entry = queue.poll();
            parent = entry.key;
            parent.pass = parent.pass - removePass;
            root.pass = root.pass - removePass;
            if (parent.pass == 0){
                //如果pass为0，则表示删除当前前缀的所有节点
                root.nexts[entry.value] = null;
                size = size - removePass;
                return removeNode.pass;
            }
        }

        while (!queue.isEmpty()){
            Entry entry = queue.poll();
            Node node = entry.key;
            node.pass = node.pass - removePass;
            if (node.pass == 0){
                parent.nexts[entry.value] = null;
                size = size - removePass;
                return removeNode.pass;
            }
            parent = node;
        }

        return 0;
    }

    final static class Entry implements Map.Entry<Node,Integer>{

        Node key;
        Integer value;

        public Entry(Node key, Integer value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public Node getKey() {
            return key;
        }

        @Override
        public Integer getValue() {
            return value;
        }
    }

    @Override
    public boolean contains(String value) {
        char[] chars = toCharArray(value);
        Node node = root;
        for (int i = 0; i < chars.length; i++) {
            int index = getIndex(chars[i]);
            if (ObjectUtils.isEmpty(node.nexts[index])) return false;
            node = node.nexts[index];
        }
        return node.end > 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public List<String> getPrefix(String prefix) {
        if (isEmpty()) return null;
        char[] chars = toCharArray(prefix);
        Node node = root;
        for (int i = 0; i < chars.length; i++) {
            int index = getIndex(chars[i]);
            if (node == null) return null;
            node = node.nexts[index];
        }

        return getCharacterList(node,prefix.toLowerCase(),node.end > 0);
    }

    /**
     * 获取当前节点下的所有子字符
     * @param node
     * @param flag 是否包含当前前缀
     * @return
     */
    private List<String> getCharacterList(Node node,String prefix,boolean flag){
        if (node == null) return null;
        List<String> list = new ArrayList<>();
        if (flag) list.add(prefix);
        List<String> tempList = new DoubleLinkedList<>();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < node.nexts.length; i++) {
            Node nextNode = node.nexts[i];
            if (!ObjectUtils.isEmpty(nextNode)){
                tempList.clear();
                sb.setLength(0);
                List<String> characterList = getCharacterList(nextNode, i, tempList,sb);
                Iterator<String> iterator = characterList.iterator();
                while (iterator.hasNext()){
                    list.add(prefix + iterator.next());
                }
            }
        }
        return list;
    }

    /**
     * 递归将当前节点及以下的所有节点都走一遍
     * @param node  当前节点
     * @param index 节点下标
     * @param list
     * @param sb
     * @return
     */
    private List<String> getCharacterList(Node node,int index,List<String> list,StringBuilder sb){
        if (node == null) return list; //如果节点为空，则返回

        //根据下标，获取当前节点对应的字符
        sb.append(LETTER_TABLE[index]);

        //如果当前节点是某个单词的末尾，则将拼接的内容添加到集合中
        if (node.end >0) list.add(sb.toString());

        /**
         * 遍历当前节点的所有next节点
         * 保证到每个节点都能获取到
         */
        for (int i = 0; i < node.nexts.length; ++i){
            getCharacterList(node.nexts[i],i,list,sb);
        }

        //取出上一个节点所拼接的单词字母
        sb.deleteCharAt(sb.length() - 1);
        return list;
    }

    @Override
    public int getPrefixSize(String prefix) {
        if (isEmpty()) return 0;
        Node node = root;
        char[] chars = toCharArray(prefix);
        for (int i = 0; i < chars.length; i++) {
            int index = getIndex(chars[i]);
            if (ObjectUtils.isEmpty(node)) return 0;
            node = node.nexts[index];
        }
        return node.pass;
    }


    @Override
    public void clear() {
        root.nexts = null;
        root = null;
        size = 0;
    }

    private int getIndex(char c){
        return c - 'a';
    }

    private char[] toCharArray(String value){
        if (value != null){
            if (!value.matches(regex)) throw new RuntimeException("单词查找树不允许非字符出现");
            return value.toLowerCase().toCharArray();
        }
        return new char[0];
    }
}
