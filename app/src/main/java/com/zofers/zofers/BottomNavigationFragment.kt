package com.zofers.zofers

import android.view.View
import androidx.fragment.app.Fragment

/**
 * Created by Mr Nersesyan on 19/09/2018.
 */

abstract class BottomNavigationFragment : BaseFragment() {
	abstract val title: String

	override fun onHiddenChanged(hidden: Boolean) {
		super.onHiddenChanged(hidden)
		if (!hidden) {
			(activity as? BaseActivity)?.supportActionBar?.setTitle(title)
		}
	}
}

