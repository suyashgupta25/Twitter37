package com.tretton37.twitter37.ui.image.imagedetailsscreen

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tretton37.twitter37.R
import com.tretton37.twitter37.databinding.FragmentImageDetailsBinding
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class ImageDetailsFragment : Fragment() {

    companion object {
        val TAG = ImageDetailsFragment::class.java.getSimpleName()
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    val viewModel: ImageDetailsViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(ImageDetailsViewModel::class.java)
    }

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_image_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBinding(view)
    }

    private fun initBinding(view: View) {
        val binding = DataBindingUtil.bind<FragmentImageDetailsBinding>(view)
        binding.let {
            it!!.viewModel = viewModel
            it.setLifecycleOwner(this)
        }
        val url = arguments?.getString(getString(R.string.param_image_details))
        url.let { viewModel.imageUrl.set(url) }
    }

}