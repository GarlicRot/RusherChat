package garlicrot.rusherchat;

public class Message {
    private String username;
    private String content;

    public Message() {
        // Required for Gson deserialization
    }

    public Message(String username, String content) {
        this.username = username;
        this.content = content;
    }

    public String getUsername() {
        return username;
    }

    public String getContent() {
        return content;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "[" + username + "] " + content;
    }
}
