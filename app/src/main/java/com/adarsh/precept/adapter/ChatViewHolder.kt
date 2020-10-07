package com.adarsh.precept.adapter

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.adarsh.precept.R
import com.adarsh.precept.models.Inbox
import com.adarsh.precept.utils.formatAsListItem
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.chat_item.view.*

class ChatViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
    fun bind(item: Inbox, onClick: (name:String, photo:String, id: String)->Unit) =
        with(itemView){
            countTv.isVisible = item.count > 0
            countTv.text = item.count.toString()
            timeTv.text =item.time.formatAsListItem(context)

            titleTv.text = item.name
            subtitleTv.text = item.msg
            Picasso.get().load(item.image).placeholder(R.drawable.defaultimage).error(R.drawable.defaultimage)
                .into(chat_profile_pic)

            setOnClickListener {
                onClick.invoke(item.name, item.image, item.from)
            }
        }
}