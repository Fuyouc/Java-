package utils.collection;

import utils.collection.list.List;
import utils.collection.set.Set;
import utils.collection.set.child.BinarySearchTreeSet;
import utils.collection.set.child.TrieSet;
import utils.objects.ObjectUtils;

import java.util.Iterator;

public class Collections {

    public static <E> Set<E> listToSet(List<E> list){
        if (!ObjectUtils.isEmpty(list) && list.size() > 0){
            Set<E> set = new BinarySearchTreeSet<>();
            Iterator<E> iterator = list.iterator();
            while (iterator.hasNext()){
                set.add(iterator.next());
            }
            return set;
        }
        return (Set<E>) Set.of();
    }

    public static Set<String> listToTrieSet(List<String> list){
        Set<String> set = new TrieSet();
        if (!ObjectUtils.isEmpty(list) && list.size() > 0){
            Iterator<String> iterator = list.iterator();
            while (iterator.hasNext()){
                set.add(iterator.next());
            }
        }
        return set;
    }


}
