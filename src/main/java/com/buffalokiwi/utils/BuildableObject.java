/**
 * This file is part of the buffalokiwi utils package, and is subject to the 
 * terms and conditions defined in file 'LICENSE.txt', which is part 
 * of this source code package.
 *
 * Copyright (c) 2017 John Quinn <johnquinn3@gmail.com>
 */

package com.buffalokiwi.utils;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author John Quinn
 */
public abstract class BuildableObject<R extends IBuildableObject, B extends IBuildableObject.Builder> implements IBuildableObject<R,B> 
{
  private final static Logger LOG = LogManager.getLogger( BuildableObject.class );
  
  public abstract static class Builder<T extends IBuildableObject.Builder, R extends IBuildableObject> implements IBuildableObject.Builder<T,R>
  {
    
    /**
     * Reference to this 
     */
    private final T reference = (T)this;
    
     
    
    /**
     * Retrieve a reference to this builder instance 
     * @return instance 
     */
    protected T getReference()
    {
      return reference;
    }       
  }
  
  
  /**
   * Subclass reference 
   */
  private final Class<? extends Builder> clazz;
  
  
  public BuildableObject( final Class<? extends Builder> builderClass, final Builder b )
  {
    if ( builderClass == null )
      throw new IllegalArgumentException( "clazz must not be null" );
    else if ( b == null )
      throw new IllegalArgumentException( "b must not be null" );    
    
    clazz = builderClass;
  }  
  
  /**
   * Convert this to a builder
   * @return builder
   */
  @Override
  public B toBuilder() 
  {
    try {
      final B b = (B)clazz.newInstance();
      return b;
    } catch( IllegalAccessException | InstantiationException e ) {
      Logs.error( LOG, e, "Failed to create an instance of", clazz.getName());
      throw new RuntimeException( "Builder creation failure" );
    }
  }   
}
