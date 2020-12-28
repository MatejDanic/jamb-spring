package matej.api.controllers;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

import matej.payload.request.MessageRequest;
import matej.payload.response.MessageResponse;

@RestController
public class SocketController {

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public MessageResponse hello(MessageRequest message) throws Exception {
        return new MessageResponse("Bok, " + HtmlUtils.htmlEscape(message.getUsername()) + "!");
    }

    @MessageMapping("/goodbye")
    @SendTo("/topic/greetings")
    public MessageResponse goodbye(MessageRequest message) throws Exception {
        return new MessageResponse("Adio, " + HtmlUtils.htmlEscape(message.getUsername()) + "!");
    }

    @MessageMapping("/text")
    @SendTo("/topic/everyone")
    public MessageResponse message(MessageRequest message) throws Exception {
        return new MessageResponse(message.getContent(), message.getUsername());
    }

}