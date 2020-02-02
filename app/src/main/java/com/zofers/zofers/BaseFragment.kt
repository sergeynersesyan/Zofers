package com.zofers.zofers

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.view.View
import androidx.fragment.app.Fragment

import com.zofers.zofers.callback.PermissionRequestCallback
import com.zofers.zofers.ui.create.CreateOfferSecoundFragment

/**
 * Created by Mr Nersesyan on 19/09/2018.
 */

open class BaseFragment : Fragment() {

	fun openGallery(root: View, requestCode: Int) {
		(activity as BaseActivity).openGallery(root, requestCode, this)
	}
}
