package com.zofers.zofers.ui.edit_profile

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.zofers.zofers.R
import com.zofers.zofers.databinding.ActivityEditProfileBinding
import com.zofers.zofers.staff.MessageHelper
import com.zofers.zofers.staff.States
import com.zofers.zofers.view.LoadingDialog

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var viewModel: EditProfileViewModel
    private var loadingDialog: LoadingDialog? = null

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, EditProfileActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile)
        setupViewModel()
        setupView()
    }

    private fun setupView() {
        binding.inputEmail.setText(viewModel.auth.currentUser?.email)
        binding.nameEditText.setText(viewModel.currentUser?.name)
        binding.descriptionEditText.setText(viewModel.currentUser?.description)
        binding.editSaveButton.setOnClickListener {
            viewModel.save(binding.nameEditText.text.toString(), binding.inputEmail.text.toString(), binding.descriptionEditText.text.toString())
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(EditProfileViewModel::class.java)
        viewModel.init()
        viewModel.emptyEmailError.observe(this, Observer {
            binding.emailInputLayout.error = "email should not be empty"
        })
        viewModel.emptyUserNameError.observe(this, Observer {
            binding.nameInputLayout.error = "user name should not be empty"
        })
        viewModel.invalidEmailError.observe(this, Observer {
            binding.emailInputLayout.error = "invalid email"
        })
        viewModel.state.observe(this, Observer {
            when (it) {
                States.LOADING -> {
                    loadingDialog = LoadingDialog().apply {
                        show(supportFragmentManager, null)
                    }
                }
                States.NONE -> {
                    loadingDialog?.dismiss()
                }
                States.FINISH -> finish()
                States.ERROR -> {
                    loadingDialog?.dismiss()
                    MessageHelper.showErrorToast(this)
                }
            }

        })
    }

}