package com.adarsh.precept

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.location.GnssNavigationMessage
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.activity_otp.*
import java.util.concurrent.TimeUnit

class OtpActivity : AppCompatActivity() {
    private lateinit var phoneNumber:String
    lateinit var callbacks : PhoneAuthProvider.OnVerificationStateChangedCallbacks
    var storedVerificationId:String? = null
    var resendToken: PhoneAuthProvider.ForceResendingToken? = null
    private var countdown:CountDownTimer? = null
    private lateinit var progressDialog:ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)
        phoneNumber = intent.getStringExtra("phoneNumber")
        verifyButton()
        initView()
        verify()

        //manual verifying the user.
        verify_btn.setOnClickListener {
            val code = otp_num.text.toString()
            if(code.isNotEmpty() && !storedVerificationId.isNullOrBlank()){
                val credential = PhoneAuthProvider.getCredential(storedVerificationId!!, code)
                signInWithPhoneAuthCredential(credential)
            }
        }

        //resending the request
        resend_sms.setOnClickListener {
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,    // Phone number to verify
                60,             // Timeout duration
                TimeUnit.SECONDS, // Unit of timeout
                this,              // Activity (for callback binding)
                callbacks,
                resendToken)
            progressDialog = createProgressDialog("Sending a Verification Code", false)
            progressDialog.show()
            countdown?.cancel()
            showTimer(60000)
        }
    }

    private fun verify() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber,    // Phone number to verify
            60,             // Timeout duration
            TimeUnit.SECONDS, // Unit of timeout
            this,              // Activity (for callback binding)
            callbacks)      // OnVerificationStateChangedCallbacks
        countdown?.cancel()
        progressDialog = createProgressDialog("Sending a Verification Code", false)
        progressDialog.show()
        showTimer(60000)
    }

    private fun initView() {
        setStrings()
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                if(::progressDialog.isInitialized){
                    progressDialog.dismiss()
                }
                Log.d("TAG", "onVerificationCompleted:${credential.smsCode}")
                if(credential.smsCode != null){
                    otp_num.setText(credential.smsCode)
                }
                signInWithPhoneAuthCredential(credential)
            }
            override fun onVerificationFailed(e: FirebaseException) {
                if(::progressDialog.isInitialized){
                    progressDialog.dismiss()
                }
                Log.w("TAG", "onVerificationFailed", e)
                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                } else if (e is FirebaseTooManyRequestsException) { }
                ///Show error
                createMaterialAlertDialog("Check your mobile network and try again!")
            }
            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                if(::progressDialog.isInitialized){
                    progressDialog.dismiss()
                }
                storedVerificationId = verificationId
                resendToken = token
            }
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        progressDialog = createProgressDialog("Matching the Credentials...", false)
        progressDialog.show()
        val auth = FirebaseAuth.getInstance()
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    if(::progressDialog.isInitialized){
                        progressDialog.dismiss()
                    }
                    // Sign in success, update UI with the signed-in user's information
                    val user = task.result?.user
                    Log.d("TAG", "signInWithCredential:success $user")
                    startActivity(Intent(applicationContext, ProfileSetUpActivity::class.java)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))

                } else {
                    if(::progressDialog.isInitialized){
                        progressDialog.dismiss()
                    }

                    createMaterialAlertDialog("Sign In failed, try again!")

                    // Sign in failed, display a message and update the UI
                    Log.w("TAG", "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                }
            }
    }

    private fun verifyButton() {
        verify_btn.isClickable = false
        verify_btn.isEnabled = false
        otp_num.addTextChangedListener {
            verify_btn.apply {
                isEnabled = !(it.isNullOrEmpty() || it.length<6)
                isClickable = !(it.isNullOrEmpty() || it.length<6)
            }
            if(verify_btn.isEnabled){
                verify_btn.backgroundTintList = ContextCompat.getColorStateList(this, R.color.white)
                verify_btn.isClickable = true
            }else{
                verify_btn.backgroundTintList = ContextCompat.getColorStateList(this, R.color.Fadedwhite)
                verify_btn.isClickable = false
            }
        }
    }

    private fun showTimer(milliSecInFut:Long){
        resend_sms.isEnabled = false
        resend_sms.isClickable = false
        resend_sms.backgroundTintList = ContextCompat.getColorStateList(this, R.color.Fadedwhite)
        countdown = object : CountDownTimer(milliSecInFut, 1000) {
            override fun onTick(p0: Long) {
                timer.text = "${p0 / 1000}"
            }
            override fun onFinish() {
                resend_sms.isClickable = true
                resend_sms.isClickable = true
                resend_sms.backgroundTintList =
                    ContextCompat.getColorStateList(applicationContext, R.color.white)
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

    fun createMaterialAlertDialog(message: String){
        MaterialAlertDialogBuilder(this).apply {
            setMessage(message)
            setPositiveButton("Okay"){dialog, _ ->
                startActivity(Intent(applicationContext, LoginActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
                dialog.dismiss()
            }
            setCancelable(false)
            create()
            show()
        }
    }

    override fun onBackPressed() {}

    override fun onDestroy() {
        super.onDestroy()
        countdown?.cancel()
    }
}

fun Context.createProgressDialog(message: String, isCancelable: Boolean): ProgressDialog{
    return ProgressDialog(this).apply {
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        setMessage(message)
    }
}