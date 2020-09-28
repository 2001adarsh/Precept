package com.adarsh.precept

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        val myAdapter = ScreenSliderAdapter(this)
        viewPager.adapter = myAdapter
        TabLayoutMediator(tabs, viewPager,
            TabLayoutMediator.TabConfigurationStrategy { tab, position ->
                when (position) {
                    0 -> {
                        tab.text = "CHATS"
                        myAdapter.notifyDataSetChanged()
                    }
                    1 -> {
                        tab.text = "PEOPLE"
                        myAdapter.notifyDataSetChanged()
                    }
                }
            })

    }
}