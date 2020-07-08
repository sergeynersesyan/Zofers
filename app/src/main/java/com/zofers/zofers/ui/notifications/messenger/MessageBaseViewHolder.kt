package com.zofers.zofers.ui.notifications.messenger

import android.text.method.LinkMovementMethod
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zofers.zofers.R
import com.zofers.zofers.model.Conversation
import com.zofers.zofers.model.Message
import com.zofers.zofers.staff.DateTimeUtils
import java.util.*

abstract class MessageBaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
	var attachmentContainer: ViewGroup
	var messageTextView: TextView
	var timeTextView: TextView
	var seenTextView: TextView
	var topSpace: View
	var bottomSpace: View


	fun bind(message: Message, messageType: Int, showTime: Boolean, conversation: Conversation?, profileID: String?) {
		messageTextView.text = message.text
		makeMessageBaloonForm(messageType, showTime)
		//        //seen logic
//        List<Participant> participants = conversation.getParticipantsExcept(profileID);
//        if (participants.size() > 0) {
//            String lastSeenId = participants.get(0).getLastSeenMessageId();
//            if (profileID == message.getUserId() && (lastSeenId == null || new ObjectId(message.getId()).compareTo(new ObjectId(lastSeenId)) == 1)) {
//                seenTextView.setText(seenTextView.getContext().getString(R.string.messenger_sent));
//            } else {
//                seenTextView.setText(seenTextView.getContext().getString(R.string.messenger_seen));
//            }
//        }
//
//        seenTextView.setVisibility(seenTextDefaultVisibility(message));
//        //end seen logic
		if (message.date != null) {
			timeTextView.text = DateTimeUtils.getSmartDate(message.date, false)
			timeTextView.visibility = if (showTime) View.VISIBLE else View.GONE
		}
	}

	abstract fun makeMessageBaloonForm(messageType: Int, showTime: Boolean)
	abstract fun seenTextDefaultVisibility(message: Message): Int

	companion object {
		private val attachmentStore: Map<String, Any> = HashMap()
	}

	init {
		messageTextView = itemView.findViewById(R.id.message_textView)
		timeTextView = itemView.findViewById(R.id.message_time_textView)
		seenTextView = itemView.findViewById(R.id.message_seen_textView)
		topSpace = itemView.findViewById(R.id.top_space)
		bottomSpace = itemView.findViewById(R.id.bottom_space)
		attachmentContainer = itemView.findViewById(R.id.attachment_container)
		messageTextView.movementMethod = LinkMovementMethod.getInstance()
	}
}