package com.sduduzog.slimlauncher.ui.main

import android.os.Build
import android.util.TypedValue
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.sduduzog.slimlauncher.R

abstract class StatusBarThemeFragment : Fragment() {


    /**
     * @return [android.view.View] of the [androidx.fragment.app.Fragment] extending this class to apply flags to
     */
    abstract fun getFragmentView(): View


    override fun onResume() {
        super.onResume()
        // When the Fragment resumes, check the theme and set the status bar color accordingly.
        val settings = context!!.getSharedPreferences(getString(R.string.prefs_settings), AppCompatActivity.MODE_PRIVATE)
        val active = settings.getInt(getString(R.string.prefs_settings_key_theme), 0)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when (active) {
                0, 3, 5 -> {
                    val flags = activity!!.window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    getFragmentView().systemUiVisibility = flags
                }

            }
            val value = TypedValue()
            context!!.theme.resolveAttribute(R.attr.colorPrimary, value, true)
            activity!!.window.statusBarColor = value.data
        }
    }

}