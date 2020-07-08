package com.zofers.zofers.model

import java.util.Date



class Message {

    lateinit var id: String

    lateinit var conversationId: String
    lateinit var userId: String
    var date: Date? = null
    var text: String = ""
    var type: Int = 0 //default 0 or service 1;
    var offerID: String? = null

    companion object {
        const val DOC_NAME = "message"

        const val TYPE_DEFAULT = 0
        const val TYPE_SERVICE = 1
    }

}
