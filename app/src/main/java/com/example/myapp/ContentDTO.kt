package com.example.myapp

import java.util.*


data class ContentDTO(
    var uid: String? = "",
    var userId: String? = "",
    var title: String = "",
    var story:String? = "",
    var tag1:String? ="",
    var tag2:String? = "",
    var tag3:String? = "",
    var imageUrl:String? = "",
    var date: Date =Date(),
    var favoriteCount: Int = 0,
    var favorites: MutableMap<String, Boolean> = HashMap()){

    data class Comment(var uid: String? = "",
                       var userId: String? = "",
                       var comment: String? = "",
                       var date: Date = Date())
}

