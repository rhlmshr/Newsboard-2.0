package com.newsboard.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import java.util.*

class AppViewPagerAdapter(
    fm: FragmentManager,
    fragmentList: List<Fragment>,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fm, lifecycle) {
    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }

    private var fragmentList: List<Fragment> = ArrayList()

    init {
        this.fragmentList = fragmentList
    }
}