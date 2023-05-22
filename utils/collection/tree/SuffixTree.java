package utils.collection.tree;


import utils.objects.ObjectUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SuffixTree {
    final static class Node{
        String suffix;
        int[] index;
        Map<String,Node> nexts;

        public Node(String suffix,int start,int end) {
            this.suffix = suffix;
            this.index = new int[2];
            index[0] = start;
            index[1] = end;
            nexts = new HashMap<>();
        }
    }

    private Node root;

    public SuffixTree() {

    }

    public void add(String value){
        Node node = root;
        for (int i = 0; i < value.length(); i++) {
            for (int j = 0; j <= i; j++) {

            }
        }

    }
}
