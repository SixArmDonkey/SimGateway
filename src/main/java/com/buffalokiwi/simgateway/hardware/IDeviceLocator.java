/**
 * Copyright (c) 2025 John T Quinn III, <johnquinn3@gmail.com>
 *
 * This file is part of the DCSBridge package, and is subject to the terms and conditions defined in file 'LICENSE',
 * which is part of this source code package.
 */

package com.buffalokiwi.simgateway.hardware;


/**
 * Locates devices 
 */
public interface IDeviceLocator 
{
  /**
   * Locates a device by the unique software address (control id)
   * @param address
   * @return device or null
   */
  public IDevice findDeviceBySoftwareAddress( final int address );
}
