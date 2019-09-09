package com.zofers.zofers.vvm.activity

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders

import com.zofers.zofers.R
import com.zofers.zofers.databinding.ActivityProfileBinding
import com.zofers.zofers.vvm.viewmodel.ProfileViewModel

class ProfileActivity : BaseActivity() {
	lateinit var profileActivityBinding: ActivityProfileBinding
	lateinit var profileViewModel:ProfileViewModel
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		profileActivityBinding = DataBindingUtil.setContentView(this, R.layout.activity_profile)
		profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
		profileViewModel.onCreate()
	}
}
