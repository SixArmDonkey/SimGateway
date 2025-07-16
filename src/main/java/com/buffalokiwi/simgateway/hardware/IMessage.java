/**
 * Copyright (c) 2025 John T Quinn III, <johnquinn3@gmail.com>
 *
 * This file is part of the DCSBridge package, and is subject to the terms and conditions defined in file 'LICENSE',
 * which is part of this source code package.
 */
package com.buffalokiwi.simgateway.hardware;


/**
 * Represents a message being sent to a device 
 */
public interface IMessage 
{
  /**
   * Get the payload 
   * @return 
   */
  public byte[] getBytes();
}
