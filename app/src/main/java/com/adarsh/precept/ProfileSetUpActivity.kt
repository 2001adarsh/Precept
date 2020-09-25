package com.adarsh.precept

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_profile_set_up.*

class ProfileSetUpActivity : AppCompatActivity() {

    val storage by lazy { FirebaseStorage.getInstance() }
    val auth by lazy { FirebaseAuth.getInstance() }
    val database by lazy { FirebaseFirestore.getInstance() }
    lateinit var downloadLink: String
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_set_up)

        failure_txt.visibility = View.INVISIBLE
        changeImage.setOnClickListener {
            //First check for the Access of Internal Storage.
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                openGalleryToChoosePicture()
            } else {
                requestPermissions(
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        12345
                    )
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                    openGalleryToChoosePicture()
                else
                    Toast.makeText(this, "Do not have permission to select photos!", Toast.LENGTH_SHORT).show()
            }
        }

        next_button.setOnClickListener {
            val name: String = person_name.text.toString()
            if(name.length < 3){
                person_name.error = "Atleast 3 letter should be there."
            }else if(!::downloadLink.isInitialized){ // if picture not uploaded.
                failure_txt.visibility = View.VISIBLE
            }else{
                next_button.isEnabled = false
                progressDialog = createProgressDialog("Creating your account.", false)
                progressDialog.show()
                val user = User(name, downloadLink, downloadLink, auth.uid!!)
                database.collection("users").document(auth.uid!!).set(user)
                    .addOnSuccessListener {
                        //Next Page
                        startActivity(Intent(this, MainActivity::class.java)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
                    }.addOnFailureListener{
                        //Failed to Upload.
                        progressDialog.cancel()
                        next_button.isEnabled = true
                        failure_txt.visibility = View.VISIBLE
                    }
            }
        }

    }

    private fun openGalleryToChoosePicture(){
        val itent = Intent(Intent.ACTION_PICK)
        itent.type= "image/*"                               //will Open Image type only
        startActivityForResult(itent, 189)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //After successfully picking the photo from gallery.
        if(resultCode == Activity.RESULT_OK && requestCode == 189){
            data?.data?.let {
                profile_image.setImageURI(it)
                uploadImage(it)
            }
        }
    }

    private fun uploadImage(it: Uri) {
        next_button.isEnabled = false
        uploading.visibility = View.VISIBLE
        val ref = storage.reference.child("uploads/" + auth.uid.toString())
        val uploadTask = ref.putFile(it)

        uploadTask.continueWithTask( Continuation<UploadTask.TaskSnapshot, Task<Uri>> {task->
            if(!task.isSuccessful){
                //throw an exception
                task.exception?.let {
                    throw it
                }
            }
            return@Continuation ref.downloadUrl
        }).addOnCompleteListener { task->
            if(task.isSuccessful){
                next_button.isEnabled = true
                uploading.visibility = View.INVISIBLE
                downloadLink = task.result.toString()
                Log.d("Profile Image", "uploadImage: $downloadLink" )
            }
        }.addOnFailureListener{
            failure_txt.isVisible = true
        }

    }
}