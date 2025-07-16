/**
 * Copyright (c) 2025 John T Quinn III, <johnquinn3@gmail.com>
 *
 * This file is part of the DCSBridge package, and is subject to the terms and conditions defined in file 'LICENSE',
 * which is part of this source code package.
 */
package com.buffalokiwi.simgateway.dcs;

import com.buffalokiwi.simgateway.state.ISimControl;
import com.buffalokiwi.simgateway.SimType;


/**
 * Defines a list of control constants 
 * @author John Quinn
 */
public enum Control implements ISimControl<Control>
{
  NONE( 0, "Not Specified" ),
  ENGINE_INFO_FUEL_EXTERNAL( 1, "Engine Fuel External" ),
  ENGINE_INFO_FUEL_INTERNAL( 2, "Engine Fuel Internal" ),
  ENGINE_INFO_TEMP_LEFT( 3, "Engine Temperature Left" ),
  ENGINE_INFO_TEMP_RIGHT( 4, "Engine Temperature Right" ),
  ENGINE_INFO_RPM_LEFT( 5, "Engine RPM Left" ),
  ENGINE_INFO_RPM_RIGHT( 6, "Engine RPM Right" ),
  ENGINE_INFO_FUEL_CONSUMPTION_LEFT( 7, "Engine Fuel Consumption Left" ),
  ENGINE_INFO_FUEL_CONSUMPTION_RIGHT( 8, "Engine Fuel Consumption Right" ),
  ENGINE_INFO_ENGINE_START_LEFT( 9, "Engine Start Left" ),
  ENGINE_INFO_ENGINE_START_RIGHT( 10, "Engine Start Right" ),
  ENGINE_INFO_HYDRAULIC_PRESSURE_LEFT( 11, "Engine Hydraulic Pressure Left" ),
  ENGINE_INFO_HYDRAULIC_PRESSURE_RIGHT( 12, "Engine Hydraulic Pressure Right" );
  
  private final int controlId;
  private final String caption;
  
  
  public static Control byId( final int id ) 
  {
    for ( final Control c : values())
    {
      if ( c.getSoftwareAddress() == id )
        return c;
    }
    
    return Control.NONE;
  }
  
  
  private Control( final int controlId, final String caption )
  {
    this.controlId = controlId;
    this.caption = caption;
  }
  
  
  @Override
  public int getSoftwareAddress()
  {
    return controlId;
  }
  
  
  @Override
  public String getCaption()
  {
    return caption;
  }
  
  
  @Override
  public String toString()
  {
    return caption;
  }
  
  
  /**
   * Retrieve the sim that this control is for 
   * @return 
   */
  @Override
  public SimType getSimType()
  {
    return SimType.DCS_WORLD;
  }
  
  
  @Override
  public Control getControl()
  {
    return this;
  }
}
