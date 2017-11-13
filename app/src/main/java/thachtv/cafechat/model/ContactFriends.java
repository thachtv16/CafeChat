package thachtv.cafechat.model;

/**
 * Created by Thinkpad on 10/21/2017.
 */

public class ContactFriends {

    private String userNameContact;
    private String imageAvatarContact;
    private String uidContact;
    private String dateTimeContact;
    private String dotOnline;

    public ContactFriends() {
    }

    public ContactFriends(String userNameContact, String imageAvatarContact, String uidContact, String dateTimeContact, String dotOnline) {
        this.userNameContact = userNameContact;
        this.imageAvatarContact = imageAvatarContact;
        this.uidContact = uidContact;
        this.dateTimeContact = dateTimeContact;
        this.dotOnline = dotOnline;
    }

    public String getUserNameContact() {
        return userNameContact;
    }

    public void setUserNameContact(String userNameContact) {
        this.userNameContact = userNameContact;
    }

    public String getImageAvatarContact() {
        return imageAvatarContact;
    }

    public void setImageAvatarContact(String imageAvatarContact) {
        this.imageAvatarContact = imageAvatarContact;
    }

    public String getUidContact() {
        return uidContact;
    }

    public void setUidContact(String uidContact) {
        this.uidContact = uidContact;
    }

    public String getDateTimeContact() {
        return dateTimeContact;
    }

    public void setDateTimeContact(String dateTimeContact) {
        this.dateTimeContact = dateTimeContact;
    }

    public String getDotOnline() {
        return dotOnline;
    }

    public void setDotOnline(String dotOnline) {
        this.dotOnline = dotOnline;
    }
}
