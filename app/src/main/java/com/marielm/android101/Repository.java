package com.marielm.android101;

import com.google.gson.annotations.SerializedName;

public class Repository {
  @SerializedName("name")
  public String name;

  @SerializedName("description")
  public String description;

  @SerializedName("updated_at")
  public String updatedOn;

  @SerializedName("id")
  public long id;

  @SerializedName("watchers_count")
  public int watchers;
}
