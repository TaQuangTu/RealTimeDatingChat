package com.taquangtu.forus.models

import com.google.firebase.database.Exclude

class Message {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "content" to content,
            "time" to time,
            "userId" to userId,
            "room" to room,
            "reaction" to reaction,
            "id" to id,
            "reply" to reply
        )
    }

    companion object {
        const val IMAGE_TAG = "***Image***"
        const val REACTION_NONE = 0
        const val REACTION_HEART = 1
        const val REACTION_HAHA = 2
        const val REACTION_WOW = 3
        const val REACTION_SAD = 4
        const val REACTION_ANGRY = 5
        const val REACTION_LIKE = 6
        const val REACTION_DIS_LIKE = 7
    }

    var content: String
    var time: Long
    var userId: String
    var room: Int
    var reaction: Int
    var id: String = ""
    var reply: String = ""

    constructor() {
        content = "null"
        time = 0
        userId = "unknown"
        room = 0
        reaction = 0
        id = ""
    }

    constructor(c: String, t: Long, u: String, r: Int, re: Int) {
        content = c
        time = t
        userId = u
        room = r
        reaction = re
    }
}