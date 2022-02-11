package com.silibrina.tecnova.commons.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;

public abstract class JsonParseable {

    /**
     * Generates a json representation of the current object in his
     * current state.
     *
     * @return a json representation of the current state.
     */
    public JsonNode toJson() {
        return toJson(Json.newObject());
    }

    /**
     * Generates a json representation of the current object in his
     * current state.
     *
     * @param body the json body to add its content.
     *
     * @return a json representation of this object.
     */
    public abstract JsonNode toJson(ObjectNode body);
}
