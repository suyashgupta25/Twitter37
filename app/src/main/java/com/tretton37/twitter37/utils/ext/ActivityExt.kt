package com.tretton37.twitter37.utils.ext

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.view.View
import android.view.inputmethod.InputMethodManager

/**
 * Created by suyashg
 *
 * Utility class for activity fragments operations
 */
@Suppress("UNCHECKED_CAST")
fun <T> FragmentActivity.findFragmentByTag(tag: String): T = supportFragmentManager.findFragmentByTag(tag) as T

inline fun FragmentActivity.replaceFragment(containerViewId: Int, f: () -> Fragment, tag: String, bundle: Bundle? = null): Fragment? {
    return f().apply {
        this.arguments = bundle
        supportFragmentManager?.beginTransaction()?.replace(containerViewId, this, tag)?.commit()
    }
}

inline fun FragmentActivity.addFragment(containerViewId: Int, f: () -> Fragment, bundle: Bundle): Fragment? {
    val manager = supportFragmentManager
    return f().apply {
        this.arguments = bundle
        manager?.beginTransaction()?.add(containerViewId, this)?.addToBackStack(null)?.commit()
    }
}

inline fun FragmentActivity.hideKeyboard(activity: FragmentActivity?, view: View?) {
    val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
    imm!!.hideSoftInputFromWindow(view?.windowToken, 0)
}

inline fun FragmentActivity.hideKeyboard(activity: FragmentActivity?) {
    val view = activity?.currentFocus
    if (view != null) {
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}