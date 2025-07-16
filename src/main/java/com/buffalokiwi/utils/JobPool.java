/**
 * This file is part of the buffalokiwi utils package, and is subject to the 
 * terms and conditions defined in file 'LICENSE.txt', which is part 
 * of this source code package.
 *
 * Copyright (c) 2017 John Quinn <johnquinn3@gmail.com>
 */


package com.buffalokiwi.utils;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author John Quinn
 */
public class JobPool implements IJobPool 
{
  /**
   * Log
   */
  private final static Logger LOG = LogManager.getLogger(JobPool.class );
  
  /**
   * Task queue 
   */
  private final LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
  
  /**
   * The task executor thread pool 
   */
  private final ThreadPoolExecutor executor;
  
  private final String name;
  
  /**
   * Create a new SOAPExecutor instance
   */
  public JobPool( final int numThreads )
  {
    this( numThreads, "TaskExecutor" );    
  }
  
  
  public JobPool( final int numThreads, final String name )
  {
    executor = createExecutor( numThreads );       
    this.name = name;
  }


  @Override
  public Future<?> submit(Runnable task) {
    return executor.submit( task );
  }

  @Override
  public <T> Future<T> submit(Runnable task, T result) {
    return executor.submit( task, result );
  }

  @Override
  public <T> Future<T> submit(Callable<T> task) {          
    return executor.submit( task );    
  }
  
  
  
  

  @Override
  public ExecutorService getExecutor()
  {    
    return executor;
  }
  
  
  /**
   * Create a new ThreadPoolExecutor instance 
   * @return 
   */
  private ThreadPoolExecutor createExecutor( final int numThreads )
  {
    final long keepAlive = ( numThreads > 1 ) ? 30000L : 0L;
    return new ThreadPoolExecutor( numThreads, numThreads, keepAlive, TimeUnit.MILLISECONDS, queue, 
      //..ThreadFactory
      (Runnable r) -> {
        final Thread t = new Thread( r, name + " - " + r.getClass().getName());
        t.setUncaughtExceptionHandler((Thread t1, Throwable e) -> {
          LOG.error( "Uncaught exception in thread: " + t1.getName(), e );
        });

        return t;    
      },           

      //..RejectedExecutionHandler
      //..It simply waits for 1 second and resubmits the task for execution 
      (Runnable r, ThreadPoolExecutor exe) -> {
        LOG.warn( "Job Rejected, retrying in 1 second..." );

        try {
          Thread.sleep(1000);
          exe.execute( r );
        } catch (InterruptedException e) {
          LOG.error( "sleep interrupted", e );
          Thread.currentThread().interrupt();
          throw new IllegalStateException( "Thread has been interrupted." );          
        }
      }
    );    
  }
}
