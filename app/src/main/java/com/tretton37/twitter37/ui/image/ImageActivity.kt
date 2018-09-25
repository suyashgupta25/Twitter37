package com.tretton37.twitter37.ui.image

import android.databinding.DataBindingUtil
import com.tretton37.twitter37.R
import com.tretton37.twitter37.databinding.ActivityImageBinding
import com.tretton37.twitter37.ui.common.base.BaseActivity

class ImageActivity : BaseActivity() {

    companion object {
        val TAG = ImageActivity::class.java.getSimpleName()
    }

    val binding: ActivityImageBinding by lazy {
        DataBindingUtil.setContentView<ActivityImageBinding>(this, R.layout.activity_image)
    }
}