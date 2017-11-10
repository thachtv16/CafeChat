package thachtv.cafechat.model;

/**
 * Created by Thinkpad on 10/16/2017.
 */

public class Users {

    private String userName;
    private String imageAvatar;
    private String uid;

    public Users() {
    }

    public Users(String userName, String imageAvatar, String uid) {
        this.userName = userName;
        this.imageAvatar = imageAvatar;
        this.uid = uid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getImageAvatar() {
        return imageAvatar;
    }

    public void setImageAvatar(String imageAvatar) {
        this.imageAvatar = imageAvatar;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
