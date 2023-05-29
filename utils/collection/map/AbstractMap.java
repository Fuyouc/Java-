package utils.collection.map;

import utils.collection.Collection;
import utils.collection.CollectionConstructor;
import utils.collection.list.List;
import utils.collection.set.Set;
import utils.objects.ObjectUtils;

import java.util.Comparator;
import java.util.Iterator;

public abstract class AbstractMap<K,V> implements Map<K,V>{

    protected Comparator<K> comparator;

    protected int size;

    public AbstractMap() {
        this(null);
    }

    public AbstractMap(Comparator<K> comparator) {
        this.comparator = comparator;
    }

    @Override
    public boolean putAll(Map<? extends K, ? extends V> map) {
        Iterator<? extends Entry<? extends K, ? extends V>> iterator = map.iterator();
        if (ObjectUtils.isEmpty(iterator)) return false;
        while (iterator.hasNext()){
            Entry<? extends K, ? extends V> entry = iterator.next();
            put(entry.getKey(),entry.getValue());
        }
        return true;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        Set<Map.Entry<K,V>> objectSet = CollectionConstructor.buildSet();
        Iterator<Entry<K, V>> iterator = iterator();
        while (iterator.hasNext()){
            objectSet.add(iterator.next());
        }
        return objectSet;
    }
    @Override
    public Set<K> keySet() {
        Iterator<Entry<K, V>> iterator = iterator();
        Set<K> objectSet = CollectionConstructor.buildSet();
        while (iterator.hasNext()){
            objectSet.add(iterator.next().getKey());
        }
        return objectSet;
    }

    @Override
    public Collection<V> values() {
        Iterator<Entry<K, V>> iterator = iterator();
        List<V> objectSet = CollectionConstructor.buildList();
        while (iterator.hasNext()){
            objectSet.add(iterator.next().getValue());
        }
        return objectSet;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    protected static class MapEnter<K,V> implements Map.Entry<K,V>,Comparable<MapEnter<K,V>>{

        K key;
        V value;

        public MapEnter(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public String toString() {
            return "Key：" + getKey() + ",Value：" + getValue();
        }

        @Override
        public int compareTo(MapEnter<K, V> o) {
            return ((Comparable)key).compareTo(o.key);
        }
    }

    @Override
    public String toString() {
        Iterator<Entry<K, V>> iterator = iterator();
        StringBuilder sb = new StringBuilder("{");
        while (iterator.hasNext()){
            Entry<K, V> entry = iterator.next();
            sb.append("\n\t");
            sb.append("\"");
            sb.append(entry.getKey());
            sb.append("\"");
            sb.append(":");
            V value = entry.getValue();
            if (String.class.getTypeName().equals(value.getClass().getTypeName())){
                //如果value是字符串
                sb.append("\"");
                sb.append(value);
                sb.append("\"");
            } else if (Character.class.getTypeName().equals(value.getClass().getTypeName())){
                //如果value是一个普通字符
                sb.append("\'");
                sb.append(value);
                sb.append("\'");
            } else {
                //如果value是其他类型
                sb.append(value);
            }
            sb.append(",");
        }
        if (sb.length() > 1) {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append("\n}");
        return sb.toString();
    }
}
