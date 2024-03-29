package com.cse.ngsa.app.models;

import com.azure.cosmos.models.PartitionKey;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.minidev.json.annotate.JsonIgnore;
import org.springframework.util.StringUtils;

/**
 * Actor.
 */
@Getter
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonPropertyOrder({"id",
    "actorId",
    "partitionKey",
    "name",
    "birthYear",
    "profession",
    "type",
    "textSearch",
    "movies"})

public class Actor extends ActorBase {
  @JsonIgnore
  private String id;

  private String partitionKey;

  private List<String> profession;
  private String type;
  private String textSearch;
  private List<MovieBase> movies;

  public Actor() {
    // default constructor
  }

  @Override
  public String toString() {
    return "Actor{"
        + "id='" + id + '\''
        + ", partitionKey='" + partitionKey + '\''
        + ", profession=" + profession
        + ", type='" + type + '\''
        + ", textSearch='" + textSearch + '\''
        + ", movies=" + movies
        + ", deathYear=" + deathYear
        + "}";
  }

  /**
   * ComputePartitionKey.
   */
  public static PartitionKey computePartitionKey(String id) {
    // validate id
    if (StringUtils.hasLength(id) && id.length() > 5
        && StringUtils.startsWithIgnoreCase(id, "nm")) {
      int idInt = Integer.parseInt(id.substring(2));
      return new PartitionKey(String.valueOf(idInt % 10));
    }
    throw new IllegalArgumentException("Invalid Partition Key");
  }
}
