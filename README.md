# 线性表

- **动态数组：·**`ArrayList`
- **单向链表：**`SingleLinkedList`
- **双向链表：**`DoubleLinkedList`
- **跳跃列表：**
  - `SkipLinkedListMap`：基于跳表实现的Map
  - `SkipLinkedList`：**基于跳表实现的有序列表（允许重复元素），在进行范围操作时，效率极高**
    - **支持以下功能：**
      - `范围删除`
      - `范围查找`
      - `获取比指定元素大的元素列表`
      - `获取比指定元素小的元素列表`
      - `获取列表中最大的元素`
      - `获取列表中最小的元素`
- **栈：**`LinkedStack（基于双向链表实现）`
- **队列：**`LinkedQueue(基于双向链表实现)、PriorityQueue(优先队列，基于最大|最小堆实现)`



# 树形结构

- **二叉搜索树：**`BinarySearchTreeMap`
- **前缀树：**`PrefixTree(可高效匹配前缀字符串)`
  - **单词查找树**：`Trie`，用于高效匹配字符单词（只能存储字符）
  - **字符串查找树**：`StringPrefixSearchTree`，在`Trie`的基础上进行了增强，允许存储任意类型的字符串来进行查找
- **堆：**
  - **二叉堆：**`PriorityQueue（优先队列)`



# 图

- **无向图：**`UndirectedGraph（无向图的实现，无向图属于特殊的有向图，内部实现最短路径算法）`
- **并查集：**`UnionFindSet(该并查集已经进行扩展，方便开发期间使用)`



# 其他

- **布隆过滤器：**`BloomFilter(基于位运算实现的布隆过滤器，采用布隆公式动态创建数组大小)`
- **数据生成器：**`StringGenerator(字符串生成器) & IntegerGenerator(Int数据生成器)`
- **哈希表：**`HasTable`
- **稀疏数组：**`SparseArray`
  - `ArrayListSparseArray`：使用数组实现的稀疏数组
  - `LinkedSparseArray`：使用链表实现的稀疏数组

