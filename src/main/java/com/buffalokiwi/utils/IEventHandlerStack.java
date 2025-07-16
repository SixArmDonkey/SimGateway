/**
 * This file is part of the buffalokiwi utils package, and is subject to the 
 * terms and conditions defined in file 'LICENSE.txt', which is part 
 * of this source code package.
 *
 * Copyright (c) 2017 John Quinn <johnquinn3@gmail.com>
 */



package com.buffalokiwi.utils;

import java.util.function.Consumer;

/**
 *
 * @author John Quinn
 */
public interface IEventHandlerStack<T>
{
  /**
   * Add some handler to the stack 
   * @param t handler
   */
  public void add( final Consumer<T> t );
  
  
  /**
   * Add some handler to the head of the stack 
   * @param t handler 
   */
  public void addFirst( final Consumer<T> t );
  
  
  /**
   * Remove some handler from the stack 
   * @param t handler 
   * @return if removed
   */
  public boolean remove( final Consumer<T> t );
  
  
  /**
   * Executes the handlers 
   * @param t value to send 
   */
  public void execute( final T t );  
}
