package com.zofers.zofers.ui.edit_password

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.zofers.zofers.BaseActivity
import com.zofers.zofers.R
import com.zofers.zofers.databinding.ActivityEditPasswordBinding
import com.zofers.zofers.staff.MessageHelper
import com.zofers.zofers.staff.States
import com.zofers.zofers.view.LoadingDialog

class EditPasswordActivity : BaseActivity() {

    private lateinit var binding: ActivityEditPasswordBinding
    private lateinit var viewModel: EditPasswordViewModel
    private var loadingDialog: LoadingDialog? = null

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, EditPasswordActivity::class.java)
            context.startActivity(intent)
        }
        private const val REQUEST_CODE_PASSWORD_EDIT: Int = 1000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_password)
        setTitle(R.string.edit_password)
        setupViewModel()
        setupView()
    }

    private fun setupView() {
        binding.editSaveButton.setOnClickListener {
            viewModel.save(binding.inputPassword.text.toString(), binding.inputPasswordRepeat.text.toString())
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(EditPasswordViewModel::class.java)
        viewModel.init()
        viewModel.state.observe(this, Observer {
            loadingDialog?.dismiss()
            when (it) {
                States.LOADING -> {
                    loadingDialog = LoadingDialog().apply {
                        show(supportFragmentManager, null)
                    }
                }
                States.NONE -> {}
                States.FINISH -> finish()
                States.ERROR -> {
                    MessageHelper.showErrorToast(this)
                }
                States.UNAUTHORIZED -> {
                    MessageHelper.showErrorToast(this, ", try after re-login", Toast.LENGTH_LONG)
                }
            }
        })
        viewModel.passwordShortError.observe(this, Observer {
            binding.inputLayoutPassword.error = getString(R.string.error_short_password)
        })
        viewModel.passwordsNotMatchError.observe(this, Observer {
            binding.inputPasswordRepeat.error = getString(R.string.error_passwords_not_match)
        })
    }

}