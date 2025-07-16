/**
 * Copyright (c) 2025 John T Quinn III, <johnquinn3@gmail.com>
 *
 * This file is part of the DCSBridge package, and is subject to the terms and conditions defined in file 'LICENSE',
 * which is part of this source code package.
 */
package com.buffalokiwi.simgateway.state;

/**
 * 
 * @author John Quinn
 */
public class BooleanState extends VariableState<Boolean>
{
  public BooleanState( final ISimControl control, final IStateEventManager stateEventManager )
  {
    super( control, stateEventManager, false );
  }
  
  
  protected void registerEvent( final ISimControl control, final boolean value, final boolean oldValue )
  {
    getStateManager().<Boolean>registerEvent( control, value, oldValue );
  }
}
