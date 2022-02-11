package com.silibrina.tecnova.commons.messenger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Locale;

import static com.silibrina.tecnova.commons.utils.Preconditions.checkNotNullCondition;
import static com.silibrina.tecnova.commons.utils.Preconditions.checkValidString;

/**
 * This is the message that goes around in our message queue service.
 * It includes two simple fields, the entry and the type of operation
 * to be executed over this entry (CREATE, UPDATE or DELETE).
 */
public class Message {
    public static final String ENTRY_ID = "entry_id";
    public static final String MESSAGE_TYPE = "type";
    public static final String PAYLOAD = "payload";

    private final MessageType type;
    private final String entryId;
    private final JsonNode payload;

    public Message(MessageType type, String id, JsonNode payload) {
        checkNotNullCondition("type can not be null", type);
        checkValidString("entry entryId must be a valid string", id);

        this.type = type;
        this.entryId = id;
        this.payload = payload;
    }

    public String getEntryId() {
        return entryId;
    }

    public MessageType getType() {
        return type;
    }

    public JsonNode getPayload() {
        return payload;
    }

    public JsonNode toJson() {
        ObjectNode body = Json.newObject();
        body.put(ENTRY_ID, entryId);
        body.put(MESSAGE_TYPE, type.toString());
        return body.set(PAYLOAD, payload);
    }

    public byte[] toByteArray() throws JsonProcessingException {
        ObjectWriter writer = new ObjectMapper().writer();
        return writer.writeValueAsBytes(toJson());
    }

    public static Message fromByteArray(byte[] body) throws IOException {
        ObjectReader reader = new ObjectMapper().reader();
        JsonNode tree = reader.readTree(new ByteArrayInputStream(body));
        return new Message(MessageType.valueOf(tree.get(MESSAGE_TYPE).asText()),
                tree.get(ENTRY_ID).asText(), tree.get(PAYLOAD));
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(),
                "%s [type: %s, entryId: %s, payload: %s]",
                this.getClass().getSimpleName(), type, entryId, payload);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message = (Message) o;

        if (type != message.type) return false;
        return entryId != null ? entryId.equals(message.entryId) : message.entryId == null;

    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (entryId != null ? entryId.hashCode() : 0);
        return result;
    }

    public enum MessageType {
        CREATE,
        DELETE,
        UPDATE
    }
}
