package com.yude.game.common.timeout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: HH
 * @Date: 2020/8/4 10:46
 * @Version: 1.0
 * @Declare:
 */
public enum MahjongTimeoutTaskPool implements TimeoutTaskPool{

    /**
     * 唯一实例
     */
    INSTANCE;

    private AtomicInteger counter = new AtomicInteger(0);
    private int corePoolSize = 2;
    private int maximumPoolSize = 2;//Runtime.getRuntime().availableProcessors();
    private long keepAliveTime = 60;
    private TimeUnit unit = TimeUnit.SECONDS;
    //无界阻塞队列
    private BlockingQueue<Runnable> workQueue = new DelayQueue();

    private static ConcurrentLinkedQueue<TimeoutTask> linkedQueue = new ConcurrentLinkedQueue();
    private ThreadFactory threadFactory = (runnable) ->{
        Thread thread = new Thread(runnable);
        //守护线程 和 非守护线程，对开发人员而言，最大的区别在于线程结束时，守护线程不保证finally{}块的执行
        thread.setDaemon(true);
        thread.setName("TimeOutThread-"+counter.getAndIncrement());
        return thread;
    };
    private ThreadPoolExecutor executorService;

    //这里的Set需要是线程安全的。因为场景中存在两个线程：1读 1写
    //roomId -> stepCouts
    //public static Map<Long, Set<Integer>> uselessTaskMap = new ConcurrentHashMap();
    //roomId -> maxStepCout
    public static final Map<Long, Integer> uselessTaskMap = new ConcurrentHashMap();

    //public static Map<Long, Integer> effectiveTaskMap = new ConcurrentHashMap();

    private static final Logger log = LoggerFactory.getLogger(MahjongTimeoutTaskPool.class);

    MahjongTimeoutTaskPool() {
        //注意事项：由于队列满了之后，再有任务来，在核心线程数全启动了的情况下，启动新的线程（非核心线程），任务又会直接提交给线程执行，又不用到队列。为了避免这个情况，核心线程数与最大线程数要一致
        executorService = new ThreadPoolExecutor(corePoolSize,maximumPoolSize,keepAliveTime,unit,workQueue,threadFactory);
        //个注意事项：由于ThreadPoolExecutor的机制是，当线程数小于核心线程数时，是先创建一个线程，任务直接提交给线程处理，而不是加入到队列中，所以不先提前启动所有核心线程，会导致延时任务失效
        executorService.prestartAllCoreThreads();
    }

    public static MahjongTimeoutTaskPool getInstance(){
        return INSTANCE;
    }

    public static int  getMaxStep(long roomId){
        return uselessTaskMap.get(roomId);
    }

    public void addUseLessTask(Long roomId,int step){
        log.info("增加失效的超时任务： roomId={}  step={}",roomId,step);
        uselessTaskMap.put(roomId,step);
    }

    /**
     * 一个房间一个时间段（一次操作范围内），只有一个有效的超时任务
     * 当有下一个操作时，前面的超时任务应该全部失效，所以这里用map来覆盖
     * 这个想法无法实现
     *
     *
     *
     */
    /*public void addEffectiveTask(Long roomId,int step,TimeoutTask timeoutTask){
        effectiveTaskMap.put(roomId,step);
        addTask(timeoutTask);
    }*/

    public void uselessTaskMapClear(Long roomId){
        uselessTaskMap.remove(roomId);
    }

    @Override
    public void addTask(TimeoutTask timeOutTask) {
        linkedQueue.offer(timeOutTask);
        //executorService.execute(timeOutTask);
        //log.info("添加超时任务：{}  \n 队列任务:{}",timeOutTask,workQueue);
    }

    public void init(){
        //只执行一次

        Thread thread = new Thread(() ->execute());
        thread.setDaemon(true);
        thread.setName("Thread-task-add");
        thread.start();
    }

    public void execute(){
        while(!Thread.currentThread().isInterrupted()){
            TimeoutTask timeOutTask = linkedQueue.poll();
            if(timeOutTask != null){
                executorService.execute(timeOutTask);
                continue;
            }
            try {
                if(uselessTaskMap.isEmpty()){
                    Thread.sleep(100);
                }else{
                    Thread.sleep(1);
                }
            } catch (InterruptedException e) {
                log.error("超时任务线程休眠异常",e);
            }
        }
    }
}
