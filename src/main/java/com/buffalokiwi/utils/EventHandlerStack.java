/**
 * This file is part of the buffalokiwi utils package, and is subject to the 
 * terms and conditions defined in file 'LICENSE.txt', which is part 
 * of this source code package.
 *
 * Copyright (c) 2017 John Quinn <johnquinn3@gmail.com>
 */

package com.buffalokiwi.utils;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * A simple stack for event handlers 
 * @author John Quinn
 */
public class EventHandlerStack<T> implements IEventHandlerStack<T>
{
  /**
   * Handlers 
   */
  private final List<Consumer<T>> stack = new CopyOnWriteArrayList<>();
  
  
  /**
   * Add some handler to the stack 
   * @param t handler
   */
  @Override
  public void add( final Consumer<T> t )
  {
    if ( t == null )
      throw new IllegalArgumentException( "t must not be null" );
    stack.add( t );    
  }
  
  
  /**
   * Add some handler to the head of the stack 
   * @param t handler 
   */
  @Override
  public void addFirst( final Consumer<T> t )
  {
    if ( t == null )
      throw new IllegalArgumentException( "t must not be null" );
    stack.add( 0, t );
  }
  
  
  /**
   * Remove some handler from the stack 
   * @param t handler 
   * @return if removed
   */
  @Override
  public boolean remove( final Consumer<T> t )
  {
    if ( t == null )
      throw new IllegalArgumentException( "t must not be null" );
    return stack.remove( t );
  }
  
  
  /**
   * Executes the handlers 
   * @param t value to send 
   */
  @Override
  public void execute( final T t )
  {
    stack.forEach( v -> v.accept( t ));
  }
}
