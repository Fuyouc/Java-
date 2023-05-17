package utils.bloom.hashcode;


import JAVA_Collection_Framework.utils.bloom.BloomHashCode;

public class JavaHashCode implements BloomHashCode {
    @Override
    public int hashCode(Object element) {
        return element.hashCode();
    }
}
