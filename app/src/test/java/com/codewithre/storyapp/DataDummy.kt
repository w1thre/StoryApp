package com.codewithre.storyapp

import com.codewithre.storyapp.data.remote.response.ListStoryItem

object DataDummy {
    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                i.toString(),
                "name $i",
                "photoUrl $i",
                "description $i",
            )
            items.add(story)
        }
        return items
    }
}