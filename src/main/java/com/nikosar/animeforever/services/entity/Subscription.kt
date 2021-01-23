package com.nikosar.animeforever.services.entity

import org.springframework.data.annotation.Id

data class Subscription(
    @Id
    var id: Long?,
    var userId: Long,
    var animeId: Long,
    var channelId: Long
)
