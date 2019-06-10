package com.esbeecorp.oneltmessenger

import java.util.*
import java.util.concurrent.TimeUnit

data class User(
    val uid: String = "",
    val username: String = "",
    val profileImageUrl: String = ""
)

data class Chat(
    val id: String = "",
    val lastMessage: String = "",
    val users: MutableList<String> = mutableListOf()
)

data class Invitation(
    var id: String = "",
    val receiver: User = User(),
    val sender: User = User(),
    val dateTime: Date = Date()
){

    fun displayAge(): String{

        val diff = Date().time - this.dateTime.time


        var age = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)

        if (age > 1)
            return "$age days ago"
        else if (age > 0)
            return "$age day ago"

        age = TimeUnit.HOURS.convert(diff, TimeUnit.MILLISECONDS)

        if (age > 1)
            return "$age hours ago"
        else if (age > 0)
            return "$age hour ago"

        age = TimeUnit.MINUTES.convert(diff, TimeUnit.MILLISECONDS)

        if (age > 1)
            return "$age mins ago"
        else if (age > 0)
            return "$age min ago"
        else
            return "just now"
    }
}