package com.example.forgithubclient.data

import com.google.gson.annotations.SerializedName

data class User(
    val login: String,
    val id: Int,
    @SerializedName("avatar_url") val avatarUrl: String,
    val url: String,
    // Enriched fields (not present in the list API response, but fetched later)
    val name: String? = null,
    val company: String? = null,
    @SerializedName("public_repos") val publicRepos: Int? = null
)

data class UserDetail(
    val login: String,
    val id: Int,
    @SerializedName("avatar_url") val avatarUrl: String,
    val name: String?,
    val company: String?,
    val blog: String?,
    val location: String?,
    val email: String?,
    val bio: String?,
    @SerializedName("public_repos") val publicRepos: Int,
    val followers: Int,
    val following: Int
)
