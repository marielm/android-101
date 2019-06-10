package com.marielm.android101;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface GithubService {
  @GET("/users/{user}/repos")
  Call<List<Repository>> getRepositories(@Path("user") String username);
}
