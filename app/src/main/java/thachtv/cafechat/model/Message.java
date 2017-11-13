package thachtv.cafechat.model;

public class Message {

    private String message;
    private String type;
    private long time;
    private boolean seen;
    private String linkAvatar;
    private String from;
    private String to;

    public Message() {
    }

    public Message(String message, String type, long time, boolean seen, String linkAvatar, String from, String to) {
        this.message = message;
        this.type = type;
        this.time = time;
        this.seen = seen;
        this.linkAvatar = linkAvatar;
        this.from = from;
        this.to = to;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public String getLinkAvatar() {
        return linkAvatar;
    }

    public void setLinkAvatar(String linkAvatar) {
        this.linkAvatar = linkAvatar;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
