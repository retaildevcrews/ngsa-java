package com.cse.ngsa.app.models;

import com.azure.cosmos.models.PartitionKey;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import net.minidev.json.annotate.JsonIgnore;
import org.springframework.util.StringUtils;

/**
 * Movie.
 */
@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)

@JsonPropertyOrder({"id", "movieId", "partitionKey", "type", "title", "textSearch", "year",
    "runtime", "rating", "votes", "totalScore", "genres", "roles"})
public class Movie extends MovieBase {

  @SuppressFBWarnings("UUF_UNUSED_FIELD")
  @JsonIgnore
  private String id;

  @SuppressFBWarnings("UUF_UNUSED_FIELD")
  private String movieId;

  @SuppressFBWarnings("UUF_UNUSED_FIELD")
  @com.azure.spring.data.cosmos.core.mapping.PartitionKey
  private String partitionKey;

  @SuppressFBWarnings("UUF_UNUSED_FIELD")
  private double rating;

  @SuppressFBWarnings("UUF_UNUSED_FIELD")
  private long votes;

  @SuppressFBWarnings("UUF_UNUSED_FIELD")
  private long totalScore;

  @SuppressFBWarnings("UUF_UNUSED_FIELD")
  private String textSearch;

  @SuppressFBWarnings("UUF_UNUSED_FIELD")
  private String type;

  @SuppressFBWarnings("UUF_UNUSED_FIELD")
  private List<Role> roles;


  public Movie() {
    // default constructor
  }

  /**
   * ComputePartitionKey.
   */
  public static PartitionKey computePartitionKey(String id) {
    // validate id
    if (StringUtils.hasLength(id) && id.length() > 5
        && (StringUtils.startsWithIgnoreCase(id, "tt")
            || StringUtils.startsWithIgnoreCase(id, "zz"))) {
      int idInt = Integer.parseInt(id.substring(2));
      return new PartitionKey(String.valueOf(idInt % 10));
    }
    throw new IllegalArgumentException("Invalid Partition Key");
  }
}
