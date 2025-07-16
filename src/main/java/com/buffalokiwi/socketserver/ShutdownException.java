/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.buffalokiwi.socketserver;

/**
 *
 * @author john
 */
public class ShutdownException extends RuntimeException {

  /**
   * Creates a new instance of <code>ShutdownExceptio</code> without detail
   * message.
   */
  public ShutdownException() {
  }

  /**
   * Constructs an instance of <code>ShutdownExceptio</code> with the specified
   * detail message.
   *
   * @param msg the detail message.
   */
  public ShutdownException(String msg) {
    super(msg);
  }
}
