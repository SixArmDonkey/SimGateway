/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.buffalokiwi.socketserver;

/**
 * Represents a client program that can run on the socket server 
 * @author John Quinn
 */
public interface IClient extends AutoCloseable 
{
  /**
   * Retrieve the client connection uptime 
   * @return uptime
   */
  public IUptime getUptime();
  
  
  /**
   * Retrieve the UUID used to identify the client connection 
   * @return UUID 
   */
  public String getUUID();
  
  
  /**
   * Retrieve the initial prompt text 
   * @return text 
   */
  public String getPromptText();
  
  
  /**
   * Run the client program.
   * @throws Exception 
   */
  public void run() throws Exception;
  
  
  public boolean isRunning();
  
  public boolean isExpired();
}
