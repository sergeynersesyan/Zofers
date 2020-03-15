package com.zofers.zofers.model

import com.google.gson.annotations.SerializedName

import java.util.Date



class Message {

    lateinit var id: String

    lateinit var conversationId: String
    lateinit var userId: String
    var date: Date? = null
    var text: String = ""
    var type: Int = 0 //default 0 or service 1;
    var status: Int = 0

//    var isInternal: Boolean = false

//    val isUnsent: Boolean
//        get() = isInternal && System.currentTimeMillis() - date!!.time > 1000 * 5

    companion object {
        val DOC_NAME = "message"

        val TYPE_DEFAULT = 0
        val TYPE_SERVICE = 1
    }

}
