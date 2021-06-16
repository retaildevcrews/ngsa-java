package com.cse.ngsa.app.utils;

import com.microsoft.correlationvector.CorrelationVector;
import com.microsoft.correlationvector.CorrelationVectorVersion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.server.ServerWebExchange;

/**
 * Correlation Vector Extension class providing some functions.
 */
public class CorrelationVectorExtensions {

  private CorrelationVectorExtensions() {
  }

  private static final Logger logger = LogManager.getLogger(CorrelationVectorExtensions.class);

  /**
   * Get Correlation vector from ServerWebExchabge object.
   * 
   * @param webx ServerWebExchange object.
   * @return CorrelationVector.
   */
  public static CorrelationVector extend(ServerWebExchange webx) {

    if (webx == null) {
      throw new NullPointerException("ServerWebExchange is null");
    }

    CorrelationVector cv;
    var headers = webx.getRequest().getHeaders();
    // get the cv from the header
    if (headers.containsKey(CorrelationVector.HEADER_NAME)) {
      try {
        // extend the correlation vector
        var corrVectHeaders = headers.get(CorrelationVector.HEADER_NAME);

        // If corrVectHeaders is null, it will throw nullpointer exception
        if (corrVectHeaders.size() > 1) {
          logger.warn("More than one CorrelationVector Header was found");
        }

        cv = CorrelationVector.extend(corrVectHeaders.get(0));
      } catch (Exception ex) {
        // in case of exception
        // create a new correlation vector
        logger.warn(String.format("Excpetion thrown: %s", ex.getMessage()));
        cv = new CorrelationVector(CorrelationVectorVersion.V2);
      }
    } else {
      // create a new correlation vector
      cv = new CorrelationVector(CorrelationVectorVersion.V2);
    }

    return cv;
  }

  /**
   * Replace or Create new CorrelationVector attribute in ServerWebExchange.
   * It puts the CorrelationVector object as is with 
   * CorrelationVector.HEADER_NAME or "MS-CV" as key.
   * @param cv Correlation Vector.
   * @param webx ServerWebExchange Object.
   */
  public static void putCorrelationVectIntoAttribute(
      CorrelationVector cv, ServerWebExchange webx) {

    if (cv == null || webx == null) {
      throw new NullPointerException("CorrelationVector or SeverWebExchange arg is null!");
    }

    var attributes = webx.getAttributes();

    if (attributes.containsKey(CorrelationVector.HEADER_NAME)) {
      attributes.replace(CorrelationVector.HEADER_NAME, cv);
    } else {
      attributes.put(CorrelationVector.HEADER_NAME, cv);
    }
  }
}
