package thachtv.cafechat.define;

/**
 * Created by Thinkpad on 10/04/2017.
 */

public class Constant {
    public static class FirebaseDatabase {
        public static final String USERS = "users";
        public static final String EMAIL = "email";
        public static final String GENDER = "gender";
        public static final String LINK_AVATAR = "link_avatar";
        public static final String ONLINE = "online";
        public static final String PASSWORD = "password";
        public static final String PHONE = "phone";
        public static final String USER_NAME = "user_name";
        public static final String UID = "uid";

        public static final String PHOTOS_AVATAR = "photos_avatar";

        public static final String FRIEND_REQUEST = "friend_request";
        public static final String SENT_REQUEST = "sent_request";
        public static final String RECEIVED_REQUEST = "received_request";

        public static final String FRIENDS = "friends";
        public static final String CHAT = "chat";
        public static final String MESSAGES = "messages";
    }

    public static class InformationImage {
        public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 2017;
        public static final int FOLDER_IMAGE_ACTIVITY_REQUEST_CODE = 2018;
    }

    public static class SharePref {
        public static final String CAFE_CHAT_PREF = "CafeChatPref";
        public static final String UID = "uid";
        public static final String IS_LOGGED_BEFORE = "is_logged_before";
    }

    public static class SharePrefPrivacy {
        public static final String PRIVACY_PREF = "privacy_pref";
        public static final String CURRENT_UID = "current_uid";
        public static final String EMAIL_PRIVACY_PREF = "email_privacy";
        public static final String PHONE_PRIVACY_PREF = "phone_privacy";
        public static final String GENDER_PRIVACY_REF = "gender_privacy";
    }
}
