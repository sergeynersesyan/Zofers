//package com.zofers.zofers.messenger.messenger.adapter;
//
//import android.animation.ValueAnimator;
////import android.arch.paging.PagedList;
////import android.arch.paging.PagedListAdapter;
//import android.content.ClipData;
//import android.content.ClipboardManager;
//import android.content.Context;
//import android.graphics.PorterDuff;
//import android.graphics.drawable.Animatable;
//import android.graphics.drawable.Drawable;
//import android.support.annotation.NonNull;
//import android.support.v7.widget.PopupMenu;
//import android.support.v7.widget.RecyclerView;
//import android.text.method.LinkMovementMethod;
//import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.animation.DecelerateInterpolator;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.zofers.zofers.R;
//import com.zofers.zofers.model.Conversation;
//import com.zofers.zofers.model.Message;
//import com.zofers.zofers.model.Participant;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class MessengerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
//
//    private List<Message> items = new ArrayList<>();
//    private ArrayList<Participant> typingList = new ArrayList<>();
//    private Conversation conversation;
//    private int profileID;
//    private String selectedItemId = "";
//    private Listener listener;
//    private boolean hasReachedEnd = true;
//
//    private static final String PAYLOAD_OPEN_INFO = "payload_open";
//    private static final String PAYLOAD_CLOSE_INFO = "payload_close";
//    private static final String PAYLOAD_CHANGE_BALLOON_FORM = "PAYLOAD_CHANGE_BALLOON_FORM";
//
//    private static final int TYPE_MESSAGE_TO_ME = 1;
//    private static final int TYPE_MESSAGE_BY_ME = 2;
//    private static final int TYPE_MESSAGE_TYPING = 3;
//    private static final int TYPE_MESSAGE_SERVICE = 4;
//    private static final int TYPE_LOADING = 5;
//
//    private final int ANIMATION_DEFAULT_DURATION = 250;
//
//    public Conversation getConversation() {
//        return conversation;
//    }
//
//    public List<Message> getItems() {
//        return items;
//    }
//
//    public void setConversation(Conversation conversation) {
//        boolean notify = this.conversation == null;
//        this.conversation = conversation;
//        if (notify) {
//            notifyDataSetChanged();
//        }
//    }
//
//    public MessengerAdapter(int profileID) {
////        super(DIFF_CALLBACK);
//        this.profileID = profileID;
//        setHasStableIds(true);
//    }
//
//    public void setListener(Listener listener) {
//        this.listener = listener;
//    }
//
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
//        if (viewType == TYPE_MESSAGE_TO_ME) {
//            return new MessageToMeViewHolder(inflater.inflate(R.layout.item_message_to_me, parent, false));
//        } else if (viewType == TYPE_MESSAGE_BY_ME) {
//            return new MessageByMeViewHolder(inflater.inflate(R.layout.item_message_by_me, parent, false));
//        } else if (viewType == TYPE_MESSAGE_TYPING) {
//            return new TypingViewHolder(inflater.inflate(R.layout.item_message_typing, parent, false));
//        } else if (viewType == TYPE_LOADING) {
//            return new LoadingViewHolder(inflater.inflate(R.layout.view_feed_load_more, parent, false));
//        } else {
//            return new ServiceMessageViewHolder(inflater.inflate(R.layout.item_message_service, parent, false));
//        }
//    }
//
//    @Override
//    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//        if (getItemViewType(position) == TYPE_MESSAGE_TYPING) {
//            ((TypingViewHolder) holder).bind(typingList.get(position));
//            return;
//        }
//        if (position - typingList.size() == items.size()) {
//            return; // loading
//        }
//
//        Message message = items.get(position - typingList.size());
//        if (message == null) return;
//        if (getItemViewType(position) == TYPE_MESSAGE_SERVICE) {
//            ((ServiceMessageViewHolder) holder).bind(message);
//        } else if (getItemViewType(position) == TYPE_MESSAGE_BY_ME) {
//            ((MessageByMeViewHolder) holder).bind(message, getMessageType(position - typingList.size()), showTime(position - typingList.size()));
//        } else {
//            ((MessageToMeViewHolder) holder).bind(message, getMessageType(position - typingList.size()), showTime(position - typingList.size()));
//        }
//    }
//
//    @Override
//    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
//        if (payloads.contains(PAYLOAD_OPEN_INFO)) {
//            MessageBaseViewHolder viewHolder = (MessageBaseViewHolder) holder;
//            float messageTranslation = viewHolder.timeTextView.getContext().getResources().getDimension(R.dimen.messenger_small_text_height);
//            boolean showTime = viewHolder.timeTextView.getVisibility() == View.GONE;
//            if (showTime) {
//                viewHolder.timeTextView.setVisibility(View.VISIBLE);
//                viewHolder.timeTextView.setAlpha(0);
//                viewHolder.timeTextView.animate().alpha(1).setInterpolator(new DecelerateInterpolator()).setDuration(ANIMATION_DEFAULT_DURATION);
//                viewHolder.messageTextView.setTranslationY(-messageTranslation);
//                viewHolder.messageTextView.animate().translationY(0).setDuration(ANIMATION_DEFAULT_DURATION);
//                viewHolder.attachmentContainer.setTranslationY(-messageTranslation);
//                viewHolder.attachmentContainer.animate().translationY(0).setDuration(ANIMATION_DEFAULT_DURATION);
//                if (viewHolder instanceof MessageToMeViewHolder) {
//                    ((MessageToMeViewHolder) viewHolder).avatarContainer.setTranslationY(-messageTranslation);
//                    ((MessageToMeViewHolder) viewHolder).avatarContainer.animate().translationY(0).setDuration(ANIMATION_DEFAULT_DURATION);
//                } else {
//                    ((MessageByMeViewHolder) viewHolder).infoImage.setTranslationY(-messageTranslation);
//                    ((MessageByMeViewHolder) viewHolder).infoImage.animate().translationY(0).setDuration(ANIMATION_DEFAULT_DURATION);
//                }
//            }
//            Message message = items.get(position - typingList.size());
//            if (viewHolder.seenTextView.getVisibility() == View.GONE && !message.isUnsent()) {
//                viewHolder.seenTextView.setVisibility(View.VISIBLE);
//                viewHolder.seenTextView.setAlpha(0);
//                viewHolder.seenTextView.setTranslationY(-messageTranslation - (showTime ? messageTranslation : 0));
//                viewHolder.seenTextView.animate().translationY(0).alpha(1).setDuration(ANIMATION_DEFAULT_DURATION);
//                viewHolder.seenHeadsRecyclerView.setTranslationY(-messageTranslation - (showTime ? messageTranslation : 0));
//                viewHolder.seenHeadsRecyclerView.animate().translationY(0).setDuration(ANIMATION_DEFAULT_DURATION);
//            } else if (showTime) {
//                viewHolder.seenTextView.setTranslationY(-messageTranslation);
//                viewHolder.seenTextView.animate().translationY(0).setDuration(ANIMATION_DEFAULT_DURATION);
//            }
//
//        } else if (payloads.contains(PAYLOAD_CLOSE_INFO)) {
//            MessageBaseViewHolder viewHolder = (MessageBaseViewHolder) holder;
//            Message message = items.get(position - typingList.size());
//            int seenVisibility = viewHolder.seenTextDefaultVisibility(message);
//            viewHolder.seenTextView.setVisibility(seenVisibility);
//            boolean hideTime = !showTime(position);
//
//            int messageTranslation = (int) viewHolder.timeTextView.getContext().getResources().getDimension(R.dimen.messenger_small_text_height);
//            if (hideTime) {
//                viewHolder.timeTextView.setVisibility(View.GONE);
//                animateMargin(viewHolder.messageTextView, messageTranslation, 0);
//                if (viewHolder instanceof MessageByMeViewHolder) {
//                    animateMargin(((MessageByMeViewHolder) viewHolder).infoImage, messageTranslation / 2, 0);
//                }
//            }
//            if (conversation.isGroup()) {
//                animateMargin(viewHolder.seenHeadsRecyclerView, messageTranslation, 0);
//            }
//        } else if (payloads.contains(PAYLOAD_CHANGE_BALLOON_FORM)) {
//            if (holder instanceof MessageBaseViewHolder) {
//                ((MessageBaseViewHolder) holder).makeMessageBaloonForm(getMessageType(position - typingList.size()), showTime(position - typingList.size()));
//            }
//        } else {
//            super.onBindViewHolder(holder, position, payloads);
//        }
//    }
//
//    private void animateMargin(View view, int from, int to) {
//        UIHelper.setMargins(view, 0, from, UIHelper.CURRENT_MARGIN, 0);
//        ValueAnimator va = ValueAnimator.ofInt(from, to);
//        va.setDuration(ANIMATION_DEFAULT_DURATION);
//        va.addUpdateListener(animation ->
//                UIHelper.setMargins(view, 0, (int) animation.getAnimatedValue(), UIHelper.CURRENT_MARGIN, 0));
//        va.start();
//    }
//
//    private int getMessageType(int index) {
////        List<Message> items = getCurrentList();
//        if (items == null) return MessageType.ALONE;
//        boolean hasTop = false, hasBottom = false;
//        if (index < items.size() - 1 && items.get(index).getUserId() == items.get(index + 1).getUserId() && !showTime(index) && items.get(index + 1).getType() == Message.TYPE_DEFAULT) {
//            hasTop = true;
//        }
//        if (index > 0 && items.get(index).getUserId() == items.get(index - 1).getUserId() && !showTime(index - 1) && items.get(index - 1).getType() == Message.TYPE_DEFAULT) {
//            // in case of group conversation check that received message users
//            hasBottom = true;
//        }
//        if (hasTop) {
//            return hasBottom ? MessageType.CENTER : MessageType.BOTTOM;
//        } else {
//            return hasBottom ? MessageType.TOP : MessageType.ALONE;
//        }
//    }
//
//    private boolean showTime(int index) {
////        List<Message> items = getCurrentList();
//        return index == items.size() - 1 || items.get(index).getDate().getTime() - items.get(index + 1).getDate().getTime() > 20 * 60 * 1000;
//    }
//
//    public boolean addTypingUser(Participant typingUser) {
//        for (Participant p : typingList) {
//            if (p.getUserId() == typingUser.getUserId()) {
//                return false;
//            }
//        }
//        this.typingList.add(typingUser);
//        notifyItemInserted(this.typingList.size() - 1);
//        return true;
//    }
//
//    public void removeTypingUser(Participant typingUser) {
//        for (int i = 0; i < typingList.size(); i++) {
//            Participant p = typingList.get(i);
//            if (p.getUserId() == typingUser.getUserId()) {
//                typingList.remove(i);
//                notifyItemRemoved(i);
//            }
//        }
//    }
//
//    public void clearTypingList() {
//        int typingsize = typingList.size();
//        if (typingsize > 0) {
//            typingList.clear();
//            notifyItemRangeRemoved(0, typingsize);
//        }
//
//    }
//
//    @Override
//    public long getItemId(int position) {
//        if (position < typingList.size()) {
//            return -typingList.get(position).getUserId();
//        }
//        if (position == items.size() + typingList.size()) {
//            return 0; // loading
//        }
//        return items.get(position - typingList.size()).getLocalId().hashCode();
//    }
//
//    @Override
//    public int getItemCount() {
//        return conversation == null ? 0 : items.size() + typingList.size() + (hasReachedEnd || items.size() == 0 ? 0 : 1);
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        int typingOffset = typingList.size();
//        if (position < typingOffset) {
//            return TYPE_MESSAGE_TYPING;
//        } else if (position - typingOffset == items.size()) {
//            return TYPE_LOADING;
//        } else {
//            Message message = items.get(position - typingOffset);
//            if (message.getType() == Message.TYPE_SERVICE) {
//                return TYPE_MESSAGE_SERVICE;
//            }
//            if (message.getUserId() == profileID) {
//                return TYPE_MESSAGE_BY_ME;
//            }
//            return TYPE_MESSAGE_TO_ME;
//        }
//
//
//    }
//
//
//    public void setAll(List<Message> messageList) {
//        if (itemsExist(messageList)) return;
//        if (newMessageAdded(messageList)) return;
//        if (loadingMoreMessages(messageList)) return;
//
//        this.items.clear();
//        items.addAll(messageList);
//        notifyDataSetChanged();
//    }
//
//    private boolean loadingMoreMessages(List<Message> newItems) {
//        int existingItemsSize = items.size();
//        if (existingItemsSize >= newItems.size()) return false;
//        for (int i = 0; i < existingItemsSize; i++) {
//            if (!newItems.get(i).getId().equals(items.get(i).getId())) { // note that getLocalIds can be different, and if we notify adapter, items will blink, because stable id changed
//                return false;
//            }
//        }
//        List<Message> loadedItems = newItems.subList(existingItemsSize, newItems.size());
//        items.addAll(loadedItems);
//        if (existingItemsSize > 0) {
//            notifyItemChanged(typingList.size() + existingItemsSize - 1, PAYLOAD_CHANGE_BALLOON_FORM); // last item changed baloon form, and loading is removed
//        }
////        if (loadedItems.size() > 1) {
//        notifyItemRangeInserted(typingList.size() + existingItemsSize, loadedItems.size());
////        }
//        return true;
//    }
//
//    private boolean itemsExist(List<Message> newItems) {
//        if (items.size() < newItems.size()) return false;
//        for (int i = 0; i < newItems.size(); i++) {
//            if (!newItems.get(i).getId().equals(items.get(i).getId())) { // note that getLocalIds can be different, and if we notify adapter, items will blink, because stable id changed
//                return false;
//            }
//        }
//        int oldItemsCount = items.size() - newItems.size();
//        items = newItems;
//        if (oldItemsCount > 0) {
//            notifyItemRangeRemoved(typingList.size()+ newItems.size(), oldItemsCount);
//        }
//        return true;
//    }
//
//    private boolean newMessageAdded(List<Message> newItems) {
//        if (newItems.size() > 1 && itemsExist(newItems.subList(1, newItems.size()))) {
//            items.add(0, newItems.get(0));
//            notifyItemChanged(typingList.size() + 1, PAYLOAD_CHANGE_BALLOON_FORM);
//            notifyItemInserted(typingList.size());
//            return true;
//        }
//        return false;
//    }
//
//    public void updateSeenStatuses() {
//        // temporary solution
//        notifyDataSetChanged();
//    }
//
//    public void setHasReachedEnd(boolean hasReachedEnd, boolean newItemsLoaded) {
//        if (this.hasReachedEnd != hasReachedEnd) {
//            this.hasReachedEnd = hasReachedEnd;
//            if (hasReachedEnd) {
//                if (!newItemsLoaded) {
//                    notifyItemRemoved(getItemCount());
//                }
//            } else {
//                notifyItemInserted(getItemCount());
//            }
//        }
//    }
//
//    public boolean hasReachedEnd() {
//        return hasReachedEnd;
//    }
//
//    private void onMessageClick(Message message) {
//        int position = -1;
//        for (int i = 0; i < items.size(); i++) {
//            if (items.get(i).getLocalId().equals(message.getLocalId())) {
//                position = i + typingList.size();
//            }
//        }
//        if (message.getLocalId().equals(selectedItemId)) {
//            notifyItemChanged(position, PAYLOAD_CLOSE_INFO);
//            selectedItemId = "";
//        } else {
//            if (!StringUtils.isNullOrWhitespace(selectedItemId)) {
//                for (int i = 0; i < items.size(); i++) {
//                    if (items.get(i).getLocalId().equals(selectedItemId)) {
//                        notifyItemChanged(i + typingList.size(), PAYLOAD_CLOSE_INFO);
//                        break;
//                    }
//                }
//            }
//            selectedItemId = message.getLocalId();
//            notifyItemChanged(position, PAYLOAD_OPEN_INFO);
//        }
//    }
//
//    private boolean onMessageLongClick(View v, Message message) {
//
//        PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
//        Menu menu = popupMenu.getMenu();
//        popupMenu.getMenuInflater().inflate(R.menu.menu_message, menu);
//
//        popupMenu.setOnMenuItemClickListener(item -> {
//            switch (item.getItemId()) {
//                case R.id.action_copy:
//                    ClipboardManager clipboard = (ClipboardManager) v.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
//                    CharSequence text = message.getText();
//                    clipboard.setPrimaryClip(ClipData.newPlainText(text, text));
//                    break;
//            }
//            return true;
//        });
//
//        popupMenu.show();
//        return true;
//    }
//
//    public void checkPendingMessages() {
//        for (int i = 0; i < items.size(); i++) {
//            if (items.get(i).isInternal()) {
//                notifyItemChanged(i + typingList.size());
//            }
//        }
//    }
//
//    class MessageByMeViewHolder extends MessageBaseViewHolder {
//        ImageView infoImage;
//
//        MessageByMeViewHolder(View itemView) {
//            super(itemView);
//            infoImage = itemView.findViewById(R.id.info_icon);
//        }
//
//        public void bind(Message message, int messageType, boolean showTime) {
//            super.bind(message, messageType, showTime, conversation, profileID);
//            messageTextView.setOnClickListener(v -> onMessageClick(message));
//            messageTextView.setOnLongClickListener(v -> onMessageLongClick(v, message));
//            if (message.getLocalId().equals(selectedItemId)) {
//                //because when recreates, it is not in selected state anymore
//                selectedItemId = "";
//            }
//            if (message.isUnsent()) {
//                infoImage.setVisibility(View.VISIBLE);
//                seenTextView.setVisibility(View.GONE);
//            } else {
//                infoImage.setVisibility(View.GONE);
//            }
//
//            infoImage.setOnClickListener(view -> {
//                listener.onInfoClick(message);
//            });
//        }
//
//        @Override
//        public void makeMessageBaloonForm(int messageType, boolean showTime) {
//            if (messageType == MessageType.ALONE) {
//                topSpace.setVisibility(View.VISIBLE);
//                bottomSpace.setVisibility(View.VISIBLE);
//                messageTextView.setBackgroundResource(R.drawable.message_baloon_my);
//            } else {
//                if (messageType == MessageType.TOP) {
//                    topSpace.setVisibility(View.VISIBLE);
//                    bottomSpace.setVisibility(View.GONE);
//                    messageTextView.setBackgroundResource(R.drawable.message_baloon_my_top);
//                } else if (messageType == MessageType.CENTER) {
//                    topSpace.setVisibility(View.GONE);
//                    bottomSpace.setVisibility(View.GONE);
//                    messageTextView.setBackgroundResource(R.drawable.message_baloon_my_center);
//                } else if (messageType == MessageType.BOTTOM) {
//                    topSpace.setVisibility(View.GONE);
//                    bottomSpace.setVisibility(View.VISIBLE);
//                    messageTextView.setBackgroundResource(R.drawable.message_baloon_my_bottom);
//                }
//            }
//            messageTextView.getBackground().setColorFilter(ColorHelper.getAttributeColor(messageTextView.getContext(), R.attr.colorPrimaryAlternative), PorterDuff.Mode.SRC_IN);
//            timeTextView.setVisibility(showTime ? View.VISIBLE : View.GONE);
//        }
//
//        public int seenTextDefaultVisibility(Message message) {
//            if (conversation.isGroup()) {
//                return View.GONE;
//            } else {
//                List<Participant> participants = conversation.getParticipantsExcept(profileID);
//                if (participants.size() > 0 && message.getId().equals(participants.get(0).getLastSeenMessageId())) {
//                    return View.VISIBLE;
//                } else {
//                    return View.GONE;
//                }
//            }
//        }
//    }
//
//    class MessageToMeViewHolder extends MessageBaseViewHolder {
//        AvatarDraweeView avatar;
//        ViewGroup avatarContainer;
//
//        MessageToMeViewHolder(View itemView) {
//            super(itemView);
//            avatar = itemView.findViewById(R.id.icon_avatar);
//            avatarContainer = itemView.findViewById(R.id.avatar_container);
//        }
//
//        public void bind(Message message, int messageType, boolean showTime) {
//            super.bind(message, messageType, showTime, conversation, profileID);
//            if (conversation != null) {
//                Participant user = conversation.getUser(message.getUserId());
//                if (user != null) {
//                    avatar.setUser(user);
//                    avatar.setImageURI(user.getAvatarUrl());
//                }
//                avatar.setOnClickListener((view) -> {
//                    listener.onAvatarClick(user);
//                });
//            }
//            messageTextView.setOnClickListener(v -> {
//                onMessageClick(message);
//            });
//            messageTextView.setOnLongClickListener(v -> onMessageLongClick(v, message));
//            if (message.getLocalId().equals(selectedItemId)) {
//                //because when recreates, it is not in selected state anymore
//                selectedItemId = "";
//            }
//        }
//
////        public void makeMessageBaloonForm(int messageType, boolean showTime) {
////            if (messageType == MessageType.ALONE) {
////                topSpace.setVisibility(View.VISIBLE);
////                bottomSpace.setVisibility(View.VISIBLE);
////                avatar.setVisibility(View.VISIBLE);
////                messageTextView.setBackgroundResource(R.drawable.message_baloon);
////            } else {
////                if (messageType == MessageType.TOP) {
////                    topSpace.setVisibility(View.VISIBLE);
////                    bottomSpace.setVisibility(View.GONE);
////                    messageTextView.setBackgroundResource(R.drawable.message_baloon_tome_top);
////                    avatar.setVisibility(View.GONE);
////                } else if (messageType == MessageType.CENTER) {
////                    topSpace.setVisibility(View.GONE);
////                    bottomSpace.setVisibility(View.GONE);
////                    messageTextView.setBackgroundResource(R.drawable.message_baloon_tome_center);
////                    avatar.setVisibility(View.GONE);
////                } else if (messageType == MessageType.BOTTOM) {
////                    topSpace.setVisibility(View.GONE);
////                    bottomSpace.setVisibility(View.VISIBLE);
////                    messageTextView.setBackgroundResource(R.drawable.message_baloon_tome_bottom);
////                    avatar.setVisibility(View.VISIBLE);
////                }
////            }
////            timeTextView.setVisibility(showTime ? View.VISIBLE : View.GONE);
////        }
//
//        @Override
//        public int seenTextDefaultVisibility(Message message) {
//            return View.GONE;
//        }
//    }
//
//    class TypingViewHolder extends RecyclerView.ViewHolder {
//        ImageView typingImageAnimation;
//        AvatarDraweeView avatar;
////        AnimationDrawable frameAnimation;
//
//        TypingViewHolder(View itemView) {
//            super(itemView);
//            typingImageAnimation = (ImageView) itemView.findViewById(R.id.message_typing_imageView);
//            avatar = (AvatarDraweeView) itemView.findViewById(R.id.icon_avatar);
////            frameAnimation = (AnimationDrawable) layout.getBackground();
//        }
//
//        public void bind(Participant user) {
////            typingImageAnimation.setImageDrawable(VectorDrawableCompat.create(context.getResources(), R.drawable.anim_message_typing, null));
//            Drawable drawable = typingImageAnimation.getDrawable();
//            if (drawable instanceof Animatable) {
//                ((Animatable) drawable).start();
//            }
//            avatar.setUser(user);
//            avatar.setImageURI(user.getAvatarUrl());
////            frameAnimation.start();
//
//            avatar.setOnClickListener(v -> listener.onAvatarClick(user));
//        }
//    }
//
//    class ServiceMessageViewHolder extends RecyclerView.ViewHolder {
//        TextView text;
//
//        ServiceMessageViewHolder(View itemView) {
//            super(itemView);
//            text = itemView.findViewById(R.id.message_text);
//            text.setMovementMethod(LinkMovementMethod.getInstance());
//        }
//
//        public void bind(Message message) {
//            text.setText(PostParser.parseMentions(text.getContext(), message.getText(), true));
//        }
//    }
//
//
//
//    public interface Listener {
//        void onAvatarClick(IUserItem userItem);
//
//        void onInfoClick(Message message);
//    }
//
//    public class MessageType {
//        public static final int ALONE = 0;
//        public static final int TOP = 1;
//        public static final int CENTER = 2;
//        public static final int BOTTOM = 3;
//    }
//
//}
