package com.adarsh.precept.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adarsh.precept.*
import com.adarsh.precept.R
import com.adarsh.precept.adapter.ChatViewHolder
import com.adarsh.precept.models.Inbox
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import kotlinx.android.synthetic.main.fragment_in_box.*

class InBoxFragment : Fragment() {

    private lateinit var adapter: FirebaseRecyclerAdapter<Inbox, ChatViewHolder>
    private lateinit var viewManager:RecyclerView.LayoutManager
    val db by lazy{ FirebaseDatabase.getInstance() }
    val auth by lazy { FirebaseAuth.getInstance() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewManager = LinearLayoutManager(requireContext())
        listener()
        return inflater.inflate(R.layout.fragment_in_box, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        inbox_recycler_view.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = adapter
        }
    }

    fun listener(){
        val baseQuery: Query = db.reference.child("Chats").child(auth.uid!!)
        var obj:Inbox?
        baseQuery.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                obj = snapshot.getValue<Inbox>()
                Log.e("TAG", "onChildAdded: ${obj?.from}")
                setUpAdapter(obj!!.from)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun setUpAdapter(st: String){
        Log.e("TAG", "setUpAdapstter: $st")
        val case = db.reference.child("Chats${auth.uid!!}")///$st")

        val options = FirebaseRecyclerOptions.Builder<Inbox>()
            .setLifecycleOwner(viewLifecycleOwner)
            .setQuery(case, Inbox::class.java)
            .build()

        Log.e("TAG", "setUpAdapter: ${options.snapshots}")

        //Now create the adapter
        adapter = object :FirebaseRecyclerAdapter<Inbox, ChatViewHolder>(options){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
                Log.e("TAG", "onCreateViewHolder: ")
                return ChatViewHolder(layoutInflater.inflate(R.layout.chat_item, parent, false))
            }

            override fun onDataChanged() {
                super.onDataChanged()
                Log.e("TAG", "onDataChanged: ", )
            }

            override fun onError(error: DatabaseError) {
                super.onError(error)
                Log.e("TAG", "onError: ", )
            }

            override fun onBindViewHolder(holder: ChatViewHolder, position: Int, model: Inbox) {
                Log.e("TAG", "onBindViewHolder: ")
                holder.bind(model){ name: String, photo: String, id: String ->
                    val intent = Intent(requireContext(), ChatActivity::class.java)
                    intent.putExtra(UID, id)
                    intent.putExtra(NAME, name)
                    intent.putExtra(IMAGE, photo)
                    startActivity(intent)
                }
            }
        }
        adapter.startListening()
    }
     override fun onStart() {
        super.onStart()

    }

     override fun onStop() {
        super.onStop()
         adapter.stopListening()
    }
}