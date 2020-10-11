package com.adarsh.precept

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import com.adarsh.precept.models.User
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_user_profile.*
import kotlinx.android.synthetic.main.user_profile.*

class UserProfileActivity : AppCompatActivity() {
    private val friendUid: String by lazy {
        intent.getStringExtra(UID)
    }
    private val friendName: String by lazy {
        intent.getStringExtra(NAME)
    }
    private val friendImage: String by lazy {
        intent.getStringExtra(IMAGE)
    }

    private val store: FirebaseFirestore by lazy{
        FirebaseFirestore.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        store.collection("users").document(friendUid)
            .get().addOnSuccessListener { documentSnapshot ->
                Log.e("TAG", "onCreate: ${documentSnapshot.data}", )
                val user = documentSnapshot.toObject(User::class.java)
                user_status.text = user?.status
            }.addOnFailureListener {
                Log.e("TAG", "Failure ", )
            }

        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        user_name.text = friendName
        Picasso.get().load(friendImage).placeholder(R.drawable.defaultimage).error(R.drawable.defaultimage).into(user_image)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}