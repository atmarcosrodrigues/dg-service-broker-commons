package com.silibrina.tecnova.commons.messenger;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;
import play.libs.Json;

import java.io.IOException;

import static com.silibrina.tecnova.commons.messenger.Message.MessageType.CREATE;
import static org.junit.Assert.assertEquals;

public class MessageTests {

    @Test
    public void messageToByteArrayTest() throws IOException {
        ObjectNode payload = Json.newObject();
        payload.put("uri", "some_uri");

        Message message = new Message(CREATE, "some_id", payload);
        byte[] body = message.toByteArray();
        Message newMessage = Message.fromByteArray(body);

        assertEquals("should exactly the same message", message, newMessage);
        assertEquals("type should be the same", message.getType(), newMessage.getType());
        assertEquals("payload should be the same", message.getPayload(), newMessage.getPayload());
    }
}
