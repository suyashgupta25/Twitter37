package com.tretton37.twitter37.ui.home

import android.databinding.DataBindingUtil
import android.os.Bundle
import com.tretton37.twitter37.R
import com.tretton37.twitter37.databinding.ActivityHomeBinding
import com.tretton37.twitter37.ui.common.base.BaseActivity
import com.tretton37.twitter37.ui.home.tweetsscreen.TweetsFragment
import com.tretton37.twitter37.utils.ext.replaceFragment

class HomeActivity : BaseActivity() {

    val binding: ActivityHomeBinding by lazy {
        DataBindingUtil.setContentView<ActivityHomeBinding>(this, R.layout.activity_home)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentContainerId = binding.flHomeContent.id
        replaceFragment(fragmentContainerId, ::TweetsFragment)
    }

}