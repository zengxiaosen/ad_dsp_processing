package test;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Administrator on 2017/2/7.
 */
public class ABC_thread {
    /**
     * 大概的问题是这样的:
     有A,B,C三个线程, A线程输出A, B线程输出B, C线程输出C
     要求, 同时启动三个线程, 按顺序输出ABC, 循环10次
     这是一个多线程协同的问题, 本身多线程是没有执行顺序的, 顺序不一定, Java在concurrent里面提供了多线程同步的支持
     使用ReentrantLock来解决, 还有个state整数用来判断轮到谁执行了
     */

    private static Lock lock = new ReentrantLock();//通过jdk中的锁来保证线程的访问互斥
    private static int state = 0;
    static class ThreadA extends Thread{
        public void run(){
            for(int i=0; i< 10;){
                lock.lock();
                if(state % 3 == 0){
                    System.out.println("A");
                    state++;
                    i++;
                }
                lock.unlock();
            }
        }
    }
    static class ThreadB extends Thread{
        public void run(){
            for(int i=0; i< 10;){
                lock.lock();
                if(state % 3 == 1){
                    System.out.println("B");
                    state++;
                    i++;
                }
                lock.unlock();
            }
        }
    }

    static class ThreadC extends Thread{
        public void run(){
            for(int i=0; i< 10;){
                lock.lock();
                if(state % 3 == 2){
                    System.out.println("C");
                    state++;
                    i++;
                }
                lock.unlock();
            }
        }
    }

    public static void main(String[] args){
        /**
         * 使用lock来保证只有一个线程在输出操作, 要保证了state不会被两个线程同时修改, 思路简单
         */
        new ThreadA().start();
        new ThreadB().start();
        new ThreadC().start();
    }
}
