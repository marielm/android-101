package com.marielm.android101

import com.google.gson.annotations.SerializedName

data class Repository(var name: String? = null,
                      var description: String? = null,
                      @SerializedName("updated_at")
                      var updatedOn: String? = null,
                      var id: Long = 0,
                      @SerializedName("watchers_count")
                      var watchers: Int = 0
)
