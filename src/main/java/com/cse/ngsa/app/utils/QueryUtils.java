package com.cse.ngsa.app.utils;

import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;

/**
 * Query Utilities
 */
public class QueryUtils {
  /**
   * 
   * @param request HttpRequest
   * @return String Array with category, subCategory and mode in that order 
   */
  public static String[] getCategoryAndMode(ServerHttpRequest request) {
    String category;
    String mode;
    String subCategory;
    String path = request.getURI().getPath().toLowerCase();

    if (path.startsWith("/api/movies?") || path.startsWith("/api/movies/?")) {
      category = "Movies";
      mode = "Query";

      if (path.contains("year=")) {
        subCategory = "Year10";
      } else if (path.contains("rating=")) {
        subCategory = "Rating10";
      } else if (path.contains("genre=")) {
        subCategory = "Genre10";
      } else {
        subCategory = "Movies";
      }

      if (subCategory.endsWith("10") && path.contains("pagesize=100")) {
        subCategory += "0";
      }
    } else if (path.startsWith("/api/movies/")) {
      category = "Movies";
      subCategory = "Movies";
      mode = "Direct";

      if (request.getMethod() == HttpMethod.DELETE) {
        mode = "Delete";
      } else if (request.getMethod() == HttpMethod.POST
          || request.getMethod() == HttpMethod.PUT) {
        mode = "Upsert";
      }
    } else if (path.startsWith("/api/movies")) {
      category = "Movies";
      subCategory = "Movies";
      mode = "Query";
    } else if (path.startsWith("/api/actors?") 
          || path.startsWith("/api/actors/?") 
          || path.startsWith("/api/actors")) {
      category = "Actors";
      subCategory = "Actors";
      mode = "Query";
    } else if (path.startsWith("/api/actors/")) {
      category = "Actors";
      subCategory = "Actors";
      mode = "Direct";
    }  else if (path.startsWith("/api/genres")) {
      category = "Genres";
      subCategory = "Genres";
      mode = "Query";
    } else if (path.startsWith("/healthz")) {
      category = "Healthz";
      subCategory = "Healthz";
      mode = "Healthz";
    } else if (path.startsWith("/metrics")) {
      category = "Metrics";
      subCategory = "Metrics";
      mode = "Metrics";
    } else {
      category = "Static";
      subCategory = "Static";
      mode = "Static";
    }

    return new String[] { category, subCategory, mode };
  }
}
