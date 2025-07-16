/**
 * Copyright (c) 2025 John T Quinn III, <johnquinn3@gmail.com>
 *
 * This file is part of the DCSBridge package, and is subject to the terms and conditions defined in file 'LICENSE',
 * which is part of this source code package.
 */
package com.buffalokiwi.simgateway.state;


/**
 * A state event for booleans 
 * @author John Quinn 
 */
public class BooleanStateEvent extends GenericStateEvent<Boolean>
{
  public BooleanStateEvent( final ISimControl control, final boolean value, final boolean oldValue )
  {
    super( control, value, oldValue );
  }
  
  
  @Override
  public String toString()
  {
    return ( getValue()) ? "1" : "0";
  }
}
