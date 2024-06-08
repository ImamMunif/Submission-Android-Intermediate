package com.dicoding.storysubmission

import com.dicoding.storysubmission.data.response.ListStoryItem

object DataDummy {

    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                id = "user-ReWg75PJS9H9hFBf",
                name = "Imam Munif",
                description = "ini deskripsi ke-${i + 1} testing",
                photoUrl = "https://story-api.dicoding.dev/images/stories/photos-1717831893250_d30e14bfdadcc0a2c43b.jpg",
                createdAt = "2024-06-08T07:31:33.252Z",
                lat = -7.795580,
                lon = 110.369492
            )
            items.add(story)
        }
        return items
    }
}