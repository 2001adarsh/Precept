package com.adarsh.precept

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import com.adarsh.precept.adapter.ScreenSliderAdapter
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        viewPager.adapter = ScreenSliderAdapter(this)
        //Integrating the TabLayout and the Viewpager2
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "CHATS"
                1 -> tab.text = "PEOPLE"
                2 -> tab.text = "SETTINGS"
            }
        }.attach()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}