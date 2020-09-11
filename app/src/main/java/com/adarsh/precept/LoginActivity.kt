package com.adarsh.precept

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        editTextPhone.addTextChangedListener {
            send_otp.isEnabled = !(it.isNullOrEmpty() || it.length<10 || it.length>10)
            if(send_otp.isEnabled){
                send_otp.backgroundTintList = ContextCompat.getColorStateList(this, R.color.white)
                send_otp.isClickable = true
            }else{
                send_otp.backgroundTintList = ContextCompat.getColorStateList(this, R.color.Fadedwhite)
                send_otp.isClickable = false
            }
        }

    }
}