package com.adarsh.precept

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        editTextPhone.addTextChangedListener {
            send_otp.isEnabled = !(it.isNullOrEmpty() || it.length<10 )
            send_otp.isClickable = !(it.isNullOrEmpty() || it.length < 10 )
            
            if(send_otp.isEnabled){
                error_num.text = ""
                send_otp.backgroundTintList = ContextCompat.getColorStateList(this, R.color.white)
                send_otp.isClickable = true
            }else{
                error_num.text = "Phone number of 10 digits."
                send_otp.backgroundTintList = ContextCompat.getColorStateList(this, R.color.Fadedwhite)
                send_otp.isClickable = false
            }
        }

        send_otp.setOnClickListener {
            val countryCode = countryCodePicker.selectedCountryCodeWithPlus
            val phoneNumber = countryCode + editTextPhone.text.toString()

            MaterialAlertDialogBuilder(this).apply {
                setMessage("We will send an OTP to $phoneNumber.\n Is It okay, or would you like to edit the number?")
                setPositiveButton("Okay"){dialog, _ ->
                    showNewActivity(phoneNumber)
                    dialog.dismiss()
                }
                setNegativeButton("Edit"){dialog,_ ->
                    dialog.dismiss()
                }
                setCancelable(false)
                create()
                show()
            }
        }
    }

    private fun showNewActivity(phoneNumber:String) {
        val it = Intent(this, OtpActivity::class.java)
        it.putExtra("phoneNumber", phoneNumber)
        startActivity(it)
    }
}