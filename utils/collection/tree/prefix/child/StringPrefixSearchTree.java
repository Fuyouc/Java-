package utils.collection.tree.prefix.child;

import utils.collection.list.List;
import utils.collection.list.child.ArrayList;
import utils.collection.list.child.DoubleLinkedList;
import utils.collection.tree.prefix.PrefixTree;
import utils.objects.ObjectUtils;

import java.util.*;

/**
 * 字符串查找树
 * 与 Trie 不同，StringPrefixSearchTree 支持任意 类型的字符串进行存储
 * 支持区分大小写，方便查找更多内容
 */
public class StringPrefixSearchTree implements PrefixTree {

    final static class Node{
        int pass;
        int end;
        Map<Character,Node> next;

        public Node() {
            pass = 0;
            end = 0;
            next = new HashMap<>();
        }
    }

    private Node root;

    private int size;

    /**
     * 是否区分大小写，默认为false
     */
    private boolean enableCase;

    public StringPrefixSearchTree() {
        this(false);
    }

    public StringPrefixSearchTree(boolean enableCase){
        root = new Node();
        this.enableCase = enableCase;
    }

    @Override
    public void add(String value) {
        Character[] chars = toCharArray(value);
        Node node = root;
        node.pass++;
        for (int i = 0; i < chars.length; ++i){
            Character character = chars[i];
            if (!node.next.containsKey(character)){
                Node newNode = new Node();
                node.next.put(character,newNode);
                node = newNode;
                node.pass++;
                continue;
            }
            node = node.next.get(character);
            node.pass++;
        }
        node.end++;
        ++size;
    }

    @Override
    public boolean remove(String value) {
        if (isEmpty() && !contains(value)) return false;
        Character[] characters = toCharArray(value);
        Node node = root,
             pre = null;
        for (int i = 0; i < characters.length; i++) {
            Character character = characters[i];
            node.pass--;
            if (node.pass == 0){
                if (pre == null){
                    root = null;
                }else {
                    node.next.remove(character);
                }
                --size;
                return true;
            }
            pre = node;
            node = node.next.get(character);
        }
        node.end--;
        --size;
        return true;
    }

    @Override
    public int removePrefix(String prefix) {
        if (isEmpty()) return 0;

        Character[] characters = toCharArray(prefix);


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
        for (int i = 0; i < characters.length; ++i){
            Character character = characters[i];
            Node nextNode = removeNode.next.get(character);
            if (ObjectUtils.isEmpty(nextNode)) return 0;
            removeNode = nextNode;
            queue.add(new Entry(removeNode,character));
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
                root.next.remove(entry.value);
                size = size - removePass;
                return removeNode.pass;
            }
        }

        while (!queue.isEmpty()){
            Entry entry = queue.poll();
            Node node = entry.key;
            node.pass = node.pass - removePass;
            if (node.pass == 0){
                parent.next.remove(entry.value);
                size = size - removePass;
                return removeNode.pass;
            }
            parent = node;
        }

        return 0;
    }

    final static class Entry implements utils.collection.map.Map.Entry<Node,Character>{

        Node key;

        Character value;

        public Entry(Node key, Character value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public Node getKey() {
            return key;
        }

        @Override
        public Character getValue() {
            return value;
        }
    }

    @Override
    public boolean contains(String value) {
        if (isEmpty()) return false;
        Node node = root;
        Character[] characters = toCharArray(value);
        for (int i = 0; i < characters.length; i++) {
            Character character = characters[i];
            Node nextNode = node.next.get(character);
            if (ObjectUtils.isEmpty(nextNode)) return false;
            node = nextNode;
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
        Node node = root;
        Character[] characters = toCharArray(prefix);
        for (int i = 0; i < characters.length; i++) {
            Character character = characters[i];
            if (ObjectUtils.isEmpty(node)) return null;
            node = node.next.get(character);
        }
        return getCharacterList(node,prefix,node.end > 0);
    }
    
    private List<String> getCharacterList(Node node,String prefix,boolean flag){
        if (node == null) return null;
        List<String> list = new ArrayList<>();
        if (flag) list.add(prefix);
        List<String> tempList = new DoubleLinkedList<>();
        StringBuilder sb = new StringBuilder();
        Iterator<Map.Entry<Character, Node>> iterator = node.next.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<Character, Node> entry = iterator.next();
            Node nextNode = entry.getValue();
            tempList.clear();
            sb.setLength(0);
            List<String> characterList = getCharacterList(nextNode,entry.getKey(),tempList,sb);
            Iterator<String> characterIterator = characterList.iterator();
            while (characterIterator.hasNext()){
                list.add(prefix + characterIterator.next());
            }
        }
        return list;
    }

    private List<String> getCharacterList(Node node,Character key,List<String> list, StringBuilder sb){
        if (node == null) return list; //如果节点为空，则返回
        sb.append(key);
        if (node.end > 0) list.add(sb.toString());
        Iterator<Map.Entry<Character, Node>> iterator = node.next.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<Character, Node> entry = iterator.next();
            getCharacterList(entry.getValue(),entry.getKey(),list,sb);
        }
        //取出上一个节点所拼接的单词字母
        sb.deleteCharAt(sb.length() - 1);
        return list;
    }

    @Override
    public int getPrefixSize(String prefix) {
        if (isEmpty()) return 0;
        Node node = root;
        Character[] characters = toCharArray(prefix);
        for (int i = 0; i < characters.length; i++) {
            Character character = characters[i];
            if (ObjectUtils.isEmpty(node)) return 0;
            node = node.next.get(character);
        }
        return node.pass;
    }

    @Override
    public void clear() {
        root.next.clear();
        size = 0;
        root = null;
    }

    private Character[] toCharArray(String value){
       if (value == null){
           return new Character[0];
       }
        if (enableCase){
            value = value.toLowerCase();
        }
        char[] chars = value.toCharArray();
        Character[] characters = new Character[chars.length];
        for (int i = 0; i < chars.length; i++) {
            characters[i] = chars[i];
        }
        return characters;
    }
}
