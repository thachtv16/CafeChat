package thachtv.cafechat.model;

/**
 * Created by Thinkpad on 11/09/2017.
 */

public class ChatFriend {

    private String userNameChatFriend;
    private String messageChatFriend;
    private String linkAvatarChatFriend;

    public ChatFriend() {
    }

    public ChatFriend(String userNameChatFriend, String linkAvatarChatFriend) {
        this.userNameChatFriend = userNameChatFriend;
        this.linkAvatarChatFriend = linkAvatarChatFriend;
    }

    public ChatFriend(String userNameChatFriend, String messageChatFriend, String linkAvatarChatFriend) {
        this.userNameChatFriend = userNameChatFriend;
        this.messageChatFriend = messageChatFriend;
        this.linkAvatarChatFriend = linkAvatarChatFriend;
    }

    public String getUserNameChatFriend() {
        return userNameChatFriend;
    }

    public void setUserNameChatFriend(String userNameChatFriend) {
        this.userNameChatFriend = userNameChatFriend;
    }

    public String getMessageChatFriend() {
        return messageChatFriend;
    }

    public void setMessageChatFriend(String messageChatFriend) {
        this.messageChatFriend = messageChatFriend;
    }

    public String getLinkAvatarChatFriend() {
        return linkAvatarChatFriend;
    }

    public void setLinkAvatarChatFriend(String linkAvatarChatFriend) {
        this.linkAvatarChatFriend = linkAvatarChatFriend;
    }
}
