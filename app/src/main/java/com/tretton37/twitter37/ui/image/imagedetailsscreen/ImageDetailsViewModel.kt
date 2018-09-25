package com.tretton37.twitter37.ui.image.imagedetailsscreen

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import com.tretton37.twitter37.utils.AppConstants
import javax.inject.Inject

class ImageDetailsViewModel @Inject constructor(): ViewModel() {

    val imageUrl = ObservableField<String>(AppConstants.EMPTY)

}