/**
 * Copyright (c) 2025 John T Quinn III, <johnquinn3@gmail.com>
 *
 * This file is part of the DCSBridge package, and is subject to the terms and conditions defined in file 'LICENSE',
 * which is part of this source code package.
 */
package com.buffalokiwi.simgateway.state;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 
 */
public class FloatState extends VariableState<Float>
{
  private int scale;

  public FloatState( final ISimControl control, final IStateEventManager stateEventManager )
  {
    this( control, stateEventManager, 4 );
  }

  
  public FloatState( final ISimControl control, final IStateEventManager stateEventManager, final int scale )
  {
    super( control, stateEventManager, 0f );
    
    if ( scale < 0 )
      throw new IllegalArgumentException( "scale must be unsigned" );
    
    this.scale = scale;
  }
  
  @Override
  protected Float formatValue( Float value )
  {
    return BigDecimal.valueOf( value ).setScale( scale, RoundingMode.HALF_EVEN ).floatValue();
  }  
}
