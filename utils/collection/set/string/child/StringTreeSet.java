package utils.collection.set.string.child;

import utils.collection.Collections;
import utils.collection.list.List;
import utils.collection.set.string.StringSet;
import utils.collection.tree.prefix.PrefixTree;
import utils.collection.tree.prefix.child.StringPrefixSearchTree;
import utils.collection.tree.prefix.child.Trie;

import java.util.Iterator;

public class StringTreeSet implements StringSet {

    private PrefixTree prefixTree;

    public StringTreeSet() {
        this(Collections.defaultPrefixTree());
    }

    public StringTreeSet(PrefixTree prefixTree){
        this.prefixTree = prefixTree;
    }

    @Override
    public boolean add(String value) {
        prefixTree.add(value);
        return true;
    }

    @Override
    public boolean remove(String value) {
        return prefixTree.remove(value);
    }

    @Override
    public int removePrefix(String prefix) {
        return prefixTree.removePrefix(prefix);
    }

    @Override
    public List<String> matchingPrefix(String prefix) {
        return prefixTree.getPrefix(prefix);
    }

    @Override
    public int getPrefixSize(String prefix) {
        return prefixTree.getPrefixSize(prefix);
    }

    @Override
    public boolean contains(String value) {
        return prefixTree.contains(value);
    }

    @Override
    public int size() {
        return prefixTree.size();
    }

    @Override
    public boolean isEmpty() {
        return prefixTree.isEmpty();
    }

    @Override
    public void clear() {
        prefixTree.clear();
    }

    @Override
    public String toString() {
        return prefixTree.toString();
    }

    @Override
    public Iterator<String> iterator() {
        throw new RuntimeException("Set 暂未实现迭代器接口");
    }
}
