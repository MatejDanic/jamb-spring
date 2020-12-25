package matej.api.controllers;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

import matej.models.MessageSocket;

@RestController
public class SocketController {

    @MessageMapping("/user-all")
    @SendTo("/topic/user")
    public MessageSocket send(@Payload MessageSocket message) {
        return message;
    }
}