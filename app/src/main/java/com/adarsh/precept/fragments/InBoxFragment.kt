package com.adarsh.precept.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.adarsh.precept.*
import com.adarsh.precept.R
import com.adarsh.precept.adapter.InboxAdapter
import com.adarsh.precept.models.Inbox
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_in_box.*

class InBoxFragment : Fragment(), InboxAdapter.OnItemClickListener {

    private lateinit var adapter: InboxAdapter
    val db by lazy { FirebaseDatabase.getInstance() }
    val auth by lazy { FirebaseAuth.getInstance() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_in_box, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val baseQuery = FirebaseDatabase.getInstance().reference.child("Chats").child(auth.uid!!)
        val options = FirebaseRecyclerOptions.Builder<Inbox>()
            .setQuery(baseQuery, Inbox::class.java)
            .build()

        adapter = InboxAdapter(options, this)
        inbox_recycler_view.setHasFixedSize(true)
        inbox_recycler_view.layoutManager = LinearLayoutManager(requireContext())
        inbox_recycler_view.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    override fun onItemClick(name: String, image: String, uid: String) {
        val intent = Intent(requireContext(), ChatActivity::class.java)
        intent.putExtra(UID, uid)
        intent.putExtra(NAME, name)
        intent.putExtra(IMAGE, image)
        startActivity(intent)
    }
}