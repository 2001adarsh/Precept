package com.adarsh.precept.models

import android.content.Context
import com.adarsh.precept.utils.formatAsHeader
import java.util.*

interface chatEvent{
    val sentAt:Date
}

data class Message(
    val msg:String,
    val senderId:String,
    val msgID:String,
    val type:String = "TEXT",
    val status:Int = 1,
    val liked:Boolean = false,
    override val sentAt: Date = Date()
):chatEvent{
    constructor():this("", "", "", "", 1,false, Date())
}

data class DateHeader(
    override val sentAt: Date = Date(), val context: Context
):chatEvent{
    val date:String = sentAt.formatAsHeader(context)
}