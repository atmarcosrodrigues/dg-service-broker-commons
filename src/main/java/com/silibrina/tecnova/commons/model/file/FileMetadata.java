package com.silibrina.tecnova.commons.model.file;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.silibrina.tecnova.commons.model.JsonParseable;
import play.libs.Json;

import java.util.*;

/**
 * Metadata of a file. This holds general information about the location,
 * type etc. The information in this file must be enough to get the file
 * from file system and access it using http or other protocol.
 * Pay attention to url, it can probably change depending on the type of
 * file system used as storage.
 */
public class FileMetadata extends JsonParseable {
    public static final String ORIGINAL_NAME = "original_name";
    public static final String NAME = "name";
    public static final String COUNT = "count";
    public static final String VERSIONS = "versions";

    private final String originalName;
    private final String name;
    private final Map<String, FileVersion> versions;

    public FileMetadata(@JsonProperty(ORIGINAL_NAME) String originalName, @JsonProperty(NAME) String name,
                        @JsonProperty(VERSIONS) Map<String, FileVersion> versions) {
        this.originalName = originalName;
        this.name = name;
        this.versions = paths(versions);
    }

    private Map<String, FileVersion> paths(Map<String, FileVersion> versions) {
        if (versions != null) {
            return versions;
        }
        return new HashMap<>();
    }

    /**
     * The original file name when uploaded.
     *
     * @return the original name of the file.
     */
    @JsonProperty(ORIGINAL_NAME)
    public String getOriginalName() {
        return originalName;
    }

    /**
     * The current generated name of the file.
     * This will be used to find the file in the file system.
     *
     * @return the current name of the file.
     */
    @JsonProperty(NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(VERSIONS)
    public Map<String, FileVersion> getVersions() {
        return new HashMap<>(versions);
    }

    @JsonIgnore
    public List<FileVersion> getVersionsAsList() {
        return new ArrayList<>(versions.values());
    }

    @JsonIgnore
    public FileVersion putVersion(String format, FileVersion fileVersion) {
        return versions.put(format, fileVersion);
    }

    @JsonIgnore
    public FileVersion getVersion(String format) {
        return versions.get(format);
    }

    @Override
    public JsonNode toJson(ObjectNode body) {
        body.put(ORIGINAL_NAME, originalName);
        body.put(NAME, name);
        body.put(COUNT, versions.size());
        body.putArray(VERSIONS).addAll(toArrayNode(versions));
        return body;
    }

    private ArrayNode toArrayNode(Map<String, FileVersion> versions) {
        ArrayNode body = Json.newArray();
        for (FileVersion version : versions.values()) {
            body.add(version.toJson());
        }
        return body;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FileMetadata that = (FileMetadata) o;

        return originalName.equals(that.originalName)
                && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        int result = originalName.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(),
                "%s [originalName: %s, name: %s]",
                this.getClass().getSimpleName(), getOriginalName(), getName());
    }
}
