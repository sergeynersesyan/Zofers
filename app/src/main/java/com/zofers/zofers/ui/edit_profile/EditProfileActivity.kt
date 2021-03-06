package com.zofers.zofers.ui.edit_profile

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.zofers.zofers.BaseActivity
import com.zofers.zofers.R
import com.zofers.zofers.databinding.ActivityEditProfileBinding
import com.zofers.zofers.staff.MessageHelper
import com.zofers.zofers.staff.States
import com.zofers.zofers.view.LoadingDialog

class EditProfileActivity : BaseActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var viewModel: EditProfileViewModel
    private var loadingDialog: LoadingDialog? = null

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, EditProfileActivity::class.java)
            context.startActivity(intent)
        }

        fun startForResult(fragment: Fragment, requestCode: Int) {
            val intent = Intent(fragment.activity, EditProfileActivity::class.java)
            fragment.startActivityForResult(intent, requestCode)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile)
        setTitle(R.string.edit_profile)
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
                        isCancelable = true
                        show(supportFragmentManager, null)
                    }
                }
                States.NONE -> {
                    loadingDialog?.dismiss()
                }
                States.FINISH -> {
                    setResult(Activity.RESULT_OK)
                    finish()
                }
                States.ERROR -> {
                    loadingDialog?.dismiss()
                    MessageHelper.showErrorToast(this)
                }
                States.UNAUTHORIZED -> {
                    loadingDialog?.dismiss()
                    MessageHelper.showToastMessage(applicationContext, "Couldn't update your email, try after re-login", Toast.LENGTH_SHORT)
                }
            }

        })
    }

}