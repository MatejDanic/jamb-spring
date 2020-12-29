package matej.api.controllers;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.RestController;

import matej.api.services.SocketService;
import matej.api.services.UserService;
import matej.components.JwtUtils;
import matej.models.payload.request.MessageRequest;
import matej.models.payload.response.MessageResponse;
import matej.models.types.MessageType;

@RestController
public class SocketController {

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    UserService userService;

    @Autowired
    SocketService socketService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/greeting")
    @SendTo("/topic/greetings")
    public MessageResponse greeting(MessageRequest message, Principal principal) throws Exception {
        if (message.getMessage().equals("Hello") && message.getToken() != null && jwtUtils.getUsernameFromToken(message.getToken()).equals(message.getSender())) {
            socketService.addUUID(message.getSender(), principal.getName());
        }
        return new MessageResponse(message.getMessage() + ", " + message.getSender() + "!", MessageType.GREETING);
    }

    @MessageMapping("/text")
    @SendTo("/topic/everyone")
    public MessageResponse message(MessageRequest message) throws Exception {
        MessageResponse response = null;
        if (message.getToken() != null && jwtUtils.getUsernameFromToken(message.getToken()).equals(message.getSender())) {
            response = new MessageResponse(message.getMessage(), message.getSender(), MessageType.CHAT);
        }
        return response;
    }

    @MessageMapping("/challenge")
    @SendToUser("/topic/challenge")
    public void sendSpecific(@Payload MessageRequest message, @Header("simpSessionId") String sessionId)
            throws Exception {
        MessageResponse response = null;
        if (message.getToken() != null && jwtUtils.getUsernameFromToken(message.getToken()).equals(message.getSender())) {
            response = new MessageResponse(message.getMessage(), message.getSender(), MessageType.CHALLENGE, message.getContent());
            simpMessagingTemplate.convertAndSendToUser(socketService.getUUIDFromUsername(message.getReciever()), "/topic/challenge", response);
        }
    }
}