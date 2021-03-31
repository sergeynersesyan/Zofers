package com.zofers.zofers.ui.notifications.messenger

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.zofers.zofers.BaseActivity
import com.zofers.zofers.R
import com.zofers.zofers.databinding.ActivityMessengerBinding
import com.zofers.zofers.model.Offer
import com.zofers.zofers.model.Participant
import com.zofers.zofers.staff.MessageHelper
import com.zofers.zofers.staff.States
import com.zofers.zofers.ui.offer.OfferActivity
import com.zofers.zofers.ui.profile.ProfileActivity

class MessengerActivity : BaseActivity() {

	companion object {
		const val ARG_CONVERSATION_ID = "arg conv id"

		fun start(context: Context, conversationID: String?) {
			val intent = Intent(context, MessengerActivity::class.java)
			intent.putExtra(ARG_CONVERSATION_ID, conversationID)
			context.startActivity(intent)
		}
	}

	lateinit var binding: ActivityMessengerBinding
	lateinit var viewModel: MessengerViewModel
	lateinit var adapter: MessengerAdapter

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = DataBindingUtil.setContentView(this, R.layout.activity_messenger)

		initViewModel()
		initView()
		title = ""
	}

	override fun onResume() {
		super.onResume()
		app.isMessengerActive = true
	}

	override fun onPause() {
		super.onPause()
		app.isMessengerActive = false
	}


	private fun initViewModel() {
		viewModel = ViewModelProvider(this).get(MessengerViewModel::class.java)
		viewModel.init(intent.extras?.getString(ARG_CONVERSATION_ID).orEmpty())
	}

	private fun initView() {
		adapter = MessengerAdapter(viewModel.currentUser!!.id, object : MessengerAdapter.Listener {
			override fun onAvatarClick(profile: Participant?) {
				ProfileActivity.start(this@MessengerActivity, profile?.id.orEmpty())
			}

			override fun onOfferRequest(offerID: String, listener: ((Offer?) -> Unit)) {
				viewModel.getOffer(offerID, listener)
			}

			override fun onOfferClick(offer: Offer) {
				val intent = Intent(this@MessengerActivity, OfferActivity::class.java)
				intent.putExtra(OfferActivity.EXTRA_OFFER, offer)
				startActivity(intent)
			}

			override fun loadMore() {
				viewModel.loadMore()
			}

		})
		binding.recyclerView.adapter = adapter

		binding.sendImageButton.setOnClickListener {
			val message = binding.messengerInputText.text.trim().toString()
			if (message.isNotEmpty()) {
				viewModel.sendMessage(message)
				binding.messengerInputText.setText("")
			}
		}
		binding.rejectRequestButton.setOnClickListener {
			viewModel.rejectConversation()
		}

		binding.acceptRequestButton.setOnClickListener {
			viewModel.accept(getString(R.string.message_conversation_accept))
		}

		viewModel.conversation.observe(this, Observer { conversation ->
			conversation?.let {
				adapter.conversation = conversation
				updateRespondLayout()
				title = viewModel.opponent?.name
			}

		})
		viewModel.messages.observe(this, Observer { messages ->
			if (!messages.isNullOrEmpty()) {
				adapter.setAll(messages)
				viewModel.updateLastSeenMessage()
			}
		})
		viewModel.updateViewEvent.observe(this, Observer {
			updateRespondLayout()
		})
		viewModel.reachedToEnd.observe(this, Observer { hasReachedEnd ->
			if (hasReachedEnd) {
				adapter.hasReachedEnd = true
			}
		})

		viewModel.state.observe(this, Observer {state ->
			when (state) {
				States.ERROR -> MessageHelper.showErrorToast(this)
				States.FINISH -> finish()
			}
		})
	}

	private fun updateRespondLayout() {
		if (viewModel.isMyConnection) {
			binding.bottomActionBarRelativeLayout.visibility = View.VISIBLE
			binding.requestedConversationLayout.visibility = View.GONE
		} else {
			binding.requestedConversationLayout.visibility = View.VISIBLE
			binding.bottomActionBarRelativeLayout.visibility = View.INVISIBLE
		}
	}
}
