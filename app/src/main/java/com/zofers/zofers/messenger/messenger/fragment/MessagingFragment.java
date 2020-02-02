//package com.sololearn.app.fragments.messenger;
//
//
//import android.arch.lifecycle.ViewModelProviders;
////import android.arch.paging.PagedList;
//import android.content.Intent;
//import android.graphics.PorterDuff;
//import android.graphics.drawable.GradientDrawable;
//import android.os.Bundle;
//import android.os.CountDownTimer;
//import android.os.Handler;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.PopupMenu;
//import android.support.v7.widget.RecyclerView;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.util.Log;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.MenuInflater;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.EditText;
//import android.widget.ImageButton;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.sololearn.app.R;
//import com.sololearn.app.activities.AppActivity;
//import com.sololearn.app.activities.CreateConversationActivity;
//import com.sololearn.app.activities.GenericActivity;
//import com.sololearn.app.adapters.messenger.MessengerAdapter;
//import com.sololearn.app.dialogs.LoadingDialog;
//import com.sololearn.app.dialogs.MessageDialog;
//import com.sololearn.app.fragments.InfiniteScrollingFragment;
//import com.sololearn.app.fragments.discussion.PostPickerFragment;
//import com.sololearn.app.fragments.playground.CodePickerFragment;
//import com.sololearn.app.helpers.ColorHelper;
//import com.sololearn.app.helpers.UIHelper;
//import com.sololearn.app.launchers.ProfileLauncher;
//import com.sololearn.app.viewmodels.messenger.MessagingViewModel;
//import com.sololearn.app.viewmodels.messenger.MessengerBaseViewModel;
//import com.sololearn.app.viewmodels.messenger.PaginationViewModel;
//import com.sololearn.app.views.LoadingView;
//import com.sololearn.core.SoundService;
//import com.sololearn.app.messenger.MessengerService;
//import com.sololearn.core.models.IUserItem;
//import com.sololearn.core.models.messenger.Conversation;
//import com.sololearn.core.models.messenger.Message;
//import com.sololearn.core.models.messenger.Participant;
//import com.sololearn.core.util.StringUtils;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//import butterknife.OnClick;
//import butterknife.Unbinder;
//
//
//public class MessagingFragment extends InfiniteScrollingFragment implements MessengerService.ChatListener {
//
//    public static final String ARG_CONVERSATION_ID = "arg_conversation_id";
//    public static final String ARG_PARTICIPANT_IDS = "arg_part_ids";
//    public static final String ARG_PARTICIPANT_NAME = "arg_part_name";
//    public static final String ARG_MESSAGE_TEXT = "arg_mess_text"; // if this sent, we should create new conversation
//    public static final int REQUEST_CODE_SETTINGS = 14178;
//    public static final String EXTRA_LEAVE_PREVIOUS_CONVERSATION = "extra_navigate_back";
//
//    @BindView(R.id.messenger_new_items_text_view)
//    TextView messengerNewItemsTextView;
//    @BindView(R.id.recycler_view)
//    RecyclerView conversationRecyclerView;
//    @BindView(R.id.messenger_input_text)
//    EditText messageInputEditText;
//    @BindView(R.id.send_image_button)
//    ImageButton sendImageButton;
//    @BindView(R.id.requested_conversation_layout)
//    LinearLayout requestConversationLayout;
//    @BindView(R.id.cannot_respond_layout)
//    TextView cannotRespondTextView;
//    @BindView(R.id.bottom_action_bar_relativeLayout)
//    ViewGroup bottomActionBar;
//    @BindView(R.id.loading_view)
//    LoadingView loadingView;
//    @BindView(R.id.default_text)
//    TextView defaultText;
//
//    private MessengerAdapter adapter;
//    private Conversation conversation;
//    private String conversationID;
//    private int[] participantIds;
//    private CountDownTimer typingTimer;
//    private boolean stillTyping = false;
//    private MessagingViewModel viewModel;
//    private LinearLayoutManager layoutManager;
//    private int profileId;
//
//    private Unbinder unbinder;
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        profileId = getApp().getUserManager().getId();
//        getAppActivity().requestInputResize();
//        conversationID = getArguments().getString(ARG_CONVERSATION_ID);
//        conversation = getApp().getBus().popSticky(Conversation.class);
//        setHasOptionsMenu(getActivity() instanceof GenericActivity);
//        adapter = new MessengerAdapter(profileId);
//        viewModel = ViewModelProviders.of(this).get(MessagingViewModel.class);
//        profileId = getApp().getUserManager().getId();
//
//    }
//
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_messenger, container, false);
//
//        unbinder = ButterKnife.bind(this, view);
//        loadingView.setErrorRes(R.string.internet_connection_failed);
//        loadingView.setOnRetryListener(() -> {
//            viewModel.reset();
//            load();
//        });
//
//        load();
//        viewModel.setLoadingListener(new MessengerBaseViewModel.OnLoadingFinishedListener() {
//            @Override
//            public void onSuccess(int count) {
//                adapter.setHasReachedEnd(count < PaginationViewModel.LOAD_COUNT, count > 0);
//                if (count == 0) {
//                    setLoadingMode(LoadingView.NONE);
//                }
//            }
//
//            @Override
//            public void onFailure() {
//                if (adapter.getItemCount() == 0) {
//                    setLoadingMode(LoadingView.ERROR);
//                }
//            }
//        });
//
//        if (conversation != null) {
//            adapter.setConversation(conversation);
//            setMode(conversation);
//        }
//        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true) {
//            @Override
//            public boolean supportsPredictiveItemAnimations() {
//                return false;
//            }
//        };
//        conversationRecyclerView.setLayoutManager(layoutManager);
//        conversationRecyclerView.setAdapter(adapter);
//        conversationRecyclerView.getItemAnimator().setRemoveDuration(0); // this was interrupting animation on message tap
//        conversationRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                if (layoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
//                    messengerNewItemsTextView.setVisibility(View.GONE);
//                }
//            }
//        });
//        messengerNewItemsTextView.getBackground().mutate().setColorFilter(ColorHelper.getAttributeColor(sendImageButton.getContext(), R.attr.textColorPrimaryColoredDark), PorterDuff.Mode.SRC_IN);
//
//        adapter.setListener(new MessengerAdapter.Listener() {
//            @Override
//            public void onAvatarClick(IUserItem userItem) {
//                if (!conversation.isBlocked() && !conversation.getParticipant(userItem.getUserId()).isBlocked()) {
//                    navigate(ProfileLauncher.create().forUser(userItem));
//                }
//            }
//
//            @Override
//            public void onInfoClick(Message message) {
//                MessageDialog.build(getContext())
//                        .setTitle(R.string.messenger_not_sent_info)
//                        .setNegativeButton(R.string.action_delete)
//                        .setPositiveButton(R.string.messenger_action_resend)
//                        .setListener(result -> {
//                            if (result == MessageDialog.YES) {
//                                viewModel.resendMessage(message);
//                                new Handler().postDelayed(() -> {
//                                    adapter.checkPendingMessages();
//                                }, 5000);
//                            } else if (result == MessageDialog.NO) {
//                                viewModel.deleteMessage(message.getLocalId());
//                            }
//                        })
//                        .create().show(getChildFragmentManager(), null);
//            }
//        });
//        UIHelper.closeKeyboardOnTouch(conversationRecyclerView, true);
//
//        messageInputEditText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                sendBtnEnabled(s.toString().trim().length() > 0 && loadingView.getVisibility() != View.VISIBLE);
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if (StringUtils.isNullOrWhitespace(s)) return;
//                if (typingTimer == null && conversationID != null) {
//                    getApp().getMessenger().userTyping(profileId, conversationID, true);
//                    typingTimer = new CountDownTimer(5000, 2000) {
//                        @Override
//                        public void onTick(long millisUntilFinished) {
//                            if (stillTyping) {
//                                getApp().getMessenger().userTyping(profileId, conversationID, true);
//                                cancel();
//                                start();
//                                stillTyping = false;
//                            }
//                        }
//
//                        @Override
//                        public void onFinish() {
//                            getApp().getMessenger().userTyping(profileId, conversationID, false);
//                            typingTimer = null;
//                        }
//                    };
//                    typingTimer.start();
//                } else {
//                    stillTyping = true;
//                }
//
//            }
//        });
//        if (getAppActivity().isNightMode()) {
//            GradientDrawable drawable = (GradientDrawable) messageInputEditText.getBackground();
//            drawable.setStroke((int) getResources().getDimension(R.dimen.one_dp), ColorHelper.getAttributeColor(getContext(), R.attr.dividerColor));
//        }
//        sendBtnEnabled(false);
//        return view;
//    }
//
//    private void load() {
//        setLoadingMode(LoadingView.LOADING);
//        if (conversationID != null) {
//            init();
//        } else { // find by participants
//            participantIds = getArguments().getIntArray(ARG_PARTICIPANT_IDS);
//            setName(getArguments().getString(ARG_PARTICIPANT_NAME));
//
//            String text = getArguments().getString(ARG_MESSAGE_TEXT);
//            if (text != null) {
//                createAndInit(text);
//            } else {
//                viewModel.findConversation(participantIds, new MessengerService.mCallback<Conversation>() {
//                    @Override
//                    public void onResponse(Conversation response) {
//                        if (!isAlive()) return;
//                        if (response != null) {
//                            conversationID = response.getId();
//                            conversation = response;
//                            init();
//                        } else {
//                            setLoadingMode(LoadingView.NONE);
//                        }
//                    }
//
//                    @Override
//                    public void onFailure() {
//                        setLoadingMode(LoadingView.ERROR);
//                    }
//                });
//            }
//        }
//    }
//
//    private void init() {
//        viewModel.init(conversationID, () -> {
////                    MessageDialog.build(getContext())
////                            .setTitle(R.string.messenger_conversation_not_exist_title)
////                            .setMessage(R.string.messenger_conversation_not_exist_body)
////                            .setPositiveButton(R.string.action_ok)
////                            .setListener(result -> {
//                    viewModel.removeConversation(conversationID);
//                    if (isAlive()) {
//                        getActivity().finish();
//                    }
////                            })
////                            .create().show(getChildFragmentManager(), null)
//                }
//        );
//
//        observeMessages();
//        observeConversation();
//        getApp().getMessenger().setChatListener(conversationID, this); // also called in onResume
//        getApp().getMessenger().setState(MessagingFragment.class, conversationID); // also called in onResume
//    }
//
//    private void observeMessages() {
//        viewModel.getMessages().observe(this, (response) -> {
//
//            if (response == null) return;
//            if (response.size() > 0) {
//                adapter.setAll(response);
//                scrollIfAppropriate();
//                setLoadingMode(LoadingView.NONE);
//                seen(response.get(0));
//            } else if (conversation != null && conversation.getLastMessage() == null) {
//                setLoadingMode(LoadingView.NONE);
//            }
//
//        });
//    }
//
//    private void observeConversation() {
//        viewModel.getConversation().observe(this, (response) -> {
//            if (response != null && getActivity() != null) {
//
//                int oldMode = viewModel.getMode();
//                setMode(response);
//                if (oldMode != viewModel.getMode() || conversation == null) {
//                    getActivity().invalidateOptionsMenu();
//                    getApp().hideSoftKeyboard();
//                }
//
//                setName(response.getDisplayName(getApp().getUserManager().getId(), getContext()));
//                if (conversation != null && adapter.getItemCount() > 0) {
//                    for (Participant p : response.getParticipants()) {
//                        String lastSeenMessageId = p.getLastSeenMessageId();
//                        Participant sameParticipantInDb = conversation.getParticipant(p.getUserId());
//                        if (lastSeenMessageId != null && (sameParticipantInDb == null || !lastSeenMessageId.equals(sameParticipantInDb.getLastSeenMessageId()))) {
//                            adapter.setConversation(response);
//                            setLoadingMode(LoadingView.NONE);
//
//                            adapter.updateSeenStatuses();
//                            break;
//                        }
//                    }
//                }
//                if (adapter.getConversation() == null) {
//                    adapter.setConversation(response);
//                }
//                conversation = response;
//                conversationID = response.getId();
//                if (conversation.getLastMessage() == null) {
//                    seen(null);
//                }
////                setLoading(false);
//            }
//        });
//    }
//
//
//    private void seen(Message message) {
////        if (conversation == null) return;
//        viewModel.seen(conversationID, message);
//    }
//
//    private void setMode(Conversation conversation) {
//        viewModel.detectMode(conversation, profileId);
//        if (requestConversationLayout == null || getActivity() == null) return;
//        if (viewModel.getMode() == MessagingViewModel.MODE_REQUESTS) {
//            bottomActionBar.setVisibility(View.INVISIBLE);
//            cannotRespondTextView.setVisibility(View.GONE);
//            requestConversationLayout.setVisibility(View.VISIBLE);
//        } else if (viewModel.getMode() == MessagingViewModel.MODE_CANNOT_RESPOND) {
//            bottomActionBar.setVisibility(View.INVISIBLE);
//            cannotRespondTextView.setVisibility(View.VISIBLE);
//            requestConversationLayout.setVisibility(View.GONE);
//        } else {
//            bottomActionBar.setVisibility(View.VISIBLE);
//            cannotRespondTextView.setVisibility(View.GONE);
//            requestConversationLayout.setVisibility(View.GONE);
//        }
//    }
//
//    private void sendBtnEnabled(boolean isEnabled) {
//        if (sendImageButton.isEnabled() == isEnabled) return;
//
//        sendImageButton.setEnabled(isEnabled);
//        if (isEnabled) {
//            sendImageButton.getDrawable().mutate().setColorFilter(ColorHelper.getAttributeColor(sendImageButton.getContext(), R.attr.textColorPrimaryColoredDark), PorterDuff.Mode.SRC_IN);
//        } else {
//            sendImageButton.getDrawable().mutate().setColorFilter(getResources().getColor(R.color.gray), PorterDuff.Mode.SRC_IN);
//        }
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        if (conversationID != null) {
//            getApp().getMessenger().setChatListener(conversationID, this);
//            getApp().getMessenger().setState(MessagingFragment.class, conversationID);
//            for (Integer userId : getApp().getMessenger().getTypingUsers(conversationID)) {
//                onTyping(userId, true);
//            }
//        }
//        getApp().getSoundService().requestSound(SoundService.TYPING);
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        getApp().getMessenger().clearState();
//        getApp().getMessenger().setChatListener(conversationID, null);
//        getApp().getSoundService().releaseSound(SoundService.TYPING);
//        adapter.clearTypingList();
//    }
//
//    @Override
//    public void onRestart() {
//        super.onRestart();
//        if (viewModel != null) {
//            viewModel.authAndConnect(null);
//        }
//    }
//
//    @OnClick(R.id.send_image_button)
//    public void onSendImageButtonClickListener() {
//
//        String messageText = messageInputEditText.getText().toString().trim();
//        if (StringUtils.isNullOrWhitespace(messageText)) {
//            return;
//        }
//        defaultText.setVisibility(View.GONE);
//        messageInputEditText.setText("");
//        if (conversationID != null) {
//            getApp().getMessenger().userTyping(profileId, conversationID, false);
//            if (typingTimer != null) {
//                typingTimer.cancel();
//                typingTimer = null;
//            }
//        }
//        if (conversation == null) { // should be created
//            if (getActivity() instanceof CreateConversationActivity) {
//                openStandAlone(messageText);
//            } else {
//                createAndInit(messageText);
//            }
//        } else if (getActivity() instanceof CreateConversationActivity) {
//            getApp().getMessenger().sendMessage(messageText, conversationID);
//            openStandAlone(conversation);
//        } else { // usual situation
//            getApp().getMessenger().sendMessage(messageText, conversationID);
//            scrollIfAppropriate();
//            new Handler().postDelayed(() -> {
//                adapter.checkPendingMessages();
//            }, 5000);
//        }
//    }
//
//    private void createAndInit(String messageText) {
//        getApp().getMessenger().createGroupConversation(messageText, participantIds, null, new MessengerService.mCallback<Conversation>() {
//            @Override
//            public void onResponse(Conversation response) {
//                if (response == null) {
//                    somethingWentWrongToast();
//                    return;
//                }
//                conversationID = response.getId();
//                conversation = response;
//                init();
//                viewModel.insertConversation(response);
//            }
//
//            @Override
//            public void onFailure() {
//                setLoadingMode(LoadingView.ERROR);
//            }
//        });
//    }
//
//    @OnClick(R.id.insert_button)
//    public void onInsertButtonClickListener(View view) {
//        PopupMenu popupMenu = new PopupMenu(getContext(), view, Gravity.NO_GRAVITY, R.attr.actionOverflowMenuStyle, 0);
//        Menu menu = popupMenu.getMenu();
//        popupMenu.getMenuInflater().inflate(R.menu.discussion_post_insert_menu, menu);
//
//        popupMenu.setOnMenuItemClickListener(item -> {
//            switch (item.getItemId()) {
//                case R.id.action_insert_code:
//                    navigateForResult(CodePickerFragment.class, CodePickerFragment.REQUEST_INSERT);
//                    return true;
//                case R.id.action_insert_post:
//                    navigateForResult(PostPickerFragment.class, PostPickerFragment.REQUEST_INSERT);
//                    return true;
//            }
//            return false;
//        });
//
//        popupMenu.show();
//    }
//
//    @OnClick(R.id.accept_request_button)
//    public void onAcceptRequestButtonClickListener() {
//        final LoadingDialog loadingDialog = new LoadingDialog();
//        loadingDialog.show(getChildFragmentManager());
//        viewModel.updateStatus(Participant.STATUS_APPROVED, new MessengerService.mCallback() {
//            @Override
//            public void onResponse(Object response) {
//                Participant me = conversation.getParticipant(profileId);
//                me.setStatus(Participant.STATUS_APPROVED);
//                conversation.setParticipantStatus(profileId, Participant.STATUS_APPROVED);
//                viewModel.insertConversation(conversation);
//                loadingDialog.dismiss();
//                requestConversationLayout.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onFailure() {
//                loadingDialog.dismiss();
//                AppActivity activity = getAppActivity();
//                if (activity == null) return;
//                MessageDialog.showNoConnectionDialog(activity, activity.getSupportFragmentManager());
//            }
//        });
//    }
//
//    @OnClick(R.id.delete_request_button)
//    public void onDeleteRequestButtonClickListener() {
//        MessageDialog.build(getContext())
//                .setTitle(R.string.messenger_decline_conversation_request_title)
//                .setMessage(R.string.messenger_decline_conversation_request_message)
//                .setNegativeButton(R.string.action_cancel)
//                .setPositiveButton(R.string.action_decline)
//                .setListener(result -> {
//                    if (result == MessageDialog.YES) {
//                        final LoadingDialog loadingDialog = new LoadingDialog();
//                        loadingDialog.show(getChildFragmentManager());
//                        viewModel.updateStatus(Participant.STATUS_REJECTED, new MessengerService.mCallback() {
//                            @Override
//                            public void onResponse(Object response) {
//                                viewModel.removeConversation(conversationID);
//                                navigateBack();
//                            }
//
//                            @Override
//                            public void onFailure() {
//                                loadingDialog.dismiss();
//                                AppActivity activity = getAppActivity();
//                                if (activity == null) return;
//                                MessageDialog.showNoConnectionDialog(activity, activity.getSupportFragmentManager());
//                            }
//                        });
//                    }
//                })
//                .create().show(getChildFragmentManager(), null);
//    }
//
//    private void openStandAlone(String messageText) {
//        navigate(MessagingFragment.class, newConversationBundleBuilder(participantIds, messageText));
//        if (getActivity() != null) {
//            getActivity().finish();
//        }
//    }
//
//    private void openStandAlone(Conversation conversation) {
//        viewModel.openConversation(conversation);
//        if (getActivity() != null) {
//            getActivity().finish();
//        }
//    }
//
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == CodePickerFragment.REQUEST_INSERT) {
//            if (data != null && data.getData() != null) {
//                String url = data.getData().toString();
//                Editable oldText = messageInputEditText.getText();
//                String newText = StringUtils.isNullOrWhitespace(oldText) ? url : oldText + "\n" + url;
//                messageInputEditText.setText(newText);
////                Code code = getApp().getBus().popSticky(Code.class);
//            }
//        } else if (requestCode == REQUEST_CODE_SETTINGS) {
//            if (data != null && data.getBooleanExtra(EXTRA_LEAVE_PREVIOUS_CONVERSATION, false)) {
//                navigateBack();
//            }
//        }
//    }
//
//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//        inflater.inflate(R.menu.messenger_menu, menu);
//        MenuItem settings = menu.getItem(0);
//        settings.setVisible(conversation != null && viewModel.getMode() != MessagingViewModel.MODE_REQUESTS);
//        settings.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                Bundle args = new Bundle();
//                args.putString(ARG_CONVERSATION_ID, conversation.getId());
//                navigateForResult(ConversationSettingsFragment.class, args, REQUEST_CODE_SETTINGS);
//                return false;
//            }
//        });
//    }
//
//    @Override
//    public void onNewMessage(Message message) {
//        scrollIfAppropriate();
//
//        if (layoutManager.findFirstCompletelyVisibleItemPosition() > 3) {
//            messengerNewItemsTextView.setVisibility(View.VISIBLE);
//        }
//    }
//
//    @Override
//    public void onTyping(final int userId, boolean isTyping) {
//        if (conversation == null || userId == profileId) {
//            return;
//        }
//        Participant user = conversation.getUser(userId);
//        if (isTyping) {
//            boolean addedNew = adapter.addTypingUser(user);
//            if (addedNew && isResumed()) {
//                scrollIfAppropriate();
//                getApp().getSoundService().play(SoundService.TYPING);
//            }
//        } else {
//            adapter.removeTypingUser(user);
//        }
//
//    }
//
//    @Override
//    public void onConversationRemove(String id) {
//        if (id != null && id.equals(conversationID) && isAlive()) {
//            getActivity().finish();
//        }
//    }
//
//    private void scrollIfAppropriate() {
//        if (layoutManager != null && layoutManager.findFirstVisibleItemPosition() < 3) {
//            conversationRecyclerView.scrollToPosition(0);
//        }
//    }
//
//    @OnClick(R.id.messenger_new_items_text_view)
//    public void onMessengerNewItemsTextView() {
//        conversationRecyclerView.smoothScrollToPosition(0);
//        messengerNewItemsTextView.setVisibility(View.GONE);
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        unbinder.unbind();
//    }
//
//    @Override
//    protected void loadMore() {
//        if (!adapter.hasReachedEnd()) {
//            viewModel.load(false, null);
//        }
//    }
//
//    public static Bundle bundleBuilder(int[] participantIds, @Nullable String name) {
//        Bundle args = new Bundle();
//        args.putIntArray(ARG_PARTICIPANT_IDS, participantIds);
//        if (name != null) {
//            args.putString(ARG_PARTICIPANT_NAME, name);
//        }
//        return args;
//    }
//
//    private static Bundle newConversationBundleBuilder(int[] participantIds, String messageText) {
//        Bundle args = bundleBuilder(participantIds, null);
//        args.putString(ARG_MESSAGE_TEXT, messageText);
//        return args;
//    }
//
//    protected void somethingWentWrongToast() {
//        Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
//    }
//
//    public void setLoadingMode(int mode) {
//        if (loadingView == null) {
//            Log.d("MessagingFragment", "setLoadingMode:" + mode + ", loadingView is null");
//            return;
//        }
//        loadingView.setMode(mode);
//        defaultText.setVisibility(View.GONE);
//
//        if (mode == LoadingView.NONE) {
//            if (messageInputEditText.getText().length() > 0) {
//                sendBtnEnabled(true);
//            }
//            if (adapter.getItems().size() == 0) {
//                defaultText.setVisibility(View.VISIBLE);
//            }
//        }
//    }
//}