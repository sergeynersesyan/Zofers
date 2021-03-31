package com.zofers.zofers.ui.notifications

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.zofers.zofers.BaseActivity
import com.zofers.zofers.BaseFragment
import com.zofers.zofers.BottomNavigationFragment
import com.zofers.zofers.R
import com.zofers.zofers.databinding.FragmentNotificationListBinding
import com.zofers.zofers.staff.MessageHelper
import com.zofers.zofers.staff.States
import com.zofers.zofers.ui.notifications.messenger.MessengerActivity

class ConversationsFragment : BottomNavigationFragment() {

	private lateinit var messagesViewModel: ConversationsViewModel
	private lateinit var binding: FragmentNotificationListBinding
	private lateinit var conversationListAdapter: ConversationListAdapter
	override val title: String
		get() = getString(R.string.messages)


	override fun onCreateView(
			inflater: LayoutInflater,
			container: ViewGroup?,
			savedInstanceState: Bundle?
	): View? {
		initViewModel()
		val root = inflater.inflate(R.layout.fragment_notification_list, container, false)
		binding = DataBindingUtil.bind(root)!!
		initView()

		(activity as? BaseActivity)?.supportActionBar?.title = title

		return root
	}

	private fun initView() {
		conversationListAdapter = ConversationListAdapter(messagesViewModel.currentUser?.id) {conversation ->
			activity?.let {
				MessengerActivity.start(context!!, conversation.id)
			}
		}
		binding.recyclerView.adapter = conversationListAdapter
		binding.refreshLayout.setOnRefreshListener {
			messagesViewModel.requestMessages()
		}
	}

	override fun onResume() {
		super.onResume()

		val notificationManager = activity?.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
		notificationManager?.cancelAll()
	}


	override fun onHiddenChanged(hidden: Boolean) {
		super.onHiddenChanged(hidden)
		app?.isMessengerActive = !hidden
	}

	private fun initViewModel() {
		messagesViewModel = ViewModelProvider(this).get(ConversationsViewModel::class.java)
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
					MessageHelper.showSnackBar(binding.root, getString(R.string.conversation_list_update_error))
				}
				States.NONE -> binding.refreshLayout.isRefreshing = false
			}

		})
	}
}

