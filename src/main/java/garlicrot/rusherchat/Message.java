package garlicrot.rusherchat;

public class Message {
    private final String username;
    private final String content;
    private final String coloredUsername;
    private final String target;
    private final boolean whisper;

    // Required for Gson deserialization
    public Message() {
        this.username = null;
        this.content = null;
        this.coloredUsername = null;
        this.target = null;
        this.whisper = false;
    }

    public Message(String username, String content) {
        this.username = username;
        this.content = content;
        this.coloredUsername = null;
        this.target = null;
        this.whisper = false;
    }

    public Message(String username, String content, String coloredUsername) {
        this.username = username;
        this.content = content;
        this.coloredUsername = coloredUsername;
        this.target = null;
        this.whisper = false;
    }

    public Message(String username, String content, String coloredUsername, String target, boolean whisper) {
        this.username = username;
        this.content = content;
        this.coloredUsername = coloredUsername;
        this.target = target;
        this.whisper = whisper;
    }

    public String getUsername() { return username; }
    public String getContent() { return content; }
    public String getColoredUsername() { return coloredUsername != null ? coloredUsername : username; }
    public String getTarget() { return target; }
    public String getTargetColored() { return target; }
    public boolean isWhisper() { return whisper; }

    @Override
    public String toString() {
        return "[" + getColoredUsername() + "] " + content;
    }
}