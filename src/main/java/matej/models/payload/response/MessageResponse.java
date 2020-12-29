package matej.models.payload.response;

import matej.models.types.MessageType;

public class MessageResponse {

    private String username;
    private String message;
    private MessageType type;
    private Object content;

    public MessageResponse(String message) {
        this.message = message;
        this.username = "Server";
        this.type = MessageType.DEFAULT;
    }

    public MessageResponse(String message, MessageType type) {
        this.message = message;
        this.username = "Server";
        this.type = type;
    }

    public MessageResponse(String message, String username, MessageType type) {
        this.message = message;
        this.username = username;
        this.type = type;
    }

    public MessageResponse(String message, String username, MessageType type, Object content) {
        this.message = message;
        this.username = username;
        this.type = type;
        this.content = content;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    
    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }
    
}