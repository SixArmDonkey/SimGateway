/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.buffalokiwi.socketserver;

import java.util.Date;

/**
 *
 * @author John Quinn
 */
public interface IUptime 
{
  /**
   * Retrieve the elapsed milliseconds
   * @return elapsed time
   */
  public long getElapsedMillis();
  

  /**
   * Retrieve the elapsed seconds
   * @return elapsed time
   */
  public long getElapsedSeconds();
  
  
  /**
   * Retrieve the elapsed time as a string 
   * @return elapsed time
   */
  public String getText();
  
  
  /**
   * Retrieve the start date/time
   * @return start date
   */
  public Date getStartDate();  
}
