package com.example.distributedsystems.distributed.systems.common;

import java.text.SimpleDateFormat;

/**
 * The helper class for common methods used at multiple client and server classes.
 */
public class Helper {
  /**
   * This helper method returns the current system datetime in the "yyyy-MM-dd HH:mm:ss.SSS" format.
   *
   * @return system datetime string.
   */
  public static String getTimestamp() {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    return dateFormat.format(System.currentTimeMillis());
  }
}
