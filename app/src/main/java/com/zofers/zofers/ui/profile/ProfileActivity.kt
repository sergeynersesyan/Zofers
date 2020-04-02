package com.zofers.zofers.ui.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.zofers.zofers.BaseActivity
import com.zofers.zofers.R
import com.zofers.zofers.databinding.ActivityProfileBinding


class ProfileActivity : BaseActivity() {
	lateinit var binding: ActivityProfileBinding

	companion object {
		private const val EXTRA_USER_ID = "ext_us_id"

		fun start(packageContext: Context, userID: String) {
			val intent = Intent(packageContext, ProfileActivity::class.java)
			intent.putExtra(EXTRA_USER_ID, userID)
			packageContext.startActivity(intent)
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = DataBindingUtil.setContentView(this, R.layout.activity_profile)
		supportFragmentManager
				.beginTransaction()
				.replace(
						R.id.fragment_container,
						ProfileFragment::class.java,
						Bundle().apply { putString(ProfileFragment.ARG_USER_ID, intent.getStringExtra(EXTRA_USER_ID)) }
				).commit()
	}
}
