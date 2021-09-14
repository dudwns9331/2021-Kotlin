package com.example.summer_part4_chapter02

import com.example.summer_part4_chapter02.service.MusicEntity

fun MusicEntity.mapper(id: Long): MusicModel =
    MusicModel(
        id = id,
        streamUrl = this.streamUrl,
        coverUrl = this.coverUrl,
        track = this.track,
        artist = this.artist
    )