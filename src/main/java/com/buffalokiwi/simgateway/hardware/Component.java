/**
 * Copyright (c) 2025 John T Quinn III, <johnquinn3@gmail.com>
 *
 * This file is part of the DCSBridge package, and is subject to the terms and conditions defined in file 'LICENSE',
 * which is part of this source code package.
 */

package com.buffalokiwi.simgateway.hardware;

import com.buffalokiwi.simgateway.SimType;


/**
 * An immutable component - this represents a physical piece of hardware on a physical device
 */
public class Component implements IComponent
{
  /**
   * A builder for making immutable components 
   */
  public static class Builder
  {
    private SimType sim = null;
    private ComponentType type = null;
    private String name = "";
    private String desc = "";
    private int address = 0;
    private int hardwareAddress = 0;
    
    public Builder setSim( final SimType t )
    {
      sim = t;
      return this;
    }


    public Builder setType( final ComponentType t )
    {
      type = t;
      return this;
    }
    
    
    public Builder setName( final String name )
    {
      this.name = name;
      return this;
    }
    
    
    public Builder setDescription( final String desc )
    {
      this.desc = desc;
      return this;
    }
    
    
    public Builder setAddress( final int address )
    {
      this.address = address;
      return this;
    }
    
    
    public Builder setHardwareAddress( final int address )
    {
      this.hardwareAddress = address;
      return this;
    }
    
    
    public Component build() throws IllegalArgumentException
    {
      return new Component( this );
    }
  }
  
  
  private final SimType sim;
  private final ComponentType type;
  private final String name;
  private final String desc;
  private final int address;
  private final int hardwareAddress;
  
  
  protected Component( final Builder b ) 
  {
    if ( b.sim == null )
      throw new IllegalArgumentException( "component sim must not be null" );
    else if ( b.type == null )
      throw new IllegalArgumentException( "component type must not be null" );
    else if ( b.name == null || b.name.isEmpty())
      throw new IllegalArgumentException( "component name must not be null or empty" );
    else if ( b.desc == null )
      throw new IllegalArgumentException( "component description must not be null" );
    else if ( b.address < 0 )
      throw new IllegalArgumentException( "component address must be an unsigned integer" );
    else if ( b.hardwareAddress < 0 )
      throw new IllegalArgumentException( "component hardware address must be an unsigned integer" );
    
    
    this.sim = b.sim;
    this.type = b.type;
    this.name = b.name;
    this.desc = b.desc;
    this.address = b.address;
    this.hardwareAddress = b.hardwareAddress;
  }
  
  
  @Override
  public ComponentType getType()
  {
    return type;
  }
  
  
  @Override
  public String getName()
  {
    return name;
  }
  
  
  @Override
  public String getDescription()
  {
    return desc;
  }
  
  
  @Override
  public int getAddress()
  {
    return address;
  }
  
  
  @Override
  public int getHardwareAddress()
  {
    return hardwareAddress;
  }
  
  
  @Override
  public SimType getSimType()
  {
    return sim;
  }
}
