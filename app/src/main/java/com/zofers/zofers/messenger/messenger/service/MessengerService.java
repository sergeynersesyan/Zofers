package com.zofers.zofers.messenger.messenger.service;
//
//import android.content.Context;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Looper;
//import android.os.SystemClock;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.util.Log;
//
//import com.google.gson.Gson;
//import com.google.gson.JsonElement;
//import com.smartarmenia.dotnetcoresignalrclientjava.HubConnection;
//import com.smartarmenia.dotnetcoresignalrclientjava.HubConnectionListener;
//import com.smartarmenia.dotnetcoresignalrclientjava.HubMessage;
//import com.smartarmenia.dotnetcoresignalrclientjava.WebSocketHubConnectionP2;
//import com.sololearn.app.activities.PlayActivity;
//import com.sololearn.app.fragments.messenger.ConversationListFragment;
//import com.sololearn.app.fragments.messenger.MessagingFragment;
//import com.sololearn.app.fragments.play.GameFragment;
//import com.sololearn.app.fragments.settings.PushNotificationsFragment;
//import com.sololearn.app.notifications.NotificationManager;
//import com.sololearn.core.StorageService;
//import com.sololearn.core.UserManager;
//import com.sololearn.core.models.Profile;
//import com.sololearn.core.models.User;
//import com.sololearn.core.models.messenger.Conversation;
//import com.sololearn.core.models.messenger.Message;
//import com.sololearn.core.models.messenger.ObjectId;
//import com.sololearn.core.models.messenger.Participant;
//import com.sololearn.core.room.AppDatabase;
//import com.sololearn.core.web.SettingsResult;
//import com.sololearn.core.web.UtcDateTypeAdapter;
//import com.sololearn.core.web.WebService;
//import com.sololearn.core.web.retro.AuthResult;
//import com.sololearn.core.web.retro.MessengerAPIService;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.Executor;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//
///**
// * Created by David on 27-Jul-17.
// */
//
public class MessengerService {   }
//    private static String accessToken;
//    private Context context;
//    private List<Runnable> connectionCallbacks;
//
////    private Socket socket;
//
//    private final AppDatabase appDatabase;
//    private final MessengerAPIService api;
//    private Executor discIO;
//    private Executor mainExecutor;
//    private WebService webService;
//    private UserManager userManager;
//    private HubConnection connection;
//    private boolean isConnecting;
//    private boolean pushEnabled = true;
//    private MessengerState state;
//    //    private ComponentProvider componentProvider;
//    private NotificationManager notificationManager;
//    private StorageService storageService;
//
//    private HashMap<String, ChatListener> chatListeners = new HashMap<>();
//    private HashMap<String, Map<String, String>> notificationQueue = new HashMap<>();
//    private TypingManager typingManager;
//    public static String MESSENGER_PUSH_LOG_TAG = "messenger push";
//
//    public MessengerService(Context context, AppDatabase database, MessengerAPIService api, Executor discIO, Executor mainExecutor,
//                            UserManager userManager, WebService webService, NotificationManager notManager, StorageService storage) {
//        this.context = context;
//        appDatabase = database;
//        this.api = api;
//        this.discIO = discIO;
//        this.mainExecutor = mainExecutor;
//        this.userManager = userManager;
//        this.webService = webService;
//        notificationManager = notManager;
//        storageService = storage;
//        typingManager = new TypingManager(chatListeners);
//        discIO.execute(() -> database.messengerDao().deleteOldMessages());
////        accessToken = null; // todo remove this row
//    }
//
//    public void connect(final Runnable callback) {
//        if (isConnected()) {
//            if (callback != null) callback.run();
//            return;
//        }
//
//
//        if (connectionCallbacks == null) {
//            connectionCallbacks = new ArrayList<>();
//        }
//
//        if (callback != null) connectionCallbacks.add(callback);
//
//        if (isConnecting) return;
//        isConnecting = true;
//
//        connection = new WebSocketHubConnectionP2(MessengerAPIBuilder.BASE_URL + "hubs/chat", "Bearer " + accessToken);
//
//        connection.addListener(new HubConnectionListener() {
//            @Override
//            public void onConnected() {
//                if (connectionCallbacks != null) {
//                    for (Runnable c : connectionCallbacks) {
//                        c.run();
//                    }
//                    connectionCallbacks = null;
//                }
//                isConnecting = false;
//                Log.d("signalR", "Connected");
//            }
//
//            @Override
//            public void onDisconnected() {
//                isConnecting = false;
//                Log.d("signalR", "disconnected");
//            }
//
//            @Override
//            public void onMessage(HubMessage message) {
//                Log.d("signalR hubMessage", "event: " + message.getTarget() + ", row message:" + message);
//                switch (message.getTarget()) {
//
//                    case MessengerEvent.RECEIVE_MESSAGE:
//                        onReceiveMessage(message);
//                        break;
//
//                    case MessengerEvent.TYPING:
//                        onTyping(message);
//                        break;
//
//                    case MessengerEvent.VIEW_MESSAGE:
//                        onViewMessage(message);
//                        break;
//                    case MessengerEvent.UPDATE_CONVERSATION:
//                    case MessengerEvent.UPDATE_PARTICIPANT:
//                    case MessengerEvent.REMOVE_PARTICIPANT:
//                        onServiceMessage(message, message.getTarget());
//                        break;
//                    case MessengerEvent.SERVER_TIME:
//                        updateServerTime(message);
//                        break;
//                    case MessengerEvent.ACCEPT_CONVERSATION:
//                        onAccept(message);
//                        break;
//                    case MessengerEvent.DECLINE_CONVERSATION:
//                        onDecline(message);
//                        break;
//                    case MessengerEvent.BLOCK:
//                        onBlock(message);
//                        break;
//
//                }
//                if (shouldDisconnect) {
//                    shouldDisconnect = false;
//                    disconnect();
//                }
//            }
//
//            @Override
//            public void onError(Exception exception) {
//                isConnecting = false;
//            }
//        });
//        connection.connect();
//    }
//
//    private void onDecline(HubMessage message) {
//        String conversationId = message.getArguments()[0].getAsString();
//        if (conversationId == null) return;
//        discIO.execute(() -> {
//            appDatabase.messengerDao().deleteConversation(conversationId);
//            appDatabase.messengerDao().cleanConversationMessages(conversationId);
//            ChatListener listener = chatListeners.get(conversationId);
//            if (listener != null) {
//                mainExecutor.execute(() ->
//                        listener.onConversationRemove(conversationId));
//            }
//        });
//
//    }
//
//    private void onBlock(HubMessage message) {
//        String conversationId = message.getArguments()[0].getAsString();
//        if (conversationId == null) return;
//        discIO.execute(() -> {
//            Conversation conversation = appDatabase.messengerDao().loadConversationAsyc(conversationId);
//            if (conversation == null || conversation.isGroup()) return;
//            conversation.setBlocked(true);
//            appDatabase.messengerDao().insertConversation(conversation);
//        });
//    }
//
//    private void onAccept(HubMessage message) {
//        String conversationId = message.getArguments()[0].getAsString();
//        if (conversationId == null) return;
//        discIO.execute(() -> {
//            Conversation conversation = appDatabase.messengerDao().loadConversationAsyc(conversationId);
//            conversation.setParticipantStatus(getUserId(), Participant.STATUS_APPROVED);
//            appDatabase.messengerDao().insertConversation(conversation);
//        });
//    }
//
//
//    public void disconnect() {
//
//        if (!isConnected()) {
//            return;
//        }
//
//        connection.disconnect();
//    }
//
//    public void onLanguageChange(boolean hasConnection) {
//        accessToken = null;
//        if (hasConnection) {
//            discIO.execute(() -> appDatabase.messengerDao().cleanServiceMessages());
//        }
//    }
//
//    //__________________message_events______________________________________________________________ /Sch
//
//    private void onReceiveMessage(HubMessage hubMessage) {
//
////        addExternalMessage(userMessage, false);
//        Map<String, String> data = new HashMap<>();
//        data.put(NotificationManager.DATA_ACTION, NotificationManager.ACTION_SEND_MESSAGE);
//        data.put("message", hubMessage.getArguments()[0].getAsString());
//        handleMessengerNotification(data);
//        Message userMessage = new Gson().fromJson(hubMessage.getArguments()[0].getAsString(), Message.class);
//        Log.d("Socket message", "localId: " + userMessage.getLocalId() + "realId: " + userMessage.getRealId());
//
//        ChatListener listener = chatListeners.get(userMessage.getConversationId());
//
//        if (listener != null) {
//            mainExecutor.execute(() ->
//                    listener.onNewMessage(userMessage));
//        }
//
//    }
//
//    private void onTyping(HubMessage hubMessage) {
//        JsonElement[] args = hubMessage.getArguments();
//
//        mainExecutor.execute(() ->
//                typingManager.handleUpdate(args[0].getAsString(), args[1].getAsInt(), args[2].getAsBoolean()));
//
//    }
//
//    private void onViewMessage(HubMessage hubMessage) {
//        Message message = new Gson().fromJson(hubMessage.getArguments()[0].getAsString(), Message.class);
//        discIO.execute(() -> {
//            Conversation conversation = appDatabase.messengerDao().loadConversationAsyc(message.getConversationId());
//            if (conversation == null) return;
//            Participant user = conversation.getUser(message.getUserId());
//            if (user == null) return;
//            if (user.getLastSeenMessageId() == null || user.getLastSeenMessageId().compareTo(message.getDetails().ViewedMessageId) < 0) {
//                // if viewed message newer than last seen message, update lastseenmessageId
//                user.setLastSeenMessageId(message.getDetails().ViewedMessageId);
//                appDatabase.messengerDao().insertConversation(conversation);
//            }
//        });
//        Log.d("onViewMessage", hubMessage.toString());
//    }
//
//    private void onServiceMessage(HubMessage hubMessage, String type) {
//        Message userMessage = new Gson().fromJson(hubMessage.getArguments()[0].getAsString(), Message.class);
//        discIO.execute(() -> {
//            boolean conversationUpdatedLocally = false;
//
//            switch (type) {
//                case MessengerEvent.UPDATE_CONVERSATION:
//                    conversationUpdatedLocally = userMessage.getUserId() == userManager.getId();
//                    break;
//                case MessengerEvent.UPDATE_PARTICIPANT:
//                    break;
//                case MessengerEvent.REMOVE_PARTICIPANT:
//                    if (userMessage.getDetails().RemovedUserId == userManager.getId()) return;
//                    break;
//            }
//            addExternalMessage(userMessage, true);
//            if (!conversationUpdatedLocally) {
//                getConversation(userMessage.getConversationId(), null);
//            }
//
//            ChatListener listener = chatListeners.get(userMessage.getConversationId());
//            if (listener != null) {
//                mainExecutor.execute(() ->
//                        listener.onNewMessage(userMessage));
//            }
//        });
//    }
//
//    // invoke methods /////////////////////////////////////////////////////////////////////
//
//
//    private Message
//    newMessage(String text, String conversationID) {
//        Message myMessage = new Message();
//        myMessage.setText(text);
//        myMessage.setDate(Calendar.getInstance().getTime());
//        myMessage.setConversationId(conversationID);
//        myMessage.setUserId(userManager.getId());
//        ObjectId id = new ObjectId(getServerTime());
//        myMessage.setLocalId(id.toHexString());
//        myMessage.setInternal(true);
//        return myMessage;
//    }
//
//    public void sendMessage(String messageText, String conversationId) {
//        Message newMessage = newMessage(messageText, conversationId);
//        addInternalMessage(newMessage);
//        invoke(MessengerEvent.SEND_MESSAGE, conversationId, messageText, newMessage.getLocalId());
//    }
//
//    private boolean shouldDisconnect;
//
//    public void sendMessageAndDisconnect(String messageText, String conversationId) { // if you are not in messenger
//        sendMessage(messageText, conversationId);
//        shouldDisconnect = true;
//    }
//
//    public void userTyping(int userID, String conversationId, boolean typing) {
//        invoke(MessengerEvent.TYPE_MESSAGE, conversationId, typing);
//    }
//
//    private void invoke(String event, Object... params) {
//        if (!isConnected()) {
//            authAndConnect(() -> invoke(event, params));
//            return;
//        }
//
//        connection.invoke(event, params);
//    }
//
//    private void authAndConnect(Runnable runnable) {
//        authenticate(new mCallback<Void>() {
//            @Override
//            public void onResponse(Void response) {
//                connect(() -> {
//                    if (runnable != null) {
//                        runnable.run();
//                    }
//                });
//            }
//
//            @Override
//            public void onFailure() {
//            }
//        });
//    }
//
//
//    // rest api methods /////////////////////////////////////////////////////////////////////
//
//    public Response<AuthResult> syncronAuth() throws IOException {
//        return api.authenticate(userManager.getId()).execute();
//    }
//
//    public void authenticate(mCallback<Void> mCallback) { // authorize
//
//        webService.request(AuthResult.class, "GetMessengerAccessToken", null, response -> {
//            if (!response.isSuccessful()) {
//                if (mCallback != null) {
//                    mCallback.onFailure();
//                }
//                return;
//            }
//
//            accessToken = response.getAccessToken();
//            if (mCallback != null) {
//                mCallback.onResponse(null);
//            }
//        });
//
//        webService.request(SettingsResult.class, WebService.GET_SETTINGS, null, response -> {
//            if (response.isSuccessful()) {
//                pushEnabled = response.getSettings().getSetting(PushNotificationsFragment.KEY_PUSH_MESSENGER);
//            }
//        });
//
//    }
//
//    public void getConversations(int startIndex, int count, boolean isAccepted, final mCallback<List<Conversation>> mCallback) {
//        api.getConversations(startIndex, count, isAccepted)
//                .enqueue(new Callback<List<Conversation>>() {
//                    @Override
//                    public void onResponse(Call<List<Conversation>> call, Response<List<Conversation>> response) {
//
//                        if (!response.isSuccessful()) {
//                            mCallback.onFailure();
//                            return;
//                        }
//
//                        List<Conversation> conversations = response.body();
//                        if (conversations != null) {
//                            for (Conversation conversation : conversations) {
//                                conversation.setConversationStatus(isAccepted ? Participant.STATUS_APPROVED : Participant.STATUS_PENDING);
//                            }
//                        }
//
//                        mCallback.onResponse(conversations);
//                    }
//
//                    @Override
//                    public void onFailure(Call<List<Conversation>> call, Throwable t) {
//                        mCallback.onFailure();
//                    }
//                });
//    }
//
//    private Call lastRequestCall;
//
//    public void searchProfiles(String query, int[] selectedUsers, mCallback<List<Profile>> mCallback) {
//        if (lastRequestCall != null && !lastRequestCall.isCanceled()) {
//            lastRequestCall.cancel();
//        }
//        lastRequestCall = api.searchUsers(query, 0, 20, selectedUsers);
//        lastRequestCall.enqueue(new Callback<List<Profile>>() {
//            @Override
//            public void onResponse(Call<List<Profile>> call, Response<List<Profile>> response) {
//
//                if (!response.isSuccessful()) {
//                    mCallback.onFailure();
//                    return;
//                }
//
//                mCallback.onResponse(response.body());
//            }
//
//            @Override
//            public void onFailure(Call<List<Profile>> call, Throwable t) {
//                mCallback.onFailure();
//            }
//        });
//
////        emit(EVENT_SEARCH_USERS, ParamMap.create().add("query", query), ack);
//    }
//
//    public void findConversation(int[] participantIds, mCallback<Conversation> mCallback) {
//
//        if (lastRequestCall != null && !lastRequestCall.isCanceled()) {
//            lastRequestCall.cancel();
//        }
//        lastRequestCall = api.findConversation(participantIds);
//        lastRequestCall.enqueue(new Callback<Conversation>() {
//            @Override
//            public void onResponse(Call<Conversation> call, Response<Conversation> response) {
//
//                if (!response.isSuccessful()) {
//                    mCallback.onResponse(null);
//                    return;
//                }
//
//                mCallback.onResponse(response.body());
//            }
//
//            @Override
//            public void onFailure(Call<Conversation> call, Throwable t) {
//                mCallback.onFailure();
//            }
//        });
//    }
//
//    public void getMessages(int startIndex, int count, String conversationId, ErrorCodeCallback<List<Message>> mCallback) {
//
//        api
//                .getMessages(conversationId, startIndex, count)
//                .enqueue(new Callback<List<Message>>() {
//                    @Override
//                    public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
//
//                        if (!response.isSuccessful()) {
//                            mCallback.onError(response.code());
//                            return;
//                        }
//
//                        Log.d("getMessages message", response.toString());
//                        mCallback.onResponse(response.body());
//                    }
//
//                    @Override
//                    public void onFailure(Call<List<Message>> call, Throwable t) {
//                        mCallback.onFailure();
//                    }
//                });
//    }
//
//    public void createGroupConversation(String message, int[] participantIds, String name, mCallback<Conversation> mCallback) {
//
//        api
//                .createConversation(message, participantIds, name)
//                .enqueue(new Callback<Conversation>() {
//                    @Override
//                    public void onResponse(Call<Conversation> call, Response<Conversation> response) {
//
//                        if (!response.isSuccessful()) {
//                            mCallback.onFailure();
//                            return;
//                        }
//
//                        mCallback.onResponse(response.body());
//                    }
//
//                    @Override
//                    public void onFailure(Call<Conversation> call, Throwable t) {
//                        mCallback.onFailure();
//                    }
//                });
//    }
//
//    public void deleteConversation(String id, mCallback<Void> mCallback) {
//        api.deleteConversation(id)
//                .enqueue(new Callback<Response<Void>>() {
//                    @Override
//                    public void onResponse(Call<Response<Void>> call, Response<Response<Void>> response) {
//                        if (!response.isSuccessful()) {
//                            mCallback.onFailure();
//                            return;
//                        }
//
//                        mCallback.onResponse(null);
//                    }
//
//                    @Override
//                    public void onFailure(Call<Response<Void>> call, Throwable t) {
//                        mCallback.onFailure();
//                    }
//                });
//    }
//
//    public void updateParticipantStatus(String conversationId, int userId, int status, mCallback<Void> callback) {
//        api.updateParticipantStatus(conversationId, userId, status)
//                .enqueue(new Callback<Response<Void>>() {
//                    @Override
//                    public void onResponse(Call<Response<Void>> call, Response<Response<Void>> response) {
//                        if (!response.isSuccessful()) {
//                            callback.onFailure();
//                            return;
//                        }
//
//                        callback.onResponse(null);
//
//                    }
//
//                    @Override
//                    public void onFailure(Call<Response<Void>> call, Throwable t) {
//                        if (callback != null) {
//                            callback.onFailure();
//                        }
//                    }
//                });
//    }
//
//    private boolean notificationCountUpdating;
//
//    public void getNotificationCount(mCallback<Integer> callback) {
//        if (notificationCountUpdating) return;
//        notificationCountUpdating = true;
//        api.getNotificationCount().enqueue(new Callback<Integer>() {
//            @Override
//            public void onResponse(Call<Integer> call, Response<Integer> response) {
//                notificationCountUpdating = false;
//                if (response.isSuccessful() && callback != null) {
//                    callback.onResponse(response.body());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Integer> call, Throwable t) {
//                notificationCountUpdating = false;
//            }
//        });
//
//    }
//
//
//    //________________________some_db_magic_________________________________________________________/Sch
//
//    public void addExternalMessage(final Message message, boolean withoutPush) {
//        Log.d("addingExternalMessage", message.getText());
////        discIO.execute(() -> {
//
////
////            Log.d("addingExternalMessage", "conversation got");
//
//        if (message.getLocalId() == null) { // the warning lies :)
//            message.setLocalId(message.getRealId());
//        }
//
////            if (conversation != null) {
//        messageToDB(message, () -> {
//            if (!withoutPush) {
//                notifyOrConnect(message);
//            }
//        });
////            } else {
//
////            }
////        });
//    }
//
//    private void messageToDB(Message message, Runnable callback) {
//        discIO.execute(() -> {
//            appDatabase.messengerDao().insertMessage(message);
//            Log.d("messageToDB", "localId: " + message.getLocalId() + ", realId: " + message.getRealId() + ", text: " + message.getText());
//            Conversation conversation = appDatabase.messengerDao().loadConversationAsyc(message.getConversationId());
//            if (conversation != null) {
//                conversation.setLastMessage(message);
//                conversation.setLastActionDate(message.getDate());
//                final Participant sender = conversation.getParticipant(message.getUserId());
//                if (sender != null) {
//                    sender.setLastSeenMessageId(message.getId());
//                }
//                appDatabase.messengerDao().insertConversation(conversation);
//                Log.d("addingExternalMessage", "conversation inserted");
//                if (callback != null) {
//                    callback.run();
//                }
//            } else {
//                getConversation(message.getConversationId(), new mCallback<Conversation>() {
//                    @Override
//                    public void onResponse(final Conversation response) {
//                        discIO.execute(() -> {
//                            appDatabase.messengerDao().insertConversation(response);
//                            if (callback != null) {
//                                callback.run();
//                            }
//                        });
//                    }
//
//                    @Override
//                    public void onFailure() {
//                    }
//                });
//            }
//        });
//    }
//
//    /**
//     * @param data
//     * @return true if handled,
//     */
//    public void handelConversationInvite(Map<String, String> data) {
//        String conversationId = data.get("actionId");
//        if (state != null && state.component.isAssignableFrom(ConversationListFragment.class)) {
//            getConversation(conversationId, new mCallback<Conversation>() {
//                @Override
//                public void onResponse(Conversation response) {
//                    connect(null);
//                }
//
//                @Override
//                public void onFailure() {
//                }
//            });
////            return true;
//        } else {
//            data.put(NotificationManager.DATA_REFERENCE_ID, "0");
//            notificationManager.enqueueNotification(data);
//            notificationManager.updateCount();
////            return false;
//        }
//    }
//
//    /**
//     * Call this method when new message comes via socket or push
//     * here we decide show notification or not
//     * if the conversation visible for user, we don't notify, but reestablish socket connection
//     *
//     * @param message service message or user message
//     */
//    private void notifyOrConnect(Message message) {
//        String conversationId = message.getConversationId();
//        Conversation conversation = appDatabase.messengerDao().loadConversationAsyc(message.getConversationId());
//        if (!pushEnabled
//                || message.getUserId() == userManager.getId() // your message
//                || conversation.isPending(userManager.getId())
//                || (state != null && state.component.isAssignableFrom(ConversationListFragment.class) && (int) state.info == ConversationListFragment.MODE_INBOX) // you are in conversations inbox page
//                || (state != null && state.component.isAssignableFrom(MessagingFragment.class) && conversationId.equals((String) state.info)) // this conversation is open
//                ) {
//            connect(null);
//            Log.d(MESSENGER_PUSH_LOG_TAG, "received message: " + message.getText() + " no push needed");
//            return;
//        }
//        Participant sender = conversation.getParticipant(message.getUserId());
//        if (sender == null) return;
//
//        String lastSeenMessageId = conversation.getParticipant(userManager.getId()).getLastSeenMessageId();
//        List<Message> unseenMessages = appDatabase.messengerDao().getMessagesFrom(userManager.getId(), conversationId, lastSeenMessageId);
//
//        int size = unseenMessages.size();
//        if (size == 0 || !unseenMessages.get(size - 1).getId().equals(message.getId())) {
//            unseenMessages.add(message);
//        }
//
//
////        Bundle data = new Bundle();
////        data.putString("action", "send message");
////        data.putString("actionId", message.getConversationId());
////        data.putString("title", conversation.getDisplayName(userManager.getId(), context));
////        data.putString("text", text);
////        data.putString("avatarUrl", sender.getAvatarUrl());
////        data.putString("message", gson.toJson(message));
//
//        User user = new User();
//        user.setName(sender.getUserName());
//        user.setId(sender.getUserId());
//        user.setAvatarUrl(sender.getAvatarUrl());
//
//        Log.d(MESSENGER_PUSH_LOG_TAG, "sending notification for message: " + message.getText());
//        mainExecutor.execute(() -> {
//            notificationManager.sendChatNotification(unseenMessages, user, conversation);
//        });
//
//    }
//
//    private void addInternalMessage(final Message message) {
////            final Conversation conversation = appDatabase.messengerDao().loadConversationAsyc(message.getConversationId());
//        messageToDB(message, null);
//    }
//
//    public void searchParticipants(String query, mCallback<List<Participant>> mCallback) {
//        if (lastRequestCall != null && !lastRequestCall.isCanceled()) {
//            lastRequestCall.cancel();
//        }
//        lastRequestCall = api.searchParticipants(query, 0, 20);
//
//        lastRequestCall.enqueue(new Callback<List<Participant>>() {
//            @Override
//            public void onResponse(Call<List<Participant>> call, Response<List<Participant>> response) {
//
//                if (!response.isSuccessful()) {
//                    return;
//                }
//
//                mCallback.onResponse(response.body());
//            }
//
//            @Override
//            public void onFailure(Call<List<Participant>> call, Throwable t) {
//                if (call.isCanceled()) return;
//                mCallback.onFailure();
//            }
//        });
//    }
//
//    public void removeParticipant(String conversationId, int participantId, mCallback<
//            Boolean> mCallback) {
//
//        api
//                .deleteParticipant(conversationId, participantId)
//                .enqueue(new Callback<Response<Void>>() {
//                    @Override
//                    public void onResponse(Call<Response<Void>> call, Response<Response<Void>> response) {
//
//                        if (!response.isSuccessful()) {
//                            mCallback.onResponse(false);
//                            return;
//                        }
//
//                        mCallback.onResponse(true);
//                    }
//
//                    @Override
//                    public void onFailure(Call<Response<Void>> call, Throwable t) {
//                        mCallback.onResponse(false);
//                    }
//                });
//    }
//
//
//    public void getConversation(String conversationId, mCallback<Conversation> mCallback) {
//
//        api
//                .getConversation(conversationId)
//                .enqueue(new Callback<Conversation>() {
//                    @Override
//                    public void onResponse(Call<Conversation> call, @NonNull Response<Conversation> response) {
//
//                        if (!response.isSuccessful()) {
//                            return;
//                        }
//                        Conversation conversation = response.body();
//                        if (conversation != null) {
//                            conversation.setConversationStatus(conversation.isPending(getUserId()) ? Participant.STATUS_PENDING : Participant.STATUS_APPROVED);
//                        }
//
//                        discIO.execute(() -> {
//                            appDatabase.messengerDao().insertConversation(conversation);
//                        });
//
//                        if (mCallback != null)
//                            mCallback.onResponse(conversation);
//                    }
//
//                    @Override
//                    public void onFailure(Call<Conversation> call, Throwable t) {
//                        mCallback.onFailure();
//                    }
//                });
//    }
////
////    public void findConversation(int participantId, mCallback<Conversation> mCallback) {
////        findConversation(new int[]{participantId}, mCallback);
////    }
//
//    public void seen(String conversationId, @NonNull Message message) {
//
//        if (message.getUserId() == userManager.getId()) {
//            return;
//        }
//
//        api
//                .seen(conversationId, message.getId())
//                .enqueue(new Callback<Response<Void>>() {
//                    @Override
//                    public void onResponse(Call<Response<Void>> call, Response<Response<Void>> response) {
//                        discIO.execute(new Runnable() {
//                            @Override
//                            public void run() {
//                                Conversation conversation = appDatabase.messengerDao().loadConversationAsyc(conversationId);
//                                if (conversation != null) {
//                                    conversation.getParticipant(userManager.getId()).setLastSeenMessageId(message.getId());
//                                    appDatabase.messengerDao().insertConversation(conversation);
//                                }
//                            }
//                        });
//                    }
//
//                    @Override
//                    public void onFailure(Call<Response<Void>> call, Throwable t) {
//
//                    }
//                });
//    }
//
//    public void seenEmptyConversation(String conversationId) {
//
//        api
//                .seen(conversationId)
//                .enqueue(new Callback<Response<Void>>() {
//                    @Override
//                    public void onResponse(Call<Response<Void>> call, Response<Response<Void>> response) {
//                        discIO.execute(new Runnable() {
//                            @Override
//                            public void run() {
//                                Conversation conversation = appDatabase.messengerDao().loadConversationAsyc(conversationId);
//                                if (conversation != null) {
//                                    Participant participant = conversation.getParticipant(userManager.getId());
//                                    if (participant.getLastSeenMessageId() == null) {
//                                        participant.setLastSeenMessageId("00000000000000000000000");
//                                        appDatabase.messengerDao().insertConversation(conversation);
//                                    }
//                                }
//                            }
//                        });
//                    }
//
//                    @Override
//                    public void onFailure(Call<Response<Void>> call, Throwable t) {
//
//                    }
//                });
//    }
//
//    public void updateConversationParticipants(String conversationId, String newName,
//                                               int[] participantIds, mCallback<Conversation> mCallback) {
//        api.addParticipants(conversationId, participantIds, newName).enqueue(new Callback<Conversation>() {
//            @Override
//            public void onResponse(Call<Conversation> call, Response<Conversation> response) {
//                if (response.isSuccessful() && mCallback != null) {
//                    mCallback.onResponse(response.body());
//                } else {
//                    mCallback.onFailure();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Conversation> call, Throwable t) {
//                mCallback.onFailure();
//            }
//        });
////        emit(EVENT_UPDATE_CONVERSATION, params, ack);
//    }
//
//    public void renameConversation(String conversationId, String
//            name, mCallback<Response<Void>> mCallback) {
//        api.renameConversation(conversationId, name).enqueue(new Callback<Response<Void>>() {
//            @Override
//            public void onResponse(Call<Response<Void>> call, Response<Response<Void>> response) {
//                if (response.isSuccessful() && mCallback != null) {
//                    mCallback.onResponse(response.body());
//                    discIO.execute(() -> {
//                        Conversation conversation = appDatabase.messengerDao().loadConversationAsyc(conversationId);
//                        conversation.setName(name);
//                        appDatabase.messengerDao().insertConversation(conversation);
//                    });
//
//                } else {
//                    mCallback.onFailure();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Response<Void>> call, Throwable t) {
//                mCallback.onFailure();
//            }
//        });
//    }
//
//    public void revokeCache() {
//        discIO.execute(() -> {
//            appDatabase.messengerDao().deleteConversations();
//            appDatabase.messengerDao().deleteMessages();
//            accessToken = null;
//        });
//    }
//
//    public void setChatListener(String conversationId, @Nullable ChatListener listener) {
//        if (listener == null) {
//            chatListeners.remove(conversationId);
//        } else {
//            chatListeners.put(conversationId, listener);
//        }
//    }
//
//    public boolean isConnected() {
//        try { // there is crash in lib
//            return connection != null && connection.isConnected();
//        } catch (NullPointerException e) {
//            return false;
//        }
//    }
//
//    public int getUserId() {
//        return userManager.getId();
//    }
//
//    public static String getToken() {
//        return accessToken;
//    }
//
//    public static void setAccessToken(String accessToken) {
//        MessengerService.accessToken = accessToken;
//    }
//
//    public void deleteMessage(String id) {
//        discIO.execute(() -> appDatabase.messengerDao().deleteMessage(id));
//    }
//
//    public boolean hasAccessToken() {
//        return accessToken != null;
//    }
//
//    public WebService getWebService() {
//        return webService;
//    }
//
//    private Date serverTime;
//    private long serverTimeElapsed;
//
//    private void updateServerTime(HubMessage message) {
//        serverTime = UtcDateTypeAdapter.deserializeToDate(message.getArguments()[0]);
//        serverTimeElapsed = SystemClock.elapsedRealtime();
//    }
//
//    public Date getServerTime() {
//        if (serverTime == null) return new Date();
//
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(serverTime);
//        calendar.add(Calendar.MILLISECOND, (int) (SystemClock.elapsedRealtime() - serverTimeElapsed) + 1000);
//        return calendar.getTime();
//    }
//
//    public List<Integer> getTypingUsers(String conversationId) {
//        return typingManager.getTypingUsers(conversationId);
//    }
//
//    public boolean handleMessengerNotification(Map<String, String> data) {
//        String action = data.get(NotificationManager.DATA_ACTION);
//
//        if (action != null && action.equals(NotificationManager.ACTION_SEND_MESSAGE)) {
//            Log.d(MESSENGER_PUSH_LOG_TAG, "received message: " + data.get("message"));
//            Gson gson = new Gson();
//            Message message = gson.fromJson(data.get("message"), Message.class);
//            boolean handled = handleNotificationIfBlocked(data);
//            addExternalMessage(message, handled);
//            return true;
//        } else if (action != null && action.equals(NotificationManager.ACTION_NEW_CONVERSATION)) {
//            if (!handleNotificationIfBlocked(data)) {
//                handelConversationInvite(data);
//            }
//            return true;
//        }
//        return false;
//    }
//
//    private boolean handleNotificationIfBlocked(Map<String, String> data) {
//        if (notificationManager.isBlockedByComponent()) { // block notification
//            String conversationId = data.get("actionId");
//            notificationQueue.put(conversationId, data);
//            return true;
//        }
//        return false;
//    }
//
//    public void userBlocked(boolean isBlocked, int id) {
//        discIO.execute(() -> {
//            List<Conversation> conversations = appDatabase.messengerDao().loadConversationsWithUser(id);
//            if (conversations == null) return;
//            for (Conversation c : conversations) {
//                if (c.isGroup()) {
//                    c.getParticipant(id).setBlocked(isBlocked);
//                } else {
//                    c.setBlocked(isBlocked);
//                }
//            }
//            appDatabase.messengerDao().insertConversations(conversations);
//        });
//    }
//
//    public interface mCallback<T> {
//        void onResponse(T response);
//
//        void onFailure();
//    }
//
//    public interface ErrorCodeCallback<T> {
//        void onResponse(T response);
//
//        void onFailure();
//
//        void onError(int code);
//    }
//
//
//    public interface ChatListener {
//        void onNewMessage(Message message);
//
//        void onTyping(int userId, boolean isTyping);
//
//        void onConversationRemove(String id);
//    }
//
//    public interface ComponentProvider {
//        Class<?> getComponent();
//    }
//
//    /**
//     * this class contains info about messenger fragment and its state
//     * we use this currently to decide weather notification is necessary
//     * you can modify or add fields if you need to know about other details
//     * if @variable state is null, it means nothing interesting for MessengerService is going on :)
//     */
//    private class MessengerState {
//        Class<?> component;
//        Object info;
//    }
//
//    public void setState(@NonNull Class<?> component, Object info) {
//        if (state == null) {
//            state = new MessengerState();
//        }
//        state.component = component;
//        state.info = info;
//        onNewState();
//    }
//
//    private void onNewState() {
//        if (state != null && state.component.isAssignableFrom(MessagingFragment.class)) {
//            String conversationId = (String) state.info;
//            clearConversationNotification(conversationId);
//        }
//    }
//
//    public void checkNotificationQueue() {
//        if (!notificationQueue.isEmpty()) {
//            new Handler(Looper.getMainLooper())
//                    .postDelayed(() -> {
//                        Iterator<String> iterator = notificationQueue.keySet().iterator();
//                        if (iterator.hasNext()) {
//                            String id = iterator.next();
//                            handleMessengerNotification(notificationQueue.remove(id));
//                            checkNotificationQueue();
//                        }
//                    }, 1000);
//
//        }
//    }
//
//    public Context getContext() {
//        return context;
//    }
//
//    public void clearConversationNotification(String conversationId) {
//        notificationManager.clearByTagAdnId(NotificationManager.MESSENGER_NOTIFICATION_TAG, conversationId.hashCode());
//    }
//
//    public void clearState() {
//        state = null;
////        onNewState();
//    }
//
//    public void setPushEnabled(boolean pushEnabled) {
//        this.pushEnabled = pushEnabled;
//    }
//
//    private class MessengerEvent {
//
//        //Warning: do not edit or change String values. They have concrete event names. /Sch
//
//        //invoke
//        static final String SEND_MESSAGE = "SendMessage";
//        static final String TYPE_MESSAGE = "TypeMessage";
//
//        //subscribe
//        static final String RECEIVE_MESSAGE = "send message";
//        static final String TYPING = "typing";
//        static final String UPDATE_CONVERSATION = "update conversation";
//        static final String UPDATE_PARTICIPANT = "update participant";
//        static final String REMOVE_PARTICIPANT = "remove participant";
//        static final String BLOCK = "user blocked";
//        static final String VIEW_MESSAGE = "view message";
//        static final String ACCEPT_CONVERSATION = "accept conversation";
//        static final String DECLINE_CONVERSATION = "decline conversation";
//        static final String SERVER_TIME = "server time";
//    }
//}