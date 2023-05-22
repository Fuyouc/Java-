package utils.bloom.hashcode;


import utils.bloom.BloomHashCode;

public class JavaHashCode implements BloomHashCode {
    @Override
    public int hashCode(Object element) {
        return element.hashCode();
    }
}
