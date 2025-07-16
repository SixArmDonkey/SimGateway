/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.buffalokiwi.socketserver;

/**
 * Represents some command and data entered by the client 
 * @author John Quinn
 */
public interface ICommandInput 
{
  public String getCommand();
  public String getPayload();
}
