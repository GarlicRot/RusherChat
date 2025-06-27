package garlicrot.rusherchat;

public class Message {
    private final String username;
    private final String content;
    private final String coloredUsername;

    // Required for Gson deserialization
    public Message() {
        this.username = null;
        this.content = null;
        this.coloredUsername = null;
    }

    public Message(String username, String content) {
        this.username = username;
        this.content = content;
        this.coloredUsername = null;
    }

    public Message(String username, String content, String coloredUsername) {
        this.username = username;
        this.content = content;
        this.coloredUsername = coloredUsername;
    }

    public String getUsername() {
        return username;
    }

    public String getContent() {
        return content;
    }

    public String getColoredUsername() {
        return coloredUsername != null ? coloredUsername : username;
    }

    @Override
    public String toString() {
        return "[" + getColoredUsername() + "] " + content;
    }
}
