package com.zofers.zofers.messenger.messenger.adapter.viewHolder;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.zofers.zofers.R;
import com.zofers.zofers.messenger.messenger.adapter.OnItemClickListener;
import com.zofers.zofers.messenger.messenger.model.Conversation;

public class ConversationListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

	TextView userNameTextView;
	TextView lastMessageTextView;
	ImageView infoIcon;
	TextView dateTextView;
	ImageView avatar;
	View divider;

	private OnItemClickListener listener;

	public static ConversationListViewHolder initialize(ViewGroup parent, OnItemClickListener listener) {

		View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_conversation_user, parent, false);

		return new ConversationListViewHolder(itemView, listener);
	}

	ConversationListViewHolder(View itemView, OnItemClickListener listener) {
		super(itemView);

		this.listener = listener;

		itemView.setOnClickListener(this);
	}

	public void bind(final Conversation conversation, int userId, int itemCount) {

//        userId = 1044;
//        userId = 441743;

		if (conversation.isGroup()) {
			userNameTextView.setText(conversation.getDisplayName(userId, userNameTextView.getContext()));
		} else {
//            userNameTextView.setText(ProfileHelper.getNameWithBadge(userNameTextView.getContext(), conversation.getParticipantsExcept(userId).get(0)));
		}

		avatar.setImageURI(null);
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
		boolean isUnread = conversation.isUnread(userId);
		lastMessageTextView.setTypeface(null, isUnread ? Typeface.BOLD : Typeface.NORMAL);
		userNameTextView.setTypeface(null, isUnread ? Typeface.BOLD : Typeface.NORMAL);
		dateTextView.setTypeface(null, isUnread ? Typeface.BOLD : Typeface.NORMAL);
//        lastMessageTextView.setTextColor(ColorHelper.getAttributeColor(lastMessageTextView.getContext(), isUnread ? R.attr.textColorSecondary : R.attr.textColorTertiary));

		updateDivider(itemCount);
	}

	private void updateDivider(int itemCount) {
		boolean hidden = (getAdapterPosition() == itemCount - 1);
		if (hidden) {
			divider.setVisibility(View.INVISIBLE);
		} else {
			divider.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onClick(View v) {
		listener.onItemClick(this);
	}
}