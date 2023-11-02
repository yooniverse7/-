package com.example.myapp

import java.util.Date

data class UserInfo (
    var uid :String? = null,
    var name : String? = null,
    var email : String? = null,

    var followerCount : Int = 0,
    var followers : MutableMap<String,Boolean> = HashMap(),

    var followingCount : Int = 0,
    var followings : MutableMap<String,Boolean> = HashMap()

){
    data class Alarms(
        var memberId: String? = null,
        var message: String? = null,
        var date: Date = Date(),
        var contentId: String? = null)


}
