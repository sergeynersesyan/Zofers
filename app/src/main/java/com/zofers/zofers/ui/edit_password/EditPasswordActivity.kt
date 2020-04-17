package com.zofers.zofers.ui.edit_password

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.zofers.zofers.BaseActivity
import com.zofers.zofers.R
import com.zofers.zofers.databinding.ActivityEditPasswordBinding

class EditPasswordActivity : BaseActivity() {

    private lateinit var binding: ActivityEditPasswordBinding
    private lateinit var viewModel: EditPasswordViewModel

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, EditPasswordActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_password)
        setupViewModel()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(EditPasswordViewModel::class.java)
        viewModel.init()
    }

}