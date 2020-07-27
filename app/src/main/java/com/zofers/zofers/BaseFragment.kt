package com.zofers.zofers

import android.view.View
import androidx.fragment.app.Fragment

/**
 * Created by Mr Nersesyan on 19/09/2018.
 */

open class BaseFragment : Fragment() {

	protected val app: App?
	get() = (activity as BaseActivity?)?.app

	fun openGallery(root: View, requestCode: Int) {
		(activity as BaseActivity).openGallery(root, requestCode, this)
	}
}
