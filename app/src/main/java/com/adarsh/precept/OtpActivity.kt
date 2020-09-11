package com.adarsh.precept

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import kotlinx.android.synthetic.main.activity_otp.*

class OtpActivity : AppCompatActivity() {
    lateinit var phoneNumber:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)
        phoneNumber = intent.getStringExtra("phoneNumber")
        setStrings()
        showTimer(60000)
        verify()
    }

    private fun verify() {
        verify_btn.isClickable = false
        verify_btn.isEnabled = false

        otp_num.addTextChangedListener {
            verify_btn.apply {
                isEnabled = !(it.isNullOrEmpty() || it.length<6)
                isClickable = !(it.isNullOrEmpty() || it.length<6)
            }
            if(verify_btn.isEnabled){

            }
        }
    }

    private fun showTimer(milliSecInFut:Long){
        resend_sms.isEnabled = false
        resend_sms.isClickable = false
        resend_sms.backgroundTintList = ContextCompat.getColorStateList(this, R.color.Fadedwhite)

        object :CountDownTimer(milliSecInFut, 1000){
            override fun onTick(p0: Long) {
                timer.text = "${p0/1000}"
            }
            override fun onFinish() {
                resend_sms.isClickable = true
                resend_sms.isClickable = true
                resend_sms.backgroundTintList = ContextCompat.getColorStateList(applicationContext, R.color.white)
            }
        }.start()
    }

    private fun setStrings() {
        val span = SpannableString(getString(R.string.Otp_heading, phoneNumber))
        val clickableSpan = object :ClickableSpan(){
            override fun onClick(p0: View) {
                //send back
                startActivity(Intent(applicationContext, LoginActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
            }
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.color = resources.getColor(R.color.white)
            }
        }
        span.setSpan(clickableSpan, span.length-25, span.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        waiting_text.movementMethod = LinkMovementMethod.getInstance()
        waiting_text.text = span
    }

    override fun onBackPressed() {}
}