package com.adarsh.precept.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.adarsh.precept.fragments.InBoxFragment
import com.adarsh.precept.fragments.PeopleFragment
import com.adarsh.precept.fragments.SettingsFragment

class ScreenSliderAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment = when(position){
        0 -> InBoxFragment()
        1 -> PeopleFragment()
        else -> SettingsFragment()
    }

}
