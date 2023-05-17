package utils.data.random;

import java.util.Random;

public abstract class BaseAbstractGenerator<E> implements DataGenerator<E>{
    protected Random random;

    public BaseAbstractGenerator() {
        random = new Random();
    }

}
