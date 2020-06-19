package com.marielm.android101

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface GithubService {
    @GET("/users/{user}/repos")
    fun getRepositories(@Path("user") username: String): Call<List<Repository>>
}
