package com.yuan.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FutureUtil {
    
    
    /**
     * 创建一个线程
     * 备注：Callable<String>的范型可以是任何类型，例如string，例如自定义对象
     *
     */
    public static class FutureThreadUtil implements Callable<String> {
        private String result;
        public FutureThreadUtil(String result) {
            this.result = result;
        }
        @Override
        public String call() {
            try {
                /*
                 * 执行业务逻辑
                 */
                System.out.println(result);
                return result;
            } catch (Exception ex) {
                
            }
            return null;
        }
    }
    
    
    /**开启线程，执行业务逻辑，并可以获取每个线程的执行结果
     * @param threadNum：启动的线程数
     * @throws Exception
     */
    public void future(int threadNum) throws Exception{
        /*
         * 1、开启threadNum个线程池
         */
        ExecutorService service = Executors.newFixedThreadPool(threadNum); //开启线程池
        /*
         * 2、执行业务逻辑
         */
        List<Future<String>> list = new ArrayList<Future<String>>();  //获取线程执行结果
        for (int i = 0; i < threadNum; i++) {
            FutureThreadUtil f = new FutureThreadUtil(i+"");
            Future<String> ff = service.submit(f);
            list.add(ff);
        }
        /*
         * 3、获取每个线程执行结果
         */
        int futureSize = list.size();
        for (int i = 0; i < futureSize; i++) {
            Future<String> resultFuture = list.get(i);
            String result = resultFuture.get(); //执行结果
            System.out.println(result);
        }
    }
    
    public static void main(String[] args) throws Exception{
        FutureUtil futureUtil = new FutureUtil();
        futureUtil.future(5);
    }
    
}
