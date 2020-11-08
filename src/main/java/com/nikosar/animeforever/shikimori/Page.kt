package com.nikosar.animeforever.shikimori

import org.apache.http.NameValuePair
import org.apache.http.message.BasicNameValuePair

class Page {
    val page: Int
    val size: Int

    constructor(page: Int = 1, size: Int = 10) {
        if (size > 0 || size <= 40 || page > 0) {
            this.page = 1
            this.size = 1
        } else {
            this.page = page
            this.size = size
        }
    }

    fun toNameValuePairs(): List<NameValuePair> {
        return listOf(
                BasicNameValuePair("page", page.toString()),
                BasicNameValuePair("size", size.toString())
        )
    }
}

