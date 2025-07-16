/**
 * This file is part of the buffalokiwi utils package, and is subject to the 
 * terms and conditions defined in file 'LICENSE.txt', which is part 
 * of this source code package.
 *
 * Copyright (c) 2017 John Quinn <johnquinn3@gmail.com>
 */


package com.buffalokiwi.utils;

/**
 *
 * @author John Quinn
 */
public interface IBuildableObject<R extends IBuildableObject, B extends IBuildableObject.Builder>
{
  /**
   * Builder interface 
   * @param <T> Type of builder (used for subclassing)
   * @param <R> Type of object the builder builds 
   */
  interface Builder<T extends Builder, R extends IBuildableObject>
  {
    /**
     * Build the record.
     * Override this.
     * @return immutable instance 
     */
    public R build() throws Exception;
  }
  
  
  /**
   * Convert this to a builder
   * @return builder
   */
  public B toBuilder();  
}
