/**
 * Copyright (c) 2025 John T Quinn III, <johnquinn3@gmail.com>
 *
 * This file is part of the DCSBridge package, and is subject to the 
 * terms and conditions defined in file 'LICENSE', which is part 
 * of this source code package.
 */
package com.buffalokiwi.simgateway.config;

import java.io.File;

import com.beust.jcommander.IStringConverter;


/**
 * Used as part of the com.beyst.jcommander implementation in CommandLineArguments
 * Converts a string representation of a file name into a File object
 */
public class FileArgumentConverter implements IStringConverter<File>
{
  /**
   * Convert a string to a File 
   * @param value file name 
   * @return object 
   */
  @Override
  public File convert( final String value )
  {
    return new File( value );
  }
}
