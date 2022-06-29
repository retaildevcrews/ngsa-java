package com.cse.ngsa.app;

/**
 * Constants.
 */
public final class Constants {

  public static final String COSMOS_URL_KEYNAME = "CosmosUrl";
  public static final String COSMOS_KEY_KEYNAME = "CosmosKey";
  public static final String COSMOS_DATABASE_KEYNAME = "CosmosDatabase";
  public static final String COSMOS_COLLECTION_KEYNAME = "CosmosCollection";
  public static final String GENRE_DOCUMENT_TYPE = "Genre";
  public static final String MOVIE_DOCUMENT_TYPE = "Movie";
  public static final String ACTOR_DOCUMENT_TYPE = "Actor";

  public static final Integer DEFAULT_PAGE_SIZE = 100;
  public static final Integer MAX_DEGREE_PARALLELISM = 2;
  public static final Integer MAX_BENCH_STR_SIZE = 1024 * 1024;

  public static final String WEB_INSTANCE_ROLE = "WEBSITE_ROLE_INSTANCE_ID";
  public static final String WEB_INSTANCE_ROLE_ID = "WEBSITE_ROLE_INSTANCE_ID";

  public static final String ACTOR_CONTROLLER_EXCEPTION = "ActorControllerException";
  public static final String MOVIE_CONTROLLER_EXCEPTION = "MovieControllerException";

  public static final String SECRETS_VOLUME_ARGUMENT = "secrets-volume";
  public static final String BURST_HEADER_ARGUMENT = "burst-header";
  public static final String BURST_HEADER_KEY = "X-Load-Feedback";

  private Constants() {
    // private constructor to hide public constructor
  }
}
