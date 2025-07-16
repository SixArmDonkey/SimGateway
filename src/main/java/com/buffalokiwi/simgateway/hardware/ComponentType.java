/**
 * Copyright (c) 2025 John T Quinn III, <johnquinn3@gmail.com>
 *
 * This file is part of the DCSBridge package, and is subject to the terms and conditions defined in file 'LICENSE',
 * which is part of this source code package.
 */

package com.buffalokiwi.simgateway.hardware;

/**
 * Types of components we can talk to on the hardware
 */
public enum ComponentType 
{
  /**
   * A toggle switch
   */
  TOGGLE( "toggle", "Toggle" ),
  
  /**
   * A momentary push button
   */
  MOMENTARY( "momentary", "Momentary" ),
  
  /**
   * A rotary switch - it spins YAY! 
   */
  ROTARY( "rotary", "Rotary" ),
  
  /**
   * A character display 
   */
  LCD_CHARACTER( "lcd_character", "LCD" ),
  
  /**
   * An indicator light
   */
  LED( "led", "LED" );
  
  
  private final String name;
  private final String caption;
  
  
  public static ComponentType fromName( final String name )
  {
    for ( final ComponentType t : values())
    {
      if ( t.getName().equalsIgnoreCase( name ))
        return t;
    }
    
    return null;
  }
  
  
  ComponentType( final String name, final String caption )
  {
    this.name = name;
    this.caption = caption;
  }
  
  
  public String getName()
  {
    return name;
  }
  
  
  public String getCaption()
  {
    return caption;
  }
  
  
  @Override
  public String toString()
  {
    return name;
  }
}
