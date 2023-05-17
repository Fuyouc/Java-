package utils.collection;

import utils.collection.list.List;
import utils.collection.list.child.ArrayList;
import utils.collection.queue.Queue;
import utils.collection.queue.child.LinkedQueue;
import utils.collection.set.Set;
import utils.collection.set.child.BinarySearchTreeSet;
import utils.collection.map.Map;
import utils.collection.map.tree.BinarySearchTreeMap;
import utils.collection.stack.Stack;
import utils.collection.stack.child.LinkedStack;
import utils.objects.ObjectUtils;

/**
 * 集合构造器
 */
public class CollectionConstructor {

    public static <K,V> Map<? extends K,? extends V> buildMap(Object... objects){
        check(objects);
        if (objects.length % 2 != 0) throw new RuntimeException("length is not a multiple of 2");
        Map<Object, Object> map = new BinarySearchTreeMap<>();
        for (int i = 0; i < objects.length; i+=2) {
            map.put(objects[i],objects[i+1]);
        }
        return (Map<? extends K, ? extends V>) map;
    }

    public static <E> List<E> buildList(E... objects){
        check(objects);
        return new ArrayList<E>(objects);
    }

    public static <E>Set<E> buildSet(E... objects){
        if (ObjectUtils.isEmpty(objects)) throw new RuntimeException("objects is null");
        return new BinarySearchTreeSet<>(objects);
    }

    public static <E>Queue<E> buildQueue(E... objects){
        check(objects);
        return new LinkedQueue<>(objects);
    }

    public static <E> Stack<E> buildStack(E... objects){
        check(objects);
        return new LinkedStack<>(objects);
    }

    private static <E> void check(E objects){
        if (ObjectUtils.isEmpty(objects)) throw new RuntimeException("objects is null");
    }
}
