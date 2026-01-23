package garlicrot.rusherchat;

public class Message {

    public enum Type {
        LOGIN,
        CHAT,
        SYSTEM,
        WHISPER
    }

    private Type type;
    private String username;
    private String content;
    private String coloredUsername;
    private String target;
    private boolean whisper;

    public Message() {
    }

    public Message(String username, String content) {
        this(Type.CHAT, username, content, null, null, false);
    }

    public Message(String username, String content, String coloredUsername) {
        this(Type.CHAT, username, content, coloredUsername, null, false);
    }

    public Message(String username,
                   String content,
                   String coloredUsername,
                   String target,
                   boolean whisper) {
        this(Type.CHAT, username, content, coloredUsername, target, whisper);
    }

    public Message(Type type, String username, String content) {
        this(type, username, content, null, null, false);
    }

    public Message(Type type,
                   String username,
                   String content,
                   String coloredUsername,
                   String target,
                   boolean whisper) {
        this.type = (type != null ? type : Type.CHAT);
        this.username = username;
        this.content = content;
        this.coloredUsername = coloredUsername;
        this.target = target;
        this.whisper = whisper;
    }

    public Type getType() {
        return type != null ? type : Type.CHAT;
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

    public String getTarget() {
        return target;
    }

    public String getTargetColored() {
        return target;
    }

    public boolean isWhisper() {
        return whisper;
    }

    @Override
    public String toString() {
        return "[" + getColoredUsername() + "] " + content;
    }
}
