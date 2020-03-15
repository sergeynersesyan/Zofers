//package com.zofers.zofers.messenger.messenger.adapter;
//
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.text.method.LinkMovementMethod;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
////
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;

//
//public abstract class MessageBaseViewHolder extends RecyclerView.ViewHolder {
//    private static Map<String, Object> attachmentStore = new HashMap<>();
//    AttachmentHelper attachmentHelper;
//    public ViewGroup attachmentContainer;
//    public TextView messageTextView;
//    public TextView timeTextView;
//    public TextView seenTextView;
//    public RecyclerView seenHeadsRecyclerView;
//    public View topSpace;
//    public View bottomSpace;
//
//    public MessageBaseViewHolder(View itemView) {
//        super(itemView);
//        messageTextView = itemView.findViewById(R.id.message_textView);
//        timeTextView = itemView.findViewById(R.id.message_time_textView);
//        seenTextView = itemView.findViewById(R.id.message_seen_textView);
//        seenHeadsRecyclerView = itemView.findViewById(R.id.seen_heads_recyclerView);
//        topSpace = itemView.findViewById(R.id.top_space);
//        bottomSpace = itemView.findViewById(R.id.bottom_space);
//        attachmentContainer = itemView.findViewById(R.id.attachment_container);
//        attachmentHelper = new AttachmentHelper(attachmentContainer);
//        attachmentHelper.setAttachmentStore(attachmentStore);
//        messageTextView.setMovementMethod(LinkMovementMethod.getInstance());
//    }
//
//    public void bind(Message message, int messageType, boolean showTime, Conversation conversation, int profileID) {
//        if (message.getText() == null) message.setText("");
//        messageTextView.setText(PostParser.parse(messageTextView.getContext(), message.getText()));
//        attachmentHelper.setText(message.getText());
//        makeMessageBaloonForm(messageType, showTime);
//        //seen logic
//        if (message.isInternal()) {
//            seenTextView.setText(seenTextView.getContext().getString(R.string.messenger_pending));
//            seenHeadsRecyclerView.setVisibility(View.GONE);
//        } else if (conversation.isGroup()) {
//            String seenText = "";
//            ArrayList<Participant> seenParticipants = new ArrayList<>();
//            boolean seenByEveryone = true;
//            for (Participant p : conversation.getParticipants()) {
//                if (!p.isActive() || p.getUserId() == profileID) continue;
//                int compareIds = 1;
//                if (p.getLastSeenMessageId() != null) {
//                    compareIds = new ObjectId(message.getId()).compareTo(new ObjectId(p.getLastSeenMessageId()));
//                }
//                if (compareIds < 1) {
//                    seenText += p.getUserName() + ", ";
//                    if (compareIds == 0) {
//                        seenParticipants.add(p);
//                    }
//                } else {
//                    seenByEveryone = false;
//                }
//            }
//            if (seenByEveryone) {
//                seenTextView.setText(seenTextView.getContext().getString(R.string.messenger_everyone));
//            } else if (!StringUtils.isNullOrWhitespace(seenText)) {
//                seenText = String.format(seenTextView.getContext().getString(R.string.messenger_seen_by), seenText);
//                seenText = seenText.substring(0, seenText.length() - 2);
//                seenTextView.setText(seenText);
//            } else {
//                seenTextView.setText(seenTextView.getContext().getString(profileID == message.getUserId() ? R.string.messenger_sent : R.string.messenger_seen));
//            }
//            if (seenParticipants.size() > 0) {
//                SeenAdapter adapter = new SeenAdapter();
//                adapter.setItems(seenParticipants);
//                seenHeadsRecyclerView.setLayoutManager(new LinearLayoutManager(messageTextView.getContext(), LinearLayoutManager.HORIZONTAL, true));
//                seenHeadsRecyclerView.setAdapter(adapter);
//                seenHeadsRecyclerView.setVisibility(View.VISIBLE);
//            } else {
//                seenHeadsRecyclerView.setVisibility(View.GONE);
//            }
//        } else {
//            List<Participant> participants = conversation.getParticipantsExcept(profileID);
//            if (participants.size() > 0) {
//                String lastSeenId = participants.get(0).getLastSeenMessageId();
//                if (profileID == message.getUserId() && (lastSeenId == null || new ObjectId(message.getId()).compareTo(new ObjectId(lastSeenId)) == 1)) {
//                    seenTextView.setText(seenTextView.getContext().getString(R.string.messenger_sent));
//                } else {
//                    seenTextView.setText(seenTextView.getContext().getString(R.string.messenger_seen));
//                }
//            }
//
//        }
//        seenTextView.setVisibility(seenTextDefaultVisibility(message));
//        //end seen logic
//        timeTextView.setText(DateTimeUtils.getSmartDate(message.getDate(), false));
//        timeTextView.setVisibility(showTime ? View.VISIBLE : View.GONE);
//    }
//
//    public abstract void makeMessageBaloonForm(int messageType, boolean showTime);
//    public abstract int seenTextDefaultVisibility(Message message);
//}
