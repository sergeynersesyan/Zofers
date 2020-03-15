package com.zofers.zofers.ui.notifications;

//import android.arch.paging.PagedListAdapter;
//import android.support.v7.util.DiffUtil;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.zofers.zofers.R;
import com.zofers.zofers.messenger.messenger.adapter.OnItemClickListener;
import com.zofers.zofers.messenger.messenger.adapter.viewHolder.LoadingViewHolder;
import com.zofers.zofers.model.Conversation;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class ConversationListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements OnItemClickListener {

    private static final int ITEM_TYPE_REQUEST = 1;
    private static final int ITEM_TYPE_LOADING = 2;

    private List<Conversation> conversations = new ArrayList<>();
    private Conversation convRequest;
    //    private boolean hasConvRequest;
    private ConversationListListener listener;
    private String userId;
    private boolean hasReachedEnd = true;

    public ConversationListAdapter(String userId, ConversationListListener listener) {
        this.userId = userId;
        this.listener = listener;

        setHasStableIds(true);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == ITEM_TYPE_LOADING) {
            return new LoadingViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_feed_load_more, parent, false));
        }

        return ConversationListViewHolder.Companion.initialize(parent, this);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == ITEM_TYPE_LOADING) {
            return; // loading
        }

        ((ConversationListViewHolder) holder).bind(getConversation(position), userId, getItemCount());
    }

    @Override
    public long getItemId(int position) {
        if (!hasReachedEnd && position == getItemCount() - 1) {
            return -1; // loading
        }
        return getConversation(position).getId().hashCode();
    }

    @Override
    public int getItemCount() {
        return conversations.size() + (convRequest == null ? 0 : 1) + (hasReachedEnd ? 0 : 1);
    }

    @Override
    public void onItemClick(RecyclerView.ViewHolder viewHolder) {
        int position = viewHolder.getAdapterPosition();
        if (position == -1) return; // item removed
        listener.onItemConversationClick(getConversation(viewHolder.getAdapterPosition()));
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && convRequest != null) {
            return ITEM_TYPE_REQUEST;
        }
        if (position == getItemCount() - 1 && !hasReachedEnd) {
            return ITEM_TYPE_LOADING;
        }
        return super.getItemViewType(position);
    }

    public void setAll(List<Conversation> newItems) {
        conversations.clear();
        conversations.addAll(newItems);
        notifyDataSetChanged();
    }

    public void clean() { // after this call make sure to notify adapter about change
//        Conversation request = null;
//        if (hasConvRequest) {
//            // we should keep unread message request item
//            request = conversations.get(0);
//        }
        conversations.clear();
        notifyDataSetChanged();
//        if (request!= null && request.isUnread(userId)) {
//            conversations.add(request);
//        } else {
//            hasConvRequest = false;
//        }
    }

    public void setConversationRequest(Conversation conversationRequest) {
        if (convRequest == null) {
            convRequest = conversationRequest;
            notifyItemInserted(0);
        } else {
            convRequest = conversationRequest;
            notifyItemChanged(0);
        }

    }

    public void setHasReachedEnd(boolean hasReachedEnd) {
        if (this.hasReachedEnd != hasReachedEnd) {
            this.hasReachedEnd = hasReachedEnd;
            if (hasReachedEnd) {
                notifyItemRemoved(getItemCount());
            } else {
                notifyItemInserted(getItemCount());
            }
        }
    }

    public boolean hasConvRequest() {
        return convRequest != null;
    }

    public void removeConvRequest() {
        if (convRequest != null) {
            convRequest = null;
            notifyItemRemoved(0);
        }
    }

    public List<Conversation> getConversations() {
        return conversations;
    }

    public interface ConversationListListener {
        void onItemConversationClick(Conversation conversation);
    }

    public Conversation getConversation(int position) {
        if (hasConvRequest()) {
            if (position == 0) {
                return convRequest;
            } else {
                return conversations.get(position - 1);
            }
        } else {
            return conversations.get(position);
        }
    }
}