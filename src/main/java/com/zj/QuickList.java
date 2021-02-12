package com.zj;

import java.util.*;

public class QuickList<E> extends AbstractSequentialList<E> implements Deque<E>, Cloneable, java.io.Serializable{


    private  Node<E> first;

    private  Node<E> last;

    /**
     * 每个linkedList节点都是一个数组,默认大小为100
     */
    private static final int DEFAULT_NODE_CAPACITY = 100;


    private int nodeCapacity;

    private int size = 0;

    public QuickList() {
        this(DEFAULT_NODE_CAPACITY);
    }

    public QuickList(int nodeCapacity){
        if(nodeCapacity<=0){
            throw new IllegalArgumentException("Illegal Capacity: "+
                    nodeCapacity);
        }
        this.nodeCapacity = nodeCapacity;
    }

    public QuickList(Collection<? extends E> c) {
        this();
        addAll(c);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Iterator<E> iterator() {
        return listIterator();
    }

    @Override
    public Iterator<E> descendingIterator() {
        return new DescendingIterator();
    }

    private class DescendingIterator implements Iterator<E> {
        private final ListItr itr = new ListItr(size);
        @Override
        public boolean hasNext() {
            return itr.hasPrevious();
        }
        @Override
        public E next() {
            return itr.previous();
        }
        @Override
        public void remove() {
            itr.remove();
        }
    }

    @Override
    public void addFirst(E e) {
        add(0,e);
    }

    @Override
    public void addLast(E e) {
        add(size,e);
    }

    @Override
    public boolean offerFirst(E e) {
        addFirst(e);
        return true;
    }

    @Override
    public boolean offerLast(E e) {
        addLast(e);
        return true;
    }

    @Override
    public E removeFirst() {
        return remove(0);
    }

    @Override
    public E removeLast() {
        return remove(size);
    }

    @Override
    public E pollFirst() {
        if(size == 0){
            return null;
        }
        return removeFirst();
    }

    @Override
    public E pollLast() {
        if(size == 0){
            return null;
        }
        return removeLast();
    }

    @Override
    public E getFirst() {
        return get(0);
    }

    @Override
    public E getLast() {
        return get(size);
    }

    @Override
    public E peekFirst() {
        if(size==0){
            return null;
        }
        return getFirst();
    }

    @Override
    public E peekLast() {
        if(size==0){
            return null;
        }
        return getLast();
    }

    @Override
    public boolean removeFirstOccurrence(Object o) {
        return remove(o);
    }

    @Override
    public boolean removeLastOccurrence(Object o) {
        ListIterator<E> it = listIterator(size);
        if (o==null) {
            while (it.hasPrevious()) {
                if (it.previous()==null) {
                    it.remove();
                    return true;
                }
            }
        } else {
            while (it.hasPrevious()) {
                if (o.equals(it.previous())) {
                    it.remove();
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean add(E e) {
        add(size,e);
        return true;
    }

    @Override
    public boolean offer(E e) {
        return offerFirst(e);
    }

    @Override
    public E remove() {
        return removeFirst();
    }

    @Override
    public E poll() {
        return pollFirst();
    }

    @Override
    public E element() {
        return getFirst();
    }

    @Override
    public E peek() {
        return peekFirst();
    }

    @Override
    public void push(E e) {
        addFirst(e);
    }

    @Override
    public E pop() {
        return removeFirst();
    }


    /**
     * 通过索引寻找数据是在哪个node里面，返回node的偏移量和迭代器
     * @param index
     * @return
     */
    private NodePosition<E> findDelegateNode(int index){
        if(size==0){
            return new NodePosition(null,0);
        }
        //如果index 在前半段就从前向后遍历查找，否则从后向前
        int middle = size>>1;
        if(index<=middle){
            return findDelegateNodeNext(index);
        }else{
            return findDelegateNodePre(index);
        }
    }

    private NodePosition<E> findDelegateNodePre(int index){
        Node<E> node = last;
        int offset = size - node.size;
        while(offset>index){
            node = node.prev;
            offset = offset - node.size;
        }
        int nodeIndex = index - offset;
        return new NodePosition(node,nodeIndex);
    }

    private NodePosition<E> findDelegateNodeNext(int index){
        Node<E> node = first;
        int offset = 0;
        while(node.size+offset-1<index){
            offset = offset + node.size;
            node = node.next;
        }
        int nodeIndex = index - offset;
        return new NodePosition(node,nodeIndex);
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        checkPositionIndex(index);
        return new ListItr(index);
    }


    private class ListItr implements ListIterator<E>{

        private int nextIndex;
        private Node<E> node;
        //数据在node中的位置
        private int nodeIndex;
        private int lastRet = -1;

        public ListItr(int nextIndex){
            this.nextIndex = nextIndex;
            NodePosition<E> nodePosition = findDelegateNode(nextIndex);
            node = nodePosition.node;
            nodeIndex = nodePosition.nodeIndex;
        }


        @Override
        public boolean hasNext() {
            return nextIndex<size;
        }

        @Override
        public E next() {
            if (!hasNext()){
                throw new NoSuchElementException();
            }
            //先遍历node 当node遍历完毕 再取下一个node
            if(nodeIndex>=node.size){
                node = node.next;
                nodeIndex = 0;
            }
            E element =  node.get(nodeIndex);
            lastRet = nodeIndex;
            nextIndex++;
            nodeIndex++;
            return element;
        }

        @Override
        public boolean hasPrevious() {
            return nextIndex > 0;
        }

        @Override
        public E previous() {
            if (!hasPrevious()){
                throw new NoSuchElementException();
            }
            nextIndex--;
            nodeIndex--;
            if(nodeIndex<0){
                node = node.prev;
                nodeIndex = node.size-1;
            }
            lastRet = nodeIndex;
            E element =  node.get(nodeIndex);
            return element;
        }

        @Override
        public int nextIndex() {
            return nextIndex;
        }

        @Override
        public int previousIndex() {
            return nextIndex-1;
        }

        @Override
        public void remove() {
            if (lastRet < 0)
                throw new IllegalStateException();
            E old = node.remove(lastRet);
            //如果节点为空 则删除节点
            if(node.size == 0){
                Node pre = node.prev;
                Node next = node.next;
                remove(node);
                if(nodeIndex != lastRet){
                    node = next;
                    nodeIndex = 0;
                    nextIndex--;
                }else{
                    node = pre;
                    if(node!=null){
                        nodeIndex = node.size;
                    }
                }
            }else{
                if(nodeIndex != lastRet){
                    nodeIndex--;
                    nextIndex--;
                }
            }
            lastRet=-1;
            size--;
        }

        @Override
        public void set(E e) {
            if (lastRet < 0)
                throw new IllegalStateException();
            node.set(lastRet,e);
        }

        @Override
        public void add(E e) {
            if(node == null){
                linkLast(e);
                node = last;
                nextIndex++;
                nodeIndex++;
                size++;
                return;
            }
            if (nodeIndex == 0){
                addBefor(e);
            }else if(nodeIndex == node.size){
                addAfter(e);
            }else{
                addAndDivision(e);
            }
            size++;
            if(lastRet == nextIndex){
                previous();
            }else{
                next();
            }
            lastRet = -1;
        }

        private void linkBefore(E element ,Node<E> node){
            Node prev = node.prev;
            boolean f = prev == null?true:false;
            Node newNode = new Node(nodeCapacity,element,f);
            newNode.prev = prev;
            newNode.next = node;
            node.prev = newNode;
            if(prev != null){
                prev.next = newNode;
            }else{
                node.first = false;
                first = newNode;
            }
        }

        private void linkLast(E element) {
            Node<E> l = last;
            boolean f = l == null?true:false;
            Node<E> newNode = new Node<E>(nodeCapacity, element, f);
            last = newNode;
            if (l == null){
                first = newNode;
            }else{
                l.next = newNode;
                newNode.prev = l;
            }
        }

        private void linkAfter(E element,Node<E> node){
            Node newNode = new Node(nodeCapacity,element,false);
            linkAfter(newNode,node);
        }

        private void linkAfter(Node<E> newNode,Node<E> node){
            Node next = node.next;
            newNode.prev = node;
            newNode.next = next;
            node.next = newNode;
            if(next != null){
                next.prev = newNode;
            }else{
                last = newNode;
            }
        }

        /**
         * 删除节点
         * @param x
         */
        private void remove(Node x){
            final Node<E> next = x.next;
            final Node<E> prev = x.prev;

            if (prev == null) {
                first = next;
                if(first!=null){
                    first.first = true;
                }
            } else {
                prev.next = next;
                x.prev = null;
            }

            if (next == null) {
                last = prev;
            } else {
                next.prev = prev;
                x.next = null;
            }
        }

        /**
         * 在node开始部分，如果node已满则插入到前一个node最后面，如果前一个node已满则新建一个node
         * @param element
         */
        private void addBefor(E element){
            if(!node.full()){
                node.add(0, element);
                //如果自身满了 找前一个节点
            }else {
                Node pre = node.prev;
                //如果前一个节点已满或者是头节点 则新建一个节点
                if(pre== null || pre.full() || pre.first){
                    linkBefore(element,node);
                }else{
                    pre.add(element);
                }
            }
        }

        /**
         * 在node结束部分 如果node已满则插入到后一个node头部，如果后一个node已满则新建一个node
         * @param element
         */
        private void addAfter(E element){
            //如果node不满 并且不是头节点 就直接添加到尾部
            if(!node.full()){
                node.add(element);
                return;
            }
            //如果已满,或者是头节点，则查看后一个节点
            Node next = node.next;
            if(next!=null){
                if(next.full()){
                    linkAfter(element,node);
                }else{
                    next.add(0,element);
                }
            }else{
                //是尾节点
                linkLast(element);
            }
        }

        /**
         * 在node中间部分，如果node已满则分裂成俩个node
         * @param element
         */
        private void addAndDivision(E element){
            //如果node不满 就直接添加
            if(!node.full()){
                node.add(nodeIndex,element);
                return;
            }
            //如果已满，则从nodeIndex处分裂成俩个node
            Node<E> left = node.copy(0,nodeIndex,node.first);
            Node<E> right = node.copy(nodeIndex,node.size-nodeIndex,false);
            left.add(element);
            linkAfter(left,node);
            linkAfter(right,left);
            remove(node);
            node = left;
        }
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return null;
    }

    private String outOfBoundsMsg(int index) {
        return "Index: "+index+", Size: "+size;
    }

    private void checkElementIndex(int index){
        if(index<0 || index>=size){
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
        }
    }

    private void checkPositionIndex(int index) {
        if (index<0||index>size){
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
        }
    }

    private class NodePosition<E>{
        Node<E> node;
        //数据在node中的位置
        int nodeIndex;

        public NodePosition(){

        }

        public NodePosition(Node<E> node,int nodeIndex){
            this.node = node;
            this.nodeIndex = nodeIndex;
        }
    }

    private class Node<E>{
        Object[] elementData;
        int size;
        int nodeCapacity;
        //为了优化offerFirst等操作,当first=true时 数据存储是从elementData的尾部开始存储
        boolean first;
        int startIndex;
        Node<E> next;
        Node<E> prev;

        public Node(int nodeCapacity,boolean first){
            elementData = new Object[nodeCapacity];
            this.nodeCapacity = nodeCapacity;
            this.first = first;
            if(first){
                startIndex = nodeCapacity;
            }
        }

        public Node(int nodeCapacity,E element,boolean first){
            this(nodeCapacity,first);
            if(first){
                add(0,element);
            }else{
                add(element);
            }
        }


        public void add(int index, E element){
            if(first){
                if(index == 0){
                    elementData[--startIndex] = element;
                }else{
                    System.arraycopy(elementData, startIndex, elementData, --startIndex,
                            index);
                    elementData[startIndex + index] = element;
                }
                size++;
                return;
            }
            if(index == size){
                add(element);
                return;
            }
            System.arraycopy(elementData, index, elementData, index + 1,
                    size - index);
            elementData[index] = element;
            size++;
        }

        public void add(E element){
            if(first){
                add(size,element);
                return;
            }
            elementData[size] = element;
            size++;
        }

        public E remove(int index){
            E oldValue = null;
            if(first){
                index = startIndex+index;
                oldValue = (E)elementData[index];
                int numMoved = index-startIndex;
                if(numMoved >0){
                    System.arraycopy(elementData, startIndex, elementData, startIndex+1,
                            numMoved);
                }
                elementData[startIndex]=null;
                startIndex++;
                size--;
            }else{
                oldValue = (E)elementData[index];
                int numMoved = size - index - 1;
                if (numMoved > 0){
                    System.arraycopy(elementData, index+1, elementData, index,
                            numMoved);
                }
                elementData[--size] = null;
            }
            return oldValue;
        }

        public E set(int index,E element){
            if(first){
                index = index + startIndex;
            }
            E oldValue = (E)elementData[index];
            elementData[index] = element;
            return oldValue;
        }

        public E get(int index){
            if(first){
                index = index + startIndex;
            }
            return (E)elementData[index];
        }

        public boolean full(){
            if(first){
                return startIndex <= 0;
            }
            return size >= nodeCapacity;
        }

        public Node<E> copy(int position,int length,boolean first){
            if(this.first){
                position = position + startIndex;
            }
            Node newNode = new Node(nodeCapacity,first);
            //头节点复制
            if(first){
                System.arraycopy(elementData, position, newNode.elementData, nodeCapacity-length,
                        length);
                newNode.startIndex = nodeCapacity-length;
            }else{
                System.arraycopy(elementData, position, newNode.elementData, 0,
                        length);
            }
            newNode.size = length;
            return newNode;
        }
    }

    public static void main(String[] args) {
        QuickList quickList = new QuickList(3);
        ListIterator<Integer> listIterator = quickList.listIterator();
        for(int i=0;i<10;i++){
            listIterator.add(i);
        }
        listIterator = quickList.listIterator();
        for(int i=0;i<10;i++){
            System.err.println(listIterator.next());
            System.err.println(listIterator.previous());
        }
        System.err.println(quickList);
    }
}
