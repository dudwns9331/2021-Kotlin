package com.example.summer_part4_chapter05.data.response

import com.example.summer_part4_chapter05.data.entity.GithubRepoEntity

data class GithubRepoSearchResponse(
    val totalCount: Int,
    val items: List<GithubRepoEntity>
)
