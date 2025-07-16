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

/**
 *
 * @author John Quinn
 */
public interface IJobPool 
{

  public Future<?> submit(Runnable task);

  public <T> Future<T> submit(Runnable task, T result);

  public <T> Future<T> submit(Callable<T> task);
  
  public ExecutorService getExecutor();
}
