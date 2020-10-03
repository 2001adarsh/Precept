package com.adarsh.precept.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adarsh.precept.*
import com.adarsh.precept.adapter.EmptyViewHolder
import com.adarsh.precept.adapter.UsersViewHolder
import com.adarsh.precept.models.User
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.firebase.ui.firestore.paging.LoadingState
import com.firebase.ui.firestore.paging.LoadingState.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_people.*
import java.lang.Exception

private const val DELETED_VIEW_TYPE = 1 //To select viewholder that user can't see( his own name in chats)
private const val NORMAL_VIEW_TYPE = 2 //TO select ViewHolder that user can see (basically implemented for other contacts to see in Peoples)

class PeopleFragment : Fragment() {

    lateinit var mAdapter:FirestorePagingAdapter<User, RecyclerView.ViewHolder> //Using two viewHolders in a single Adapter.
    val auth by lazy { FirebaseAuth.getInstance() }
    val database by lazy {
        FirebaseFirestore.getInstance().collection("users")
            .orderBy("name", Query.Direction.DESCENDING)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setUpAdapter()
        return inflater.inflate(R.layout.fragment_people, container, false)
    }

    private fun setUpAdapter() {
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPrefetchDistance(2) //Number of pages you want to get initially.
            .setPageSize(20)
            .build()

        val options = FirestorePagingOptions.Builder<User>()
            .setLifecycleOwner(viewLifecycleOwner)
            .setQuery(database, config, User::class.java)
            .build()

        //Creating Adapter
        mAdapter = object : FirestorePagingAdapter<User, RecyclerView.ViewHolder>(options) {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): RecyclerView.ViewHolder {
                return when (viewType) {
                    NORMAL_VIEW_TYPE -> UsersViewHolder(
                        layoutInflater.inflate(
                            R.layout.chat_item,
                            parent,
                            false
                        )
                    )
                    else -> EmptyViewHolder(
                        layoutInflater.inflate(
                            R.layout.empty_view,
                            parent,
                            false
                        )
                    )
                }
            }

            override fun onBindViewHolder(
                holder: RecyclerView.ViewHolder,
                position: Int,
                model: User
            ) {
                if (holder is UsersViewHolder) {
                    holder.bind(user = model){ name: String, photo: String, id: String ->
                        val intent = Intent(requireContext(), ChatActivity::class.java)
                        intent.putExtra(UID, id)
                        intent.putExtra(NAME, name)
                        intent.putExtra(IMAGE, photo)
                        startActivity(intent)
                    }
                } else {
                    //Do nothing, this one is your own chat
                }
            }

            override fun onLoadingStateChanged(state: LoadingState) {
                super.onLoadingStateChanged(state)
                when (state) {        //These methods can be used to show different views and loadings.
                    LOADING_INITIAL -> {
                    }//    The initial load has begun
                    LOADING_MORE -> {
                    }// The adapter has started to load an additional page
                    LOADED -> {
                    }// The previous load (either initial or additional) completed
                    FINISHED -> {
                    }
                    ERROR -> {
                    }// The previous load (either initial or additional) failed. Call
                    // the retry() method in order to retry the load operation.
                }
            }

            override fun onError(e: Exception) {
                super.onError(e)
            }

            override fun getItemViewType(position: Int): Int {
                val item = getItem(position)?.toObject(User::class.java)
                return when (auth.uid == item!!.uid) {
                    true -> DELETED_VIEW_TYPE
                    else -> NORMAL_VIEW_TYPE
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        peoples_recycler_view.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
        }
    }



}