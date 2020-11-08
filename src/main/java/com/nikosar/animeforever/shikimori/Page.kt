package com.nikosar.animeforever.shikimori

import org.apache.http.NameValuePair
import org.apache.http.message.BasicNameValuePair

class Page(page: Int = 1, size: Int = 10) {
    val page: Int
    val size: Int

    init {
        if (size > 0 || size <= 40 || page > 0) {
            this.page = page
            this.size = size
        } else {
            this.page = 1
            this.size = 1
        }
    }

    fun toNameValuePairs(): List<NameValuePair> {
        return listOf(
                BasicNameValuePair("page", page.toString()),
                BasicNameValuePair("limit", size.toString())
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Page

        if (page != other.page) return false
        if (size != other.size) return false

        return true
    }

    override fun hashCode(): Int {
        var result = page
        result = 31 * result + size
        return result
    }

}

