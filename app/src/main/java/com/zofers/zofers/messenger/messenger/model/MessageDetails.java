package com.zofers.zofers.messenger.messenger.model;

/**
 * Created by SoloLearn PC2 on 7/9/2018.
 */

public class MessageDetails {
    // update participant
    public int[] NewAcceptedParticipants;
    public int[] NewPendingParticipants;
    public int[] RemovedParticipants; // someone removed you

    //update conversation
    public String NewConversationName;
    // remove participant
    public int RemovedUserId;
    //view message\
    public String ViewedMessageId;

}
