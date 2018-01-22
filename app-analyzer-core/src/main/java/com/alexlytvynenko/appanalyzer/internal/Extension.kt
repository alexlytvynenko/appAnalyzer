package com.alexlytvynenko.appanalyzer.internal

import android.app.Fragment
import android.content.res.Resources
import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alexlytvynenko.appanalyzer.R
import org.jetbrains.anko.bundleOf
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by alex_litvinenko on 12.10.17.
 */
internal fun AppCompatActivity.replaceFragment(fragment: Fragment, containerId: Int = R.id.fragmentContainer, addToBackStack: Boolean = false) =
        if (addToBackStack) fragmentManager.beginTransaction().replace(containerId, fragment).addToBackStack(null).commit()
        else fragmentManager.beginTransaction().replace(containerId, fragment).commit()


internal inline fun <reified T : Fragment> instanceOf(vararg params: Pair<String, Any>)
        = T::class.java.newInstance().apply {
    arguments = bundleOf(*params)
}

fun AppCompatActivity.getCurrentFragment(): Fragment =
        fragmentManager.findFragmentById(R.id.fragmentContainer)

internal fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

internal fun Date.toDateTimeFormat(): String =
        SimpleDateFormat("MMM d, HH:mm:ss", Locale.US).format(this)

internal fun Date.toDateTimeMSFormat(): String =
        SimpleDateFormat("MMM d, HH:mm:ss.SSS", Locale.US).format(this)

internal fun dateFromString(date: String, format: String = "MM-dd HH:mm:ss.SSS"): Date {
    return SimpleDateFormat(format, Locale.US).parse(date)
}

internal inline fun consume(f: () -> Unit): Boolean {
    f()
    return true
}

internal fun screenHeight() = Resources.getSystem().displayMetrics.heightPixels