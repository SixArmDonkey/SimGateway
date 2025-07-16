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
public class QuitException extends RuntimeException {

  /**
   * Creates a new instance of <code>QuitException</code> without detail
   * message.
   */
  public QuitException() {
  }

  /**
   * Constructs an instance of <code>QuitException</code> with the specified
   * detail message.
   *
   * @param msg the detail message.
   */
  public QuitException(String msg) {
    super(msg);
  }
}
