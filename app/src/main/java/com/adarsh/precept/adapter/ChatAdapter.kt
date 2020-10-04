package com.adarsh.precept.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adarsh.precept.R
import com.adarsh.precept.models.DateHeader
import com.adarsh.precept.models.Message
import com.adarsh.precept.models.chatEvent
import com.adarsh.precept.utils.formatAsTime
import kotlinx.android.synthetic.main.list_item_chat_send_message.view.*
import kotlinx.android.synthetic.main.list_item_date_header.view.*

class ChatAdapter(
    private val list: MutableList<chatEvent>,
    private val currentUid: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflate =  {layout :Int ->
            LayoutInflater.from(parent.context).inflate(layout, parent, false)
        }

        return when (viewType) {
            TEXT_MESSAGE_RECEIVED -> {
                MessageViewHolder(inflate(R.layout.list_item_chat_received_mssg))
            }
            TEXT_MESSAGE_SENT -> {
                MessageViewHolder(inflate(R.layout.list_item_chat_send_message))
            }
            DATE_HEADER -> {
                DateViewHolder(inflate(R.layout.list_item_date_header))
            }
            else -> MessageViewHolder(inflate(R.layout.list_item_chat_received_mssg))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(val item = list[position]){
            is DateHeader -> {
                holder.itemView.dateTV.text = item.date //Already formatted in DateHeader data class.
            }
            is Message -> {
                holder.itemView.apply {
                    main_message.text = item.msg
                    msg_time.text = item.sentAt.formatAsTime()
                }
            }
        }
    }

    override fun getItemCount(): Int = list.size

    override fun getItemViewType(position: Int): Int {
        return when (val event = list[position]) {
            is Message -> {
                if (event.senderId == currentUid) {
                    TEXT_MESSAGE_SENT
                } else
                    TEXT_MESSAGE_RECEIVED
            }
            is DateHeader -> DATE_HEADER
            else -> UNSUPPORTED
        }
    }

    class DateViewHolder(view: View) : RecyclerView.ViewHolder(view)

    class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view)

    companion object {
        private const val UNSUPPORTED = -1
        private const val TEXT_MESSAGE_RECEIVED = 0
        private const val TEXT_MESSAGE_SENT = 1
        private const val DATE_HEADER = 2
    }
}