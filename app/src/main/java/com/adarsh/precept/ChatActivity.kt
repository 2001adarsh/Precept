package com.adarsh.precept

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.adarsh.precept.adapter.ChatAdapter
import com.adarsh.precept.models.*
import com.adarsh.precept.utils.isSameDayAs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.EmojiPopup
import com.vanniktech.emoji.google.GoogleEmojiProvider
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.list_item_chat_send_message.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

const val UID = "uid"
const val NAME = "name"
const val IMAGE = "photo"

class ChatActivity : AppCompatActivity() {
    private val friendUid: String by lazy {
        intent.getStringExtra(UID)
    }
    private val friendName: String by lazy {
        intent.getStringExtra(NAME)
    }
    private val friendImage: String by lazy {
        intent.getStringExtra(IMAGE)
    }
    private val currentUID: String by lazy {
        FirebaseAuth.getInstance().uid!!
    }
    private val db: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance()
    }
    lateinit var currentUser: User
    private val messages = mutableListOf<chatEvent>()
    lateinit var chatAdapter: ChatAdapter

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
        Picasso.get().load(friendImage).placeholder(R.drawable.defaultimage)
            .error(R.drawable.defaultimage).into(friend_image)

        //Emoji icon Setup
        val emojiPopup = EmojiPopup.Builder.fromRootView(rootView).build(mssg_init)
        emoji_icon.setOnClickListener {
            emojiPopup.toggle()
        }

        //Refresh
        swipeRefreshLayout.setOnRefreshListener {
            val workScope = CoroutineScope(Dispatchers.Main)
            workScope.launch {
                delay(2000)
                swipeRefreshLayout.isRefreshing = false
            }
        }

        send_icon.setOnClickListener {
            mssg_init.text?.let {  //Message written by current user.
                if (it.isNotEmpty()) {
                    sendMessage(it.toString())
                    it.clear()
                }
            }
        }
        chatAdapter = ChatAdapter(list = messages, currentUID)

        msgRV.apply {
            layoutManager = LinearLayoutManager(this@ChatActivity)
            adapter = chatAdapter
        }


        listenToMessages()
        markAsRead()        //If the ChatActivity is opened. then the message is read.
        chatAdapter.lovedClick = {id, status->     //Loved action
            updateLovedStatus(id, status)
        }
    }

    private fun sendMessage(msg: String) {
        val id = getMessages(friendUid).push().key //Push will generate auto new key for messages.
        checkNotNull(id) { "Cannot be null" }
        val msgMap = Message(msg, currentUID, id)
        getMessages(friendUid).child(id).setValue(msgMap).addOnSuccessListener {
            Log.i("TAG", "sendMessage: Successful")
        }.addOnFailureListener {
            Log.i("TAG", "sendMessage: Failure")
        }
        updateLastMessage(msgMap)
    }

    private fun updateLastMessage(message: Message) {
        val inboxMap = Inbox(
            message.msg,
            friendUid,
            friendName,
            friendImage,
            count = 0
        )

        getInbox(currentUID, friendUid).setValue(inboxMap).addOnSuccessListener {
            //now after adding the details  current user's message to friendUser's. also do the changes in FriendUser's inbox
            getInbox(friendUid, currentUID).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val value = snapshot.getValue(Inbox::class.java)
                    inboxMap.apply {
                        from = message.senderId
                        name = currentUser.name
                        image = currentUser.thumbImage
                        count = 1
                    }
                    value?.let {
                        if(it.from == message.senderId){
                            inboxMap.count = value.count+1
                        }
                    }
                    getInbox(friendUid, currentUID).setValue(inboxMap)
                }
                override fun onCancelled(error: DatabaseError) {}
            })
        }
    }

    //After seeing the unread mssg.
    private fun markAsRead(){
        getInbox(friendUid, currentUID).child("count").setValue(0)
    }
    private fun updateLovedStatus(id:String, status:Boolean){
        getMessages(friendUid).child(id).updateChildren(mapOf("liked" to status))
    }

    private fun listenToMessages(){
        getMessages(friendUid)
            .orderByKey()
            .addChildEventListener(object : ChildEventListener{
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val message = snapshot.getValue(Message::class.java)!!
                    addMessage(message)
                }
                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

                override fun onChildRemoved(snapshot: DataSnapshot) {}

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun addMessage(msg: Message) {
        val eventBefore = messages.lastOrNull()
        if((eventBefore != null && !eventBefore.sentAt.isSameDayAs(msg.sentAt)) || eventBefore == null){
            messages.add(
                DateHeader(msg.sentAt, context = this)
            )
        }
        messages.add(msg)
        chatAdapter.notifyItemInserted(messages.size-1)
        msgRV.scrollToPosition(messages.size-1)
    }

    private fun getMessages(friendId: String) = db.reference.child("messages/${getId(friendId)}")

    private fun getInbox(toUser: String, fromUser: String) = db.reference.child("Chats/$toUser/$fromUser")

    //Id for the messages.
    private fun getId(friendId: String): String {
        return if (friendId > currentUID)
            currentUID + friendId
        else
            friendId + currentUID
    }


}