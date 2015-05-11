package com.eddy.common.network;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPoolManager implements Runnable {
	
	public static int CORE_POOL_SIZE = 5;
	public static int MAX_POOL_SIZE = 50;
	public static int KEEP_ALIVE_TIME = 10000;
	
	private static ThreadPoolManager instance;
	
	public static ThreadPoolManager getInstance() {
		if (instance == null) {
			instance = new ThreadPoolManager();
		}
		return instance;
	}
	
    private ThreadPoolExecutor threadPool;
    private BlockingQueue<Runnable> workQueue = new SynchronousQueue<Runnable>();
    private ArrayList<Runnable> tasks = new ArrayList<Runnable>();
    private boolean isExit;
    
    private ThreadFactory threadFactory = new ThreadFactory() {
        private final AtomicInteger integer = new AtomicInteger();

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "myThreadPool thread:" + integer.getAndIncrement());
        }
    };
    
    public ThreadPoolManager() {
        threadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.MILLISECONDS, workQueue, threadFactory);
        threadPool.setRejectedExecutionHandler(new RejectedExecutionHandler() {
			@Override
			public void rejectedExecution(Runnable task, ThreadPoolExecutor executor) {
				addTask(task);
			}
        });
    }
    
    public void addTask(Runnable task) {
    	synchronized (tasks) {
    		tasks.add(task);
    	}
    }
    
    public boolean hasTask() {
    	synchronized (tasks) {
    		return tasks.size() > 0;
    	}
    }

    public Runnable removeTask() {
    	synchronized (tasks) {
    		if (tasks.size() > 0) {
    			if (threadPool.getActiveCount() < threadPool.getMaximumPoolSize()) {
    				Runnable task = tasks.remove(0);
    				return task;
    			} else {
    				return null;
    			}
    		} else {
    			return null;
    		}
    	}
    }

    public void execute(Runnable task) {
        threadPool.execute(task);
    }
    
    public void shutdown() {
    	while (hasTask()) {
    		try {
    			Thread.sleep(300);
    		} catch (InterruptedException ie) {}
    	}
    	isExit = true;
    	threadPool.shutdown();
    }
    
    public void start() {
    	new Thread(this).start();
    }
    
	@Override
	public void run() {
		while (false == isExit) {
			try {
				Runnable task = removeTask();
				if (task != null) {
					threadPool.execute(task);
				}
				Thread.sleep(300);
			} catch (InterruptedException ie) {}
		}
	}
}
