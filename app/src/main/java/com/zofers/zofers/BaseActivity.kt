package com.zofers.zofers

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.zofers.zofers.ui.notifications.messenger.MessengerActivity

open class BaseActivity : AppCompatActivity() {

	companion object {
		private const val WRITE_STORAGE_PERMISSION_REQUEST_CODE = 1234
	}

	private var permissionCallback: PermissionRequestCallback? = null

	val app: App
	get() = application as App

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
	}


	fun isNetworkAvailable(): Boolean {
		val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
		val netInfo = cm.activeNetworkInfo
		return netInfo != null && netInfo.isConnected
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		if (item.itemId == android.R.id.home) {
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item)
	}

	private fun promptExternalStoragePermissions(callback: PermissionRequestCallback) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
				permissionCallback = callback
				requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), WRITE_STORAGE_PERMISSION_REQUEST_CODE)
			} else {
				callback.onResponse(true)
			}
		} else {
			callback.onResponse(true)
		}
	}

	override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults)
		if (requestCode == WRITE_STORAGE_PERMISSION_REQUEST_CODE) {
			for (i in permissions.indices) {
				val permission = permissions[i]
				val grantResult = grantResults[i]

				if (permission == Manifest.permission.WRITE_EXTERNAL_STORAGE) {
					if (grantResult == PackageManager.PERMISSION_GRANTED) {
						permissionCallback?.onResponse(true)
					} else {
						permissionCallback?.onResponse(false)
					}
				}
			}
		}
		permissionCallback = null
	}

	fun openGallery(root: View, requestCode: Int, fragment: BaseFragment? = null) {
		promptExternalStoragePermissions(object : PermissionRequestCallback {
			override fun onResponse(granted: Boolean) {
				if (granted) {
					val intent = Intent()
					intent.type = "image/*"
					intent.action = Intent.ACTION_GET_CONTENT
					intent.addCategory(Intent.CATEGORY_OPENABLE)
					if (fragment == null) {
						startActivityForResult(Intent.createChooser(intent, "Select picture"), requestCode)
					} else {
						fragment.startActivityForResult(Intent.createChooser(intent, "Select picture"), requestCode)
					}
				} else {
					val snackbar = Snackbar.make(root, R.string.image_permission_denied, Snackbar.LENGTH_LONG)

					snackbar.setAction("Open settings") {
						val intent = Intent()
						intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
						val uri = Uri.fromParts("package", getPackageName(), null)
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
						intent.setData(uri)
						startActivity(intent)
					}
					snackbar.show()
				}
			}
		})
	}
}

interface PermissionRequestCallback {
	fun onResponse(granted: Boolean)
}
