package com.tretton37.twitter37.ui.image

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.View
import com.tretton37.twitter37.R
import com.tretton37.twitter37.databinding.ActivityImageBinding
import com.tretton37.twitter37.ui.common.base.BaseActivity
import com.tretton37.twitter37.ui.image.imagedetailsscreen.ImageDetailsFragment
import com.tretton37.twitter37.utils.ext.replaceFragment

class ImageActivity : BaseActivity() {

    companion object {
        val TAG = ImageActivity::class.java.getSimpleName()
    }

    val binding: ActivityImageBinding by lazy {
        DataBindingUtil.setContentView<ActivityImageBinding>(this, R.layout.activity_image)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        fragmentContainerId = binding.flImageContent.id
        val urlExtra = intent.getStringExtra(getString(R.string.param_image_details))
        val bundle = Bundle()
        bundle.putString(getString(R.string.param_image_details), urlExtra)
        replaceFragment(fragmentContainerId, ::ImageDetailsFragment, ImageDetailsFragment.TAG, bundle)
    }
}