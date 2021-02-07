package com.zj;


import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class ListTest {

    public Class<List>[] listClass = new Class[]{ArrayList.class,LinkedList.class,QuickList.class};

    /**
     * 随机插入十万条数据
     */
    @Test
    public void addRandomTest(){
        addRandom(100000);
    }

    /**
     * 分别从 头部 10% 30% 50% 70% 90% 尾部的位置插入十万条数据
     */
    @Test
    public void addPostionTest(){

        addPosition(100000,0);
        System.err.println();
        addPosition(100000,0.1);
        System.err.println();
        addPosition(100000,0.3);
        System.err.println();
        addPosition(100000,0.5);
        System.err.println();
        addPosition(100000,0.7);
        System.err.println();
        addPosition(100000,0.9);
        System.err.println();
        addPosition(100000,1);
    }

    /**
     * 初始化一百万数据，随机查询十万次
     */
    @Test
    public void getRandomTest(){
        getRandom(1000000,100000);
    }

    @Test
    public void getPositionTest(){
        getPosition(100000,100000,0);
        System.err.println();
        getPosition(100000,100000,0.1);
        System.err.println();
        getPosition(100000,100000,0.3);
        System.err.println();
        getPosition(100000,100000,0.5);
        System.err.println();
        getPosition(100000,100000,0.7);
        System.err.println();
        getPosition(100000,100000,0.9);
        System.err.println();
        getPosition(100000,100000,1);
    }




    public void addPosition(int count, double ratio){
        for(Class<List> c:listClass){
            List list = create(c);
            addPosition(list,count,ratio);
        }
    }

    public  void addPosition(List<Integer> list, int count, double ratio){
        long begin = System.currentTimeMillis();
        for(int i=1;i<count;i++){
            int position = (int)(list.size() * ratio);
            list.add(position,i);
        }
        System.err.println(System.currentTimeMillis()-begin+"----"+list.getClass()+"----add position ratio"+ratio);
    }

    public  int[] randoms(int count){
        Random random = new Random();
        int[] randoms = new int[count];
        randoms[0] = 0;
        for(int i=1;i<count;i++){
            randoms[i] = random.nextInt(i);
        }
        return randoms;
    }

    public List create(Class<List> c){
        try {
            List list = c.newInstance();
            return list;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addRandom(int count){
        int[] randoms = randoms(count);
        for(Class<List> c:listClass){
            List list = create(c);
            addRandom(list,randoms);
        }
    }

    public void getPosition(int initSize,int count,double ratio){
        for(Class<List> c:listClass){
            List list = create(c);
            init(list,initSize);
            getPosition(list,count,ratio);
        }
    }

    public void getPosition(List<Integer> list,int count,double ratio){
        int position = (int)((list.size()-1) * ratio);
        long begin = System.currentTimeMillis();
        for(int i=1;i<count;i++){
            list.get(position);
        }
        System.err.println(System.currentTimeMillis()-begin+"----"+list.getClass()+"----get position ratio"+ratio);
    }

    public void init(List list,int initSize){
        for(int i=0;i<initSize;i++){
            list.add(i);
        }
    }

    public void getRandom(int initSize,int count){
        int[] randoms = randoms(count);
        for(Class<List> c:listClass){
            List list = create(c);
            init(list,initSize);
            getRandom(list,randoms);
        }
    }

    public void getRandom(List<Integer> list,int[] randoms){
        long begin = System.currentTimeMillis();
        for(int i=0;i<randoms.length;i++){
            list.get(randoms[i]);
        }
        System.err.println(System.currentTimeMillis()-begin+"----"+list.getClass()+"----getRandom");
    }

    public  void addRandom(List<Integer> list,int[] randoms){
        long begin = System.currentTimeMillis();
        for(int i=0;i<randoms.length;i++){
            list.add(randoms[i],i);
        }
        System.err.println(System.currentTimeMillis()-begin+"----"+list.getClass()+"----addRandom");
    }

}
