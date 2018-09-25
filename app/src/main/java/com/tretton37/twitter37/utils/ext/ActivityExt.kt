package com.tretton37.twitter37.utils.ext

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity

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