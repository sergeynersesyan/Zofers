package com.zofers.zofers.model

import java.util.Date

class Message {

    var id: String? = null

    var conversationId: String? = null
    var userId: String? = null
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
