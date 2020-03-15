package com.zofers.zofers.ui.notifications.messenger;

import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zofers.zofers.R;
import com.zofers.zofers.model.Conversation;
import com.zofers.zofers.model.Message;
import com.zofers.zofers.model.ObjectId;
import com.zofers.zofers.model.Participant;
import com.zofers.zofers.staff.DateTimeUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.recyclerview.widget.RecyclerView;

public abstract class MessageBaseViewHolder extends RecyclerView.ViewHolder {
    private static Map<String, Object> attachmentStore = new HashMap<>();
    public ViewGroup attachmentContainer;
    public TextView messageTextView;
    public TextView timeTextView;
    public TextView seenTextView;
    public RecyclerView seenHeadsRecyclerView;
    public View topSpace;
    public View bottomSpace;

    public MessageBaseViewHolder(View itemView) {
        super(itemView);
        messageTextView = itemView.findViewById(R.id.message_textView);
        timeTextView = itemView.findViewById(R.id.message_time_textView);
        seenTextView = itemView.findViewById(R.id.message_seen_textView);
        topSpace = itemView.findViewById(R.id.top_space);
        bottomSpace = itemView.findViewById(R.id.bottom_space);
        messageTextView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void bind(Message message, int messageType, boolean showTime, Conversation conversation, String profileID) {
        messageTextView.setText(message.getText());
        makeMessageBaloonForm(messageType, showTime);
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
        if (message.getDate() != null) {
            timeTextView.setText(DateTimeUtils.getSmartDate(message.getDate(), false));
            timeTextView.setVisibility(showTime ? View.VISIBLE : View.GONE);
        }
    }

    public abstract void makeMessageBaloonForm(int messageType, boolean showTime);

    public abstract int seenTextDefaultVisibility(Message message);
}
