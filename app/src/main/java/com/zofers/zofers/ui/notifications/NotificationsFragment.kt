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
import com.zofers.zofers.staff.MessageHelper
import com.zofers.zofers.staff.States
import com.zofers.zofers.ui.notifications.messenger.MessengerActivity

class NotificationsFragment : BaseFragment() {

	private lateinit var messagesViewModel: NotificationsViewModel
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
		conversationListAdapter = ConversationListAdapter(messagesViewModel.currentUser?.id) {
			findNavController().navigate(
					R.id.action_navigation_notifications_to_messengerActivity,
					Bundle().apply { putString(MessengerActivity.ARG_CONVERSATION_ID, it.id) }
			)
		}
		binding.recyclerView.adapter = conversationListAdapter
		binding.refreshLayout.setOnRefreshListener {
			messagesViewModel.requestMessages()
		}
	}

	private fun initViewModel() {
		messagesViewModel = ViewModelProvider(this).get(NotificationsViewModel::class.java)
		messagesViewModel.requestMessages()

		messagesViewModel.conversations.observe(viewLifecycleOwner, Observer { conversationList ->
			conversationListAdapter.setAll(conversationList)
			binding.emptyView.visibility = if (conversationList.isNullOrEmpty()) View.VISIBLE else View.GONE
		})
		messagesViewModel.state.observe(viewLifecycleOwner, Observer { state ->
			when (state) {
				States.LOADING -> binding.refreshLayout.isRefreshing = true
				States.ERROR -> {
					binding.refreshLayout.isRefreshing = false
					MessageHelper.showErrorToast(context)
				}
				States.NONE -> binding.refreshLayout.isRefreshing = false
			}

		})
	}
}

