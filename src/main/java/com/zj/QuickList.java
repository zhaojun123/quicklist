package com.zj;

import java.util.*;

public class QuickList<E> extends AbstractCollection<E> implements List<E>, Cloneable, java.io.Serializable{


    private LinkedList<Node<E>> delegate;

    /**
     * 每个linkedList节点都是一个arrayList,默认大小为100
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
        delegate = new LinkedList();
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
    public boolean add(E e) {
        add(size,e);
        return true;
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        return false;
    }

    @Override
    public E get(int index) {
        checkElementIndex(index);
        NodePosition<E> nodePosition = findDelegateNode(index);
        return nodePosition.node.get(index-nodePosition.offset);
    }

    @Override
    public E set(int index, E element) {
        return null;
    }

    @Override
    public void add(int index, E element) {
        checkPositionIndex(index);
        if(size == 0){
            Node<E> node = new Node(nodeCapacity);
            delegate.add(node);
        }
        NodePosition<E> nodePosition = null;
        //插入末尾
        if(index == size){
            nodePosition = new NodePosition(delegate.peekLast());
        //插入头部
        }else if(index == 0){
            nodePosition = new NodePosition(delegate.peek());
        }else{
            //使用迭代器查找位置
            nodePosition = findDelegateNode(index);
        }
        add(element,nodePosition,index,nodePosition.delegateIterator);
        size++;
    }

    private void add(E element,NodePosition<E> nodePosition,int index,ListIterator<Node<E>> listIterator){
        if(index == 0 || index - nodePosition.offset == 0){
            addBefor(element,nodePosition,listIterator);
        }else if(index == size || index - nodePosition.offset == size){
            addAfter(element,nodePosition,listIterator);
        }else{
            addAndDivision(element,nodePosition,index,listIterator);
        }
    }

    /**
     * 在node开始部分，如果node已满则插入到前一个node最后面，如果前一个node已满则新建一个node
     * @param element
     * @param nodePosition
     * @param listIterator
     */
    private void addBefor(E element,NodePosition<E> nodePosition,ListIterator<Node<E>> listIterator){
        Node node = nodePosition.node;
        //如果node不满 就直接添加到头部
        if(!node.full()){
            node.add(0,element);
            return;
        }
        //如果已满，则查看前一个节点
        if(nodePosition.offset != 0){
            Node pre = listIterator.previous();
            if(pre.full()){
                Node newNode = new Node(nodeCapacity,element);
                listIterator.add(newNode);
            }else{
                pre.add(element);
            }
        }else{
            //是头节点
            Node newNode = new Node(nodeCapacity,element);
            delegate.addFirst(newNode);
        }
    }

    /**
     * 在node结束部分 如果node已满则插入到后一个node头部，如果后一个node已满则新建一个node
     * @param element
     * @param nodePosition
     * @param listIterator
     */
    private void addAfter(E element,NodePosition<E> nodePosition,ListIterator<Node<E>> listIterator){
        Node node = nodePosition.node;
        //如果node不满 就直接添加到尾部
        if(!node.full()){
            node.add(element);
            return;
        }
        //如果已满，则查看后一个节点
        if(nodePosition.offset != 0){
            Node next = listIterator.next();
            if(next.full()){
                Node newNode = new Node(nodeCapacity,element);
                listIterator.previous();
                listIterator.add(newNode);
            }else{
                next.add(0,element);
            }
        }else{
            //是尾节点
            Node newNode = new Node(nodeCapacity,element);
            delegate.add(newNode);
        }
    }

    /**
     * 在node中间部分，如果node已满则分裂成俩个node
     * @param element
     * @param nodePosition
     * @param listIterator
     */
    private void addAndDivision(E element,NodePosition<E> nodePosition,int index,ListIterator<Node<E>> listIterator){
        Node node = nodePosition.node;
        int offset = index-nodePosition.offset;
        //如果node不满 就直接添加
        if(!node.full()){
            node.add(offset,element);
            return;
        }
        //如果已满，则从offset处分裂成俩个node
        Node<E> left = node.copy(0,offset);
        Node<E> right = node.copy(offset,node.size-offset);
        left.add(element);
        listIterator.remove();
        listIterator.add(left);
        listIterator.add(right);
    }

    /**
     * 通过索引寻找数据是在哪个node里面，返回node的偏移量和迭代器
     * @param index
     * @return
     */
    private NodePosition<E> findDelegateNode(int index){
        if(size == 0 || index == size){
            return new NodePosition(null,0,delegate.listIterator());
        }
        //如果index 在前半段就从前向后遍历查找，否则从后向前
        int middle = size>>1;
        if(index<=middle){
            return findDelegateNodeAfter(index);
        }else{
            return findDelegateNodePre(index);
        }
    }

    private NodePosition<E> findDelegateNodePre(int index){
        ListIterator<Node<E>> listIterator = delegate.listIterator(delegate.size());
        Node<E> node = listIterator.previous();
        int offset = size - node.size;
        while(offset>index){
            node = listIterator.previous();
            offset = offset - node.size;
        }
        return new NodePosition(node,offset,listIterator);
    }

    private NodePosition<E> findDelegateNodeAfter(int index){
        ListIterator<Node<E>> listIterator = delegate.listIterator();
        Node<E> node = listIterator.next();
        int offset = 0;
        while(node.size+offset-1<index){
            offset = offset + node.size;
            node = listIterator.next();
        }
        return new NodePosition(node,offset,listIterator);
    }

    @Override
    public E remove(int index) {
        return null;
    }

    @Override
    public int indexOf(Object o) {
        return 0;
    }

    @Override
    public int lastIndexOf(Object o) {
        return 0;
    }

    @Override
    public ListIterator<E> listIterator() {
        return listIterator(0);
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        checkPositionIndex(index);
        return new ListItr(index);
    }

    private class ListItr implements ListIterator<E>{

        private int nextIndex;
        private ListIterator<Node<E>> delegateIterator;
        private NodePosition<E> nodePosition;
        Node<E> node;
        //数据在node中的位置
        private int nodeIndex;

        public ListItr(int nextIndex){
            this.nextIndex = nextIndex;
            nodePosition = findDelegateNode(nextIndex);
            delegateIterator = nodePosition.delegateIterator;
            if(nodePosition.node!=null){
                node = nodePosition.node;
                nodeIndex = nextIndex-nodePosition.offset;
            }
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
            //先遍历node 当node遍历完毕 再从delegate中取下一个node
            if(nodeIndex>=node.size){
                node = delegateIterator.next();
                nodeIndex = 0;
            }
            E element =  node.get(nodeIndex);
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
            //这里需要处理node为null的情况判断
            if(node == null || nodeIndex<0){
                node = delegateIterator.previous();
                nodeIndex = node.size-1;
            }
            E element =  node.get(nodeIndex);
            nextIndex--;
            nodeIndex--;
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

        }

        @Override
        public void set(E e) {

        }

        @Override
        public void add(E e) {

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
        //node 在delegate中的偏移量
        int offset;
        //查找过程中创建的迭代器
        private ListIterator<Node<E>> delegateIterator;

        public NodePosition(){

        }

        public NodePosition(Node<E> node,int offset,ListIterator<Node<E>> delegateIterator){
            this.node = node;
            this.offset = offset;
            this.delegateIterator = delegateIterator;
        }

        public NodePosition(Node<E> node){
            this.node = node;
        }
    }

    private class Node<E>{
        Object[] elementData;
        int size;
        int nodeCapacity;

        public Node(int nodeCapacity){
            elementData = new Object[nodeCapacity];
            this.nodeCapacity = nodeCapacity;
        }

        public Node(int nodeCapacity,E element){
            this(nodeCapacity);
            add(element);
        }

        public void add(int index, E element){
            if(index == size){
                add(element);
                return;
            }else{
                System.arraycopy(elementData, index, elementData, index + 1,
                        size - index);
            }
            elementData[index] = element;
            size++;
        }

        public void add(E element){
            elementData[size] = element;
            size++;
        }

        public E remove(int index){
            E oldValue = (E)elementData[index];
            int numMoved = size - index - 1;
            if (numMoved > 0){
                System.arraycopy(elementData, index+1, elementData, index,
                        numMoved);
            }
            elementData[--size] = null;
            return oldValue;
        }

        public E set(int index,E element){
            E oldValue = (E)elementData[index];
            elementData[index] = element;
            return oldValue;
        }

        public E get(int index){
            return (E)elementData[index];
        }

        public boolean full(){
            return size >= nodeCapacity;
        }

        public Node<E> copy(int position,int length){
            Node newNode = new Node(nodeCapacity);
            length = Math.min(length,size-position);
            System.arraycopy(elementData, position, newNode.elementData, 0,
                    length);
            newNode.size = length;
            return newNode;
        }
    }
}
