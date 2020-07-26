package com.zofers.zofers.ui.notifications.messenger


import android.animation.ValueAnimator
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import coil.transform.CircleCropTransformation
import coil.transform.RoundedCornersTransformation
import com.google.common.base.Strings
import com.zofers.zofers.R
import com.zofers.zofers.messenger.messenger.adapter.viewHolder.LoadingViewHolder
import com.zofers.zofers.model.Conversation
import com.zofers.zofers.model.Message
import com.zofers.zofers.model.Offer
import com.zofers.zofers.model.Participant
import com.zofers.zofers.staff.UIHelper

//import android.arch.paging.PagedList;
//import android.arch.paging.PagedListAdapter;

class MessengerAdapter(
		private val profileID: String,
		private val listener: Listener?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

	private var items = mutableListOf<Message>()
	var conversation: Conversation? = null
		set(conversation) {
			val notify = this.conversation == null
			field = conversation
			if (notify) {
				notifyDataSetChanged()
			}
		}
	private var selectedItemId = ""
	var hasReachedEnd = false

	private val ANIMATION_DEFAULT_DURATION = 250

	init {
		setHasStableIds(true)
	}//        super(DIFF_CALLBACK);

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
		val inflater = LayoutInflater.from(parent.context)
		return if (viewType == TYPE_MESSAGE_TO_ME) {
			MessageToMeViewHolder(inflater.inflate(R.layout.item_message_to_me, parent, false))
		} else if (viewType == TYPE_MESSAGE_BY_ME) {
			MessageByMeViewHolder(inflater.inflate(R.layout.item_message_by_me, parent, false))
		} else if (viewType == TYPE_LOADING) {
			LoadingViewHolder(inflater.inflate(R.layout.view_feed_load_more, parent, false))
		} else {
			ServiceMessageViewHolder(inflater.inflate(R.layout.item_message_service, parent, false))
		}
	}

	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
		if (position == items.size) {
			listener?.loadMore()
			return  // loading
		}

		val message = items[position]
		if (getItemViewType(position) == TYPE_MESSAGE_SERVICE) {
			(holder as ServiceMessageViewHolder).bind(message)
		} else if (getItemViewType(position) == TYPE_MESSAGE_BY_ME) {
			(holder as MessageByMeViewHolder).bind(message, getMessageType(position), showTime(position))
		} else {
			(holder as MessageToMeViewHolder).bind(message, getMessageType(position), showTime(position))
		}
	}

	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: List<Any>) {
		if (payloads.contains(PAYLOAD_OPEN_INFO)) {
			val viewHolder = holder as MessageBaseViewHolder
			val messageTranslation = viewHolder.timeTextView.context.resources.getDimension(R.dimen.messenger_small_text_height)
			val showTime = viewHolder.timeTextView.visibility == View.GONE
			if (showTime) {
				viewHolder.timeTextView.visibility = View.VISIBLE
				viewHolder.timeTextView.alpha = 0f
				viewHolder.timeTextView.animate().alpha(1f).setInterpolator(DecelerateInterpolator()).duration = ANIMATION_DEFAULT_DURATION.toLong()
				viewHolder.messageTextView.translationY = -messageTranslation
				viewHolder.messageTextView.animate().translationY(0f).duration = ANIMATION_DEFAULT_DURATION.toLong()
				viewHolder.attachmentContainer.translationY = -messageTranslation
				viewHolder.attachmentContainer.animate().translationY(0f).duration = ANIMATION_DEFAULT_DURATION.toLong()
				if (viewHolder is MessageToMeViewHolder) {
					viewHolder.avatarContainer.translationY = -messageTranslation
					viewHolder.avatarContainer.animate().translationY(0f).duration = ANIMATION_DEFAULT_DURATION.toLong()
				}
			}
			if (viewHolder.seenTextView.visibility == View.GONE) {
				viewHolder.seenTextView.visibility = View.VISIBLE
				viewHolder.seenTextView.alpha = 0f
				viewHolder.seenTextView.translationY = -messageTranslation - if (showTime) messageTranslation else 0f
				viewHolder.seenTextView.animate().translationY(0f).alpha(1f).duration = ANIMATION_DEFAULT_DURATION.toLong()
			} else if (showTime) {
				viewHolder.seenTextView.translationY = -messageTranslation
				viewHolder.seenTextView.animate().translationY(0f).duration = ANIMATION_DEFAULT_DURATION.toLong()
			}

		} else if (payloads.contains(PAYLOAD_CLOSE_INFO)) {
			val viewHolder = holder as MessageBaseViewHolder
			val message = items[position]
			val seenVisibility = viewHolder.seenTextDefaultVisibility(message)
			viewHolder.seenTextView.visibility = seenVisibility
			val hideTime = !showTime(position)

			val messageTranslation = viewHolder.timeTextView.context.resources.getDimension(R.dimen.messenger_small_text_height).toInt()
			if (hideTime) {
				viewHolder.timeTextView.visibility = View.GONE
				animateMargin(viewHolder.messageTextView, messageTranslation)
			}
		} else if (payloads.contains(PAYLOAD_CHANGE_BALLOON_FORM)) {
			if (holder is MessageBaseViewHolder) {
				holder.makeMessageBaloonForm(getMessageType(position), showTime(position))
			}
		} else {
			super.onBindViewHolder(holder, position, payloads)
		}
	}

	private fun animateMargin(view: View, from: Int, to: Int = 0) {

		UIHelper.setMargins(view, 0, from, -1, 0)
		val va = ValueAnimator.ofInt(from, to)
		va.duration = ANIMATION_DEFAULT_DURATION.toLong()
		va.addUpdateListener { animation -> UIHelper.setMargins(view, 0, animation.animatedValue as Int, -1, 0) }
		va.start()
	}

	private fun getMessageType(index: Int): Int {
		var hasTop = false
		var hasBottom = false
		if (index < items.size - 1 && items[index].userId == items[index + 1].userId && !showTime(index) && items[index + 1].type == Message.TYPE_DEFAULT) {
			hasTop = true
		}
		if (index > 0 && items[index].userId == items[index - 1].userId && !showTime(index - 1) && items[index - 1].type == Message.TYPE_DEFAULT) {
			// in case of group conversation check that received message users
			hasBottom = true
		}
		return if (hasTop) {
			if (hasBottom) MessageType.CENTER else MessageType.BOTTOM
		} else {
			if (hasBottom) MessageType.TOP else MessageType.ALONE
		}
	}

	private fun showTime(index: Int): Boolean {
		return index == items.size - 1 || (items[index].date?.time
				?: 0) - (items[index + 1].date?.time ?: 0) > 20 * 60 * 1000
	}

	override fun getItemId(position: Int): Long {
		return if (position ==items.size) {
			-1
		} else {
			items[position].id.hashCode().toLong()
		}
	}

	override fun getItemCount(): Int {
		return items.size + if (hasReachedEnd || items.size == 0) 0 else 1
	}

	override fun getItemViewType(position: Int): Int {
		if (position == items.size) {
			return TYPE_LOADING
		} else {
			val message = items[position]
			if (message.type == Message.TYPE_SERVICE) {
				return TYPE_MESSAGE_SERVICE
			}
			return if (message.userId == profileID) {
				TYPE_MESSAGE_BY_ME
			} else TYPE_MESSAGE_TO_ME
		}
	}


	fun setAll(messageList: List<Message>) {
		this.items.clear()
		items.addAll(messageList)
		notifyDataSetChanged()
	}

	fun updateSeenStatuses() {
		// temporary solution
		notifyDataSetChanged()
	}

	private fun onMessageClick(message: Message) {
		var position = -1
		for (i in items.indices) {
			if (items[i].id == message.id) {
				position = i
			}
		}
		if (message.id == selectedItemId) {
			notifyItemChanged(position, PAYLOAD_CLOSE_INFO)
			selectedItemId = ""
		} else {
			if (!Strings.isNullOrEmpty(selectedItemId)) {
				for (i in items.indices) {
					if (items[i].id == selectedItemId) {
						notifyItemChanged(i, PAYLOAD_CLOSE_INFO)
						break
					}
				}
			}
			selectedItemId = message.id.orEmpty()
			notifyItemChanged(position, PAYLOAD_OPEN_INFO)
		}
	}

	internal inner class MessageByMeViewHolder
	//        ImageView infoImage;

	(itemView: View) : MessageBaseViewHolder(itemView) {

		fun bind(message: Message, messageType: Int, showTime: Boolean) {
			super.bind(message, messageType, showTime, conversation, profileID)
			messageTextView.setOnClickListener { v -> onMessageClick(message) }
			if (message.id == selectedItemId) {
				//because when recreates, it is not in selected state anymore
				selectedItemId = ""
			}

			message.offerID?.let { offerID ->
				listener?.onOfferRequest(offerID) { offer ->
					offer?.let {
						inflateOfferAttachment(it, attachmentContainer)
					}
				}
			}
			attachmentContainer.visibility = if (message.offerID.isNullOrEmpty()) View.GONE else View.VISIBLE
		}

		override fun makeMessageBaloonForm(messageType: Int, showTime: Boolean) {
			if (messageType == MessageType.ALONE) {
				topSpace.visibility = View.VISIBLE
				bottomSpace.visibility = View.VISIBLE
				messageTextView.setBackgroundResource(R.drawable.message_baloon_my)
			} else {
				if (messageType == MessageType.TOP) {
					topSpace.visibility = View.VISIBLE
					bottomSpace.visibility = View.GONE
					messageTextView.setBackgroundResource(R.drawable.message_baloon_my_top)
				} else if (messageType == MessageType.CENTER) {
					topSpace.visibility = View.GONE
					bottomSpace.visibility = View.GONE
					messageTextView.setBackgroundResource(R.drawable.message_baloon_my_center)
				} else if (messageType == MessageType.BOTTOM) {
					topSpace.visibility = View.GONE
					bottomSpace.visibility = View.VISIBLE
					messageTextView.setBackgroundResource(R.drawable.message_baloon_my_bottom)
				}
			}
			timeTextView.visibility = if (showTime) View.VISIBLE else View.GONE
		}

		override fun seenTextDefaultVisibility(message: Message): Int {
//			val participants = conversation!!.getParticipantsExcept(profileID)
//			return if (participants.size > 0 && message.id == participants.get(0).getLastSeenMessageId()) {
//				View.VISIBLE
//			} else {
				return View.GONE
//			}
		}
	}

	internal inner class MessageToMeViewHolder(itemView: View) : MessageBaseViewHolder(itemView) {
		var avatar: ImageView
		var avatarContainer: ViewGroup

		init {
			avatar = itemView.findViewById(R.id.icon_avatar)
			avatarContainer = itemView.findViewById(R.id.avatar_container)
		}

		fun bind(message: Message, messageType: Int, showTime: Boolean) {
			super.bind(message, messageType, showTime, conversation, profileID)
			conversation?.let {
				val user = it.getUser(message.userId.orEmpty())
				avatar.load(user?.avatarUrl) {
					placeholder(R.drawable.ic_avatar)
					fallback(R.drawable.ic_avatar)
					transformations(CircleCropTransformation())
				}
				avatar.setOnClickListener { listener?.onAvatarClick(user) }
			}
			messageTextView.setOnClickListener { onMessageClick(message) }
			if (message.id == selectedItemId) {
				//because when recreates, it is not in selected state anymore
				selectedItemId = ""
			}
			message.offerID?.let { offerID ->
				listener?.onOfferRequest(offerID) { offer ->
					offer?.let {
						inflateOfferAttachment(it, attachmentContainer)
					}
				}
			}
			attachmentContainer.visibility = if (message.offerID.isNullOrEmpty()) View.GONE else View.VISIBLE
		}

		override fun makeMessageBaloonForm(messageType: Int, showTime: Boolean) {
			if (messageType == MessageType.ALONE) {
				topSpace.visibility = View.VISIBLE
				bottomSpace.visibility = View.VISIBLE
				avatar.visibility = View.VISIBLE
				messageTextView.setBackgroundResource(R.drawable.message_baloon)
			} else {
				if (messageType == MessageType.TOP) {
					topSpace.visibility = View.VISIBLE
					bottomSpace.visibility = View.GONE
					messageTextView.setBackgroundResource(R.drawable.message_baloon_tome_top)
					avatar.visibility = View.GONE
				} else if (messageType == MessageType.CENTER) {
					topSpace.visibility = View.GONE
					bottomSpace.visibility = View.GONE
					messageTextView.setBackgroundResource(R.drawable.message_baloon_tome_center)
					avatar.visibility = View.GONE
				} else if (messageType == MessageType.BOTTOM) {
					topSpace.visibility = View.GONE
					bottomSpace.visibility = View.VISIBLE
					messageTextView.setBackgroundResource(R.drawable.message_baloon_tome_bottom)
					avatar.visibility = View.VISIBLE
				}
			}
			timeTextView.visibility = if (showTime) View.VISIBLE else View.GONE
		}

		override fun seenTextDefaultVisibility(message: Message): Int {
			return View.GONE
		}
	}

	private fun inflateOfferAttachment(offer: Offer, attachmentContainer: ViewGroup) {
		val view = LayoutInflater.from(attachmentContainer.context).inflate(R.layout.item_offer_small, attachmentContainer, false)
		val image: ImageView = view.findViewById(R.id.image)
		val city: TextView = view.findViewById(R.id.city_textView)
		val title: TextView = view.findViewById(R.id.title_textView)
		val costs: TextView = view.findViewById(R.id.costs_textView)
		val people: TextView = view.findViewById(R.id.people_textView)
		val peopleImage: ImageView = view.findViewById(R.id.people_imageView)


		image.load(offer.imageUrl) {
			transformations(RoundedCornersTransformation(image.context.resources.getDimension(R.dimen.default_radius)))
		}
		city.text = offer.city
		title.text = offer.name
		title.setLines(1)
		costs.text = offer.getCostText(costs.context)

		val peopleCount = offer.peopleCount
		if (peopleCount > 0) {
			val peopleText = people.context.resources.getQuantityString(offer.peopleTextResource, peopleCount, peopleCount)
			people.text = String.format(people.context.getString(R.string.for_someone), peopleText)
			people.visibility = View.VISIBLE
			peopleImage.visibility = View.VISIBLE
		} else {
			people.visibility = View.GONE
			peopleImage.visibility = View.GONE
		}
		view.setOnClickListener { view ->
			listener?.onOfferClick(offer)
		}
		attachmentContainer.addView(view)
	}

	internal inner class ServiceMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		var text: TextView

		init {
			text = itemView.findViewById(R.id.message_text)
			text.movementMethod = LinkMovementMethod.getInstance()
		}

		fun bind(message: Message) {
			text.text = message.text
		}
	}


	interface Listener {
		fun onAvatarClick(profile: Participant?)
		fun onOfferRequest(offerID: String, listener: ((Offer?) -> Unit))
		fun onOfferClick(offer: Offer)
		fun loadMore()
	}

	object MessageType {
		val ALONE = 0
		val TOP = 1
		val CENTER = 2
		val BOTTOM = 3
	}

	companion object {

		private const val PAYLOAD_OPEN_INFO = "payload_open"
		private const val PAYLOAD_CLOSE_INFO = "payload_close"
		private const val PAYLOAD_CHANGE_BALLOON_FORM = "PAYLOAD_CHANGE_BALLOON_FORM"

		private const val TYPE_MESSAGE_TO_ME = 1
		private const val TYPE_MESSAGE_BY_ME = 2
		private const val TYPE_MESSAGE_TYPING = 3
		private const val TYPE_MESSAGE_SERVICE = 4
		private const val TYPE_LOADING = 5
	}

}
