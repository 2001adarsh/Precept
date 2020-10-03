package com.adarsh.precept.adapter

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.adarsh.precept.R
import com.adarsh.precept.models.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.chat_item.view.*

class UsersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(user: User,  onClick:(name:String, photo:String, id:String)->Unit) = with(itemView){
        countTv.isVisible = false
        timeTv.isVisible = false

        titleTv.text = user.name
        subtitleTv.text = user.status
        Picasso.get().load(user.imageUrl).placeholder(R.drawable.defaultimage).error(R.drawable.defaultimage)
            .into(chat_profile_pic)

        setOnClickListener {
            onClick(user.name, user.thumbImage, user.uid)
        }
    }
}