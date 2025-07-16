/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.buffalokiwi.socketserver;

import java.util.List;
import java.util.Map;

/**
 *
 * @author John Quinn
 */
public interface ICommandPool 
{
  public static final int DEFAULT_GROUP_ID = 0;
  
  /**
   * Retrieve a list of commands for some group
   * @param groupId Group id 
   * @return 
   * @thows IllegalArgumentException if groupId does not exist 
   */
  public Map<String,ICommand> getCommands( final int groupId ) throws IllegalArgumentException;
  
  /**
   * Retrieve the list of group ids that contain commands 
   * @return group ids 
   */
  public List<Integer> getGroupIds();

  
  /**
   * If the command configuration is cumulative 
   * @return is cumulative
   */
  public boolean isCumulative();
}
