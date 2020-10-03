package com.adarsh.precept

import android.animation.Animator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.adarsh.precept.auth.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {
    val auth by lazy{
        FirebaseAuth.getInstance()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        messages_anim.speed = 1.0f
        messages_anim.addAnimatorListener(object : Animator.AnimatorListener{
            override fun onAnimationStart(animation: Animator?) {
            }
            override fun onAnimationEnd(animation: Animator?) {
                if(auth.currentUser == null){
                    startActivity(Intent( baseContext, LoginActivity::class.java))
                    finish()
                }else{
                    startActivity(Intent(baseContext, MainActivity::class.java))
                    finish()
                }
            }
            override fun onAnimationCancel(animation: Animator?) {
            }
            override fun onAnimationRepeat(animation: Animator?) {
            }
        })

    }
}