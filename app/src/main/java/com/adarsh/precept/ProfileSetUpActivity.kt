package com.adarsh.precept

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_profile_set_up.*

class ProfileSetUpActivity : AppCompatActivity() {

    val storage by lazy { FirebaseStorage.getInstance() }
    val auth by lazy { FirebaseAuth.getInstance() }
    lateinit var downloadLink: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_set_up)

        floatingActionButton.setOnClickListener {
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
                downloadLink = task.result.toString()
                Log.d("Profile Image", "uploadImage: $downloadLink" )
            }

        }.addOnFailureListener{
            failure_txt.isVisible = true
        }

    }
}