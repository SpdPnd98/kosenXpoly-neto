package com.sp.neto;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ChatMessages {

    private String messageText;
    private String messageUser;
    private String messageTime;

    public ChatMessages(){}

    public String getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(String messageTime) {
        this.messageTime = messageTime;
    }

    public ChatMessages(String messageText, String messageUser){
        this.messageText = messageText;
        this.messageUser = messageUser;
        //Initialize current time
        this.messageTime = SimpleDateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }
}
