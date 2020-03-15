package com.zofers.zofers.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.zofers.zofers.BaseFragment
import com.zofers.zofers.R
import com.zofers.zofers.databinding.FragmentNotificationListBinding
import com.zofers.zofers.ui.notifications.messenger.MessengerActivity

class NotificationsFragment : BaseFragment() {

    private lateinit var notificationsViewModel: NotificationsViewModel
    private lateinit var binding: FragmentNotificationListBinding
    private lateinit var conversationListAdapter: ConversationListAdapter


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        initViewModel()
        val root = inflater.inflate(R.layout.fragment_notification_list, container, false)
        binding = DataBindingUtil.bind(root)!!
        initView()

        return root
    }

    private fun initView() {
        conversationListAdapter = ConversationListAdapter(notificationsViewModel.currentUser?.id) {
            findNavController().navigate(
                    R.id.action_navigation_notifications_to_messengerActivity,
                    Bundle().apply { putString(MessengerActivity.ARG_CONVERSATION_ID, it.id) }
            )
        }
        binding.recyclerView.adapter = conversationListAdapter
        notificationsViewModel.conversations.observe(viewLifecycleOwner, Observer { conversationList ->
            conversationListAdapter.setAll(conversationList)
            binding.emptyView.visibility = if (conversationList.isNullOrEmpty()) View.VISIBLE else View.GONE
        })
    }

    private fun initViewModel() {
        notificationsViewModel = ViewModelProvider(this).get(NotificationsViewModel::class.java)
        notificationsViewModel.init()
    }
}