package com.zofers.zofers.ui.edit_profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.zofers.zofers.R
import com.zofers.zofers.databinding.ActivityEditProfileBinding

class EditProfileActivity : AppCompatActivity() {

	private lateinit var binding: ActivityEditProfileBinding

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile)
	}

}