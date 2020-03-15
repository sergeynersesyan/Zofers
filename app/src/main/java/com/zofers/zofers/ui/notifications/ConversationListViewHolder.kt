package com.zofers.zofers.ui.notifications

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import coil.transform.CircleCropTransformation

import com.zofers.zofers.R
import com.zofers.zofers.messenger.messenger.adapter.OnItemClickListener
import com.zofers.zofers.model.Conversation

class ConversationListViewHolder private constructor(itemView: View, private val listener: OnItemClickListener) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    private val userNameTextView: TextView
    private val lastMessageTextView: TextView
    private val infoIcon: ImageView
    private val dateTextView: TextView
    private val avatar: ImageView
    private val divider: View

    init {

        userNameTextView = itemView.findViewById(R.id.user_name_text_view)
        lastMessageTextView = itemView.findViewById(R.id.last_message_text_view)
        infoIcon = itemView.findViewById(R.id.info_icon)
        dateTextView = itemView.findViewById(R.id.conversation_date_text_view)
        avatar = itemView.findViewById(R.id.icon_avatar)
        divider = itemView.findViewById(R.id.divider)

        itemView.setOnClickListener(this)
    }

    fun bind(conversation: Conversation, userId: String, itemCount: Int) {

        val participant = conversation.getParticipantsExcept(userId)[0]
        userNameTextView.text = participant.name
        avatar.load(participant.avatarUrl) {
            transformations(CircleCropTransformation())
        }
        lastMessageTextView.text = conversation.lastMessage.text
        //        dateTextView.setText(DateTimeUtils.getSmartDate(conversation.getLastActionDate(), true));

        // last message logic
        //        Message lastMessage = conversation.getLastMessage();
        //        if (lastMessage != null) {
        //            String messageText = lastMessage.getText();
        //            if (lastMessage.getType() == Message.TYPE_SERVICE) {
        //                lastMessageTextView.setText(PostParser.parseMentions(lastMessageTextView.getContext(), messageText, false));
        //            } else {
        //                int lastUserId = lastMessage.getUserId();
        //                if (lastUserId == userId) {
        //                    messageText = String.format(avatar.getContext().getString(R.string.messenger_instead_your_name), messageText);
        //                } else if (conversation.isGroup()) {
        //
        //                    try {
        //                        messageText = conversation.getUser(lastUserId).getUserName() + ": " + messageText;
        //                    } catch (NullPointerException ignored) {
        //
        //                    }
        //                }
        //                lastMessageTextView.setText(messageText);
        //                infoIcon.setVisibility(lastMessage.isUnsent() ? View.VISIBLE : View.GONE);
        //            }
        //        } else {
        //            lastMessageTextView.setText("");
        //        }
        val isUnread = conversation.isUnread(userId)
        lastMessageTextView.setTypeface(null, if (isUnread) Typeface.BOLD else Typeface.NORMAL)
        userNameTextView.setTypeface(null, if (isUnread) Typeface.BOLD else Typeface.NORMAL)
        dateTextView.setTypeface(null, if (isUnread) Typeface.BOLD else Typeface.NORMAL)
        //        lastMessageTextView.setTextColor(ColorHelper.getAttributeColor(lastMessageTextView.getContext(), isUnread ? R.attr.textColorSecondary : R.attr.textColorTertiary));

        updateDivider(itemCount)
    }

    private fun updateDivider(itemCount: Int) {
        val hidden = adapterPosition == itemCount - 1
        if (hidden) {
            divider.visibility = View.INVISIBLE
        } else {
            divider.visibility = View.VISIBLE
        }
    }

    override fun onClick(v: View) {
        listener.onItemClick(this)
    }

    companion object {

        fun initialize(parent: ViewGroup, listener: OnItemClickListener): ConversationListViewHolder {

            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_conversation_user, parent, false)

            return ConversationListViewHolder(itemView, listener)
        }
    }
}