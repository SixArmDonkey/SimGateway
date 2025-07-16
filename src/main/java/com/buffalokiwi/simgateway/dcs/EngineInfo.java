/**
 * Copyright (c) 2025 John T Quinn III, <johnquinn3@gmail.com>
 *
 * This file is part of the DCSBridge package, and is subject to the terms and conditions defined in file 'LICENSE',
 * which is part of this source code package.
 */
package com.buffalokiwi.simgateway.dcs;

import com.buffalokiwi.simgateway.state.BooleanState;
import com.buffalokiwi.simgateway.state.FloatState;
import com.buffalokiwi.simgateway.state.IStateEventManager;
import com.buffalokiwi.utils.Logs;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Stores engine info 
 * @author John Quinn
 */
public class EngineInfo 
{
  /**
   * The number of variables expected in the dcs payload 
   */
  private static final int NUM_VARS = 12;
  
  private static final Logger LOG = LogManager.getLogger( EngineInfo.class );
    
  private final FloatState fuelInternal;
  private final FloatState fuelExternal;
  private final FloatState tempLeft;
  private final FloatState tempRight;
  private final FloatState rpmLeft;
  private final FloatState rpmRight;
  private final FloatState fuelConsumptionLeft;
  private final FloatState fuelConsumptionRight;
  private final BooleanState engineStartLeft;
  private final BooleanState engineStartRight;
  private final FloatState pressureLeft;
  private final FloatState pressureRight;
  
  
  public EngineInfo( final IStateEventManager stateManager )
  {
    fuelInternal = new FloatState( Control.ENGINE_INFO_FUEL_INTERNAL, stateManager );
    fuelExternal = new FloatState( Control.ENGINE_INFO_FUEL_EXTERNAL, stateManager );
    tempLeft = new FloatState( Control.ENGINE_INFO_TEMP_LEFT, stateManager, 1 );
    tempRight = new FloatState( Control.ENGINE_INFO_TEMP_RIGHT, stateManager, 1 );
    rpmLeft = new FloatState( Control.ENGINE_INFO_RPM_LEFT, stateManager, 0 );
    rpmRight  = new FloatState( Control.ENGINE_INFO_RPM_RIGHT, stateManager, 0 );
    fuelConsumptionLeft = new FloatState( Control.ENGINE_INFO_RPM_LEFT, stateManager );
    fuelConsumptionRight = new FloatState( Control.ENGINE_INFO_RPM_RIGHT, stateManager );
    engineStartLeft = new BooleanState( Control.ENGINE_INFO_ENGINE_START_LEFT, stateManager );
    engineStartRight = new BooleanState( Control.ENGINE_INFO_ENGINE_START_RIGHT, stateManager );
    pressureLeft = new FloatState( Control.ENGINE_INFO_HYDRAULIC_PRESSURE_LEFT, stateManager );
    pressureRight = new FloatState( Control.ENGINE_INFO_HYDRAULIC_PRESSURE_RIGHT, stateManager );
  }
  
  
  /**
   * This expects a comma-delimited string sent from DCS
   *  engineInfo.fuel_external 
   *  .. "," .. engineInfo.fuel_internal
   *  .. "," .. engineInfo.Temperature.left
   *  .. "," .. engineInfo.Temperature.right
   *  .. "," .. engineInfo.RPM.left
   *  .. "," .. engineInfo.RPM.right
   *  .. "," .. engineInfo.FuelConsumption.left
   *  .. "," .. engineInfo.FuelConsumption.right
   *  .. "," .. engineInfo.EngineStart.left
   *  .. "," .. engineInfo.EngineStart.right
   *  .. "," .. engineInfo.HydraulicPressure.left
   *  .. "," .. engineInfo.HydraulicPressure.right
   * @param dcsPayload 
   */
  public void update( final String dcsPayload )
  {
    if ( dcsPayload == null || dcsPayload.isEmpty())
      return;
    
    final String[] data = dcsPayload.split( "," );
    if ( data.length != NUM_VARS )
    {
      Logs.error( LOG, "DCS Engine Info payload contained an invalid number of elements.  Expected", NUM_VARS, "got", data.length );
      return;
    }
    
    try {
      fuelInternal.set( Float.valueOf( data[0] ));
    } catch( NumberFormatException e ) {}
  
    try {
      fuelExternal.set( Float.valueOf( data[1] ));
    } catch( NumberFormatException e ) {}
  
    try {
      tempLeft.set( Float.valueOf( data[2] ));
    } catch( NumberFormatException e ) {}
    
    try {
      tempRight.set( Float.valueOf( data[3] ));
    } catch( NumberFormatException e ) {}
    
    try {
      rpmLeft.set( Float.valueOf( data[4] ));
    } catch( NumberFormatException e ) {}
      
    try {
      rpmRight.set( Float.valueOf( data[5] ));
    } catch( NumberFormatException e ) {}
    
    try {
      fuelConsumptionLeft.set( Float.valueOf( data[6] ));
    } catch( NumberFormatException e ) {}
    
    try {
      fuelConsumptionRight.set( Float.valueOf( data[7] ));
    } catch( NumberFormatException e ) {}
    
    try {
      engineStartLeft.set( data[8].equals( "1" ));
    } catch( NumberFormatException e ) {}
    
    try {
      engineStartRight.set( data[9].equals( "1" ));
    } catch( NumberFormatException e ) {}
    
    
    try {
      pressureLeft.set( Float.valueOf( data[10] ));
    } catch( NumberFormatException e ) {}
    
    try {
      pressureRight.set( Float.valueOf( data[11] ));
    } catch( NumberFormatException e ) {}
  }
  
  
  /**
   * Resets the internal engine state 
   */
  public void reset()
  {
    fuelInternal.set( 0f );
    fuelExternal.set( 0f );
    rpmLeft.set( 0f );
    rpmRight.set( 0f );
    fuelConsumptionLeft.set( 0f );
    fuelConsumptionRight.set( 0f );
    engineStartLeft.set( false );
    engineStartRight.set( false );
    pressureLeft.set( 0f );
    pressureRight.set( 0f );
  }
}
