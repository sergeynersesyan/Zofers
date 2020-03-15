package com.zofers.zofers.ui.notifications.messenger

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.zofers.zofers.BaseActivity
import com.zofers.zofers.R
import com.zofers.zofers.databinding.ActivityMessengerBinding

class MessengerActivity : BaseActivity() {

    companion object {
        const val ARG_CONVERSATION_ID = "arg conv id"
    }

    lateinit var binding: ActivityMessengerBinding
    lateinit var viewModel: MessengerViewModel
    lateinit var adapter: MessengerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_messenger)

        initViewModel()
        initView()
    }


    private fun initViewModel() {
        viewModel = ViewModelProvider(this).get(MessengerViewModel::class.java)
        viewModel.init(intent.extras?.getString(ARG_CONVERSATION_ID).orEmpty())
    }

    private fun initView() {
        adapter = MessengerAdapter(viewModel.currentUser!!.id)
        binding.recyclerView.adapter = adapter
        viewModel.conversation.observe(this, Observer {conversation ->
            conversation?.let {
                adapter.conversation = conversation

                val participantID = conversation.getParticipantsExcept(viewModel.currentUser?.id)[0].id
                if (viewModel.currentUser?.connections?.contains(participantID) == false) {
                    binding.requestedConversationLayout.visibility = View.VISIBLE
                } else {
                    binding.bottomActionBarRelativeLayout.visibility = View.VISIBLE
                }
            }

        })
        viewModel.messages.observe(this, Observer {
            adapter.setAll(it)
        })
    }
}
