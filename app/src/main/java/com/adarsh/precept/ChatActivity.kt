package com.adarsh.precept

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.adarsh.precept.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.google.GoogleEmojiProvider
import kotlinx.android.synthetic.main.activity_chat.*

const val UID = "uid"
const val NAME = "name"
const val IMAGE = "photo"

class ChatActivity : AppCompatActivity() {
    private val friendUid:String by lazy{
        intent.getStringExtra(UID)
    }
    private val friendName:String by lazy {
        intent.getStringExtra(NAME)
    }
    private val friendImage:String by lazy{
        intent.getStringExtra(IMAGE)
    }
    private val currentUID:String by lazy {
        FirebaseAuth.getInstance().uid!!
    }
    private val db:FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance()
    }
    lateinit var currentUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EmojiManager.install(GoogleEmojiProvider()) //Install the emoji's before inflating the xml file.
        setContentView(R.layout.activity_chat)

        //current user object
        FirebaseFirestore.getInstance().collection("users").document(currentUID).get()
            .addOnSuccessListener {
                currentUser = it.toObject(User::class.java)!!
            }

        //Set up Friends image and Name in Toolbar
        friend_name.text = friendName
        Picasso.get().load(friendImage).placeholder(R.drawable.defaultimage).error(R.drawable.defaultimage).into(friend_image)


    }
}