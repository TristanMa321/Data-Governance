package com.atguigu.dga;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * @date 2024-02-05
 *
 */
/*
    三个概念:
        1.线程       物理概念
        2.线程池     把线程池化。节省反复创建线程的开销，时间。
        3.Task任务(控制)       进程或线程要执行的工作流程(代码)

     线程编程:
        JDK1.0  继承Thread。  占用了extends关键字
        JDK1.0 实现Runnable。 没有返回值，不能抛异常
        JDK1.5 实现Callable。  可以抛异常，可以有返回值

        前面的都不用

 */

public class ThreadDemo {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        System.out.println(Thread.currentThread().getName() +"运行了...");
        //启动线程是start()
        //new Task1().start();

        //实现Runnable
        //new Thread(new Task2()).start();

        //实现Callable
        Task3 task3 = new Task3();
        FutureTask<String> futureTask = new FutureTask<>(task3);
        new Thread(futureTask).start();
        //接收返回值. get()是一个阻塞的方法
        System.out.println(futureTask.get());

        System.out.println("xixixi");

    }

}

class Task1 extends  Thread{
    @Override
    public void run() {
        //编写要执行的任务
        System.out.println(Thread.currentThread().getName() +"运行了...");
    }
}

class Task2 implements   Runnable{
    @Override
    public void run() {
        //编写要执行的任务
        System.out.println(Thread.currentThread().getName() +"运行了...");
    }
}

class Task3 implements Callable<String> {

    //要执行的任务
    @Override
    public String call() throws Exception {
        Thread.sleep(5000);
        System.out.println(Thread.currentThread().getName() +"运行了...");
        return "ok";
    }
}
