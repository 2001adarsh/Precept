package com.adarsh.precept.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.adarsh.precept.*
import com.adarsh.precept.models.Inbox
import com.adarsh.precept.utils.formatAsListItem
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.chat_item.view.*

class InboxAdapter(
    options: FirebaseRecyclerOptions<Inbox>,
    val listener: OnItemClickListener
) :
    FirebaseRecyclerAdapter<Inbox, InboxAdapter.ChatViewHolder>(options) {

    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: Inbox) =
            with(itemView) {
                countTv.isVisible = item.count > 0
                countTv.text = item.count.toString()
                timeTv.text = item.time.formatAsListItem(context)

                titleTv.text = item.name
                subtitleTv.text = item.msg
                Picasso.get().load(item.image).placeholder(R.drawable.defaultimage)
                    .error(R.drawable.defaultimage)
                    .into(chat_profile_pic)

                setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION)
                        listener.onItemClick(item.name, item.image, item.from)
                }
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_item, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int, model: Inbox) {
        holder.bind(model)
    }

    interface OnItemClickListener {
        fun onItemClick(name:String, image:String, uid:String)
    }
}