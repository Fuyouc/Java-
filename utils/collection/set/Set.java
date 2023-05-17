package utils.collection.set;

import utils.collection.Collection;
import utils.collection.CollectionConstructor;

public interface Set<E> extends Collection<E> {

    static <E> Set<? extends E> of(E... element){
        return CollectionConstructor.buildSet(element);
    }

}
