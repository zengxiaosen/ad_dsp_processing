package test;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Administrator on 2017/2/7.
 */
public class ABC_thread2 {
    /**
     * 还可以使用condition, condition的效率可能会更高一些, await会释放lock锁, condition的await和signal与object的wait和notify方法作用类似
     */

    private static Lock lock = new ReentrantLock();
    private static int count = 0;
    private static Condition A = lock.newCondition();
    private static Condition B = lock.newCondition();
    private static Condition C = lock.newCondition();

    static class ThreadA extends Thread{
        public void run(){
            lock.lock();
            try{
                for(int i=0; i< 10; i++){
                    while(count % 3 != 0){
                        A.await(); // 释放锁
                    }
                    System.out.println("A");
                    count++;
                    B.signal();//唤醒相应进程
                }
            }catch (InterruptedException e){
                e.printStackTrace();
            }finally {
                lock.unlock();
            }
        }
    }

    static class ThreadB extends Thread{
        public void run(){
            lock.lock();
            try{
                for(int i=0; i< 10; i++){
                    while(count % 3 != 1){
                        B.await();
                    }
                    System.out.println("B");
                    count++;
                    C.signal();
                }
            }catch (InterruptedException e){
                e.printStackTrace();
            }finally {
                lock.unlock();
            }
        }
    }

    static class ThreadC extends Thread{
        public void run(){
            lock.lock();
            try {
                for(int i=0; i< 10; i++){
                    while(count % 3 != 2){
                        C.await();
                    }
                    System.out.println("C");
                    count++;
                    A.signal();
                }
            }catch (InterruptedException e){
                e.printStackTrace();
            }finally {
                lock.unlock();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException{
        new ThreadA().start();
        new ThreadB().start();
        ThreadC threadC = new ThreadC();
        threadC.start();
        threadC.join();
        System.out.println(count);
    }
}
