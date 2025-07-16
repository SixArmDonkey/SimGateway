/**
 * Copyright (c) 2025 John T Quinn III, <johnquinn3@gmail.com>
 *
 * This file is part of the DCSBridge package, and is subject to the terms and conditions defined in file 'LICENSE',
 * which is part of this source code package.
 */
package com.buffalokiwi.simgateway;

/**
 * Supported sims 
 */
public enum SimType 
{
  DCS_WORLD( "dcs", "Digital Combat Simulator World" );
  
  private final String name;
  private final String caption;
  
  
  public static SimType fromName( final String name )
  {
    for ( final SimType t : values())
    {
      if ( t.getName().equalsIgnoreCase( name ))
        return t;
    }
    
    return null;
  }
  
  
  SimType( final String name, final String caption )
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
    return caption;
  }
}
