/**
 * This file is part of the buffalokiwi utils package, and is subject to the 
 * terms and conditions defined in file 'LICENSE.txt', which is part 
 * of this source code package.
 *
 * Copyright (c) 2017 John Quinn <johnquinn3@gmail.com>
 */

package com.buffalokiwi.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 *
 * @author John Quinn
 * @param <T>
 */
public class ObjectPool<T extends Object> implements AutoCloseable
{
  private final BlockingQueue<T> queue;
  private final List<T> objectCollection;
  private final AtomicBoolean running = new AtomicBoolean( true );
  
  
  @Override
  public void close() throws Exception {        
    running.getAndSet( false );
  }
  
  
  public ObjectPool( final List<T> objects )
  {
    queue = new ArrayBlockingQueue<>( objects.size());
    objectCollection = Collections.unmodifiableList( new ArrayList<>( objects ));
    queue.addAll( objectCollection );    
  }
  
  
  
  public ObjectPool( final T... objects )
  {
    queue = new ArrayBlockingQueue<>( objects.length );
    objectCollection = Collections.unmodifiableList( Arrays.<T>asList( objects ));
    queue.addAll( objectCollection );        
  }
  
  
  public T checkout()
  {
    T result = null;
    do
    {
      checkState();
      try {
        result = queue.poll( 1, TimeUnit.SECONDS );
      } catch( InterruptedException e ) {
        Thread.currentThread().interrupt();
        throw new IllegalStateException( "Thread has been interrupted." );
      }
      
    } while( result == null );
    
    return result;
  }
  
  
  public void checkin( final T object )
  {
    if ( object == null )
      throw new IllegalArgumentException( "object must not be null" );
    
    boolean result = false;
    do 
    {
      checkState();
      try {
        result = queue.offer( object, 1, TimeUnit.SECONDS );
      } catch( InterruptedException e ) {
        Thread.currentThread().interrupt();
        throw new IllegalStateException( "Thread has been interrupted." );
      }
    } while( !result );
  }
  
  
  
  public int size()
  {
    return objectCollection.size();
  }
  
  
  /**
   * This should not exist.
   * @return 
   */
  protected final List<T> getObjectCollection()
  {
    return objectCollection;
  }
  
  
  private void checkState() throws IllegalStateException 
  {
    if ( !running.get())
      throw new IllegalArgumentException( "ObjectPool has been shut down" );
  }
}
