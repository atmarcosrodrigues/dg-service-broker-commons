package com.silibrina.tecnova.commons.model.file;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.silibrina.tecnova.commons.model.JsonParseable;

import java.net.URI;
import java.util.Locale;

public class FileVersion extends JsonParseable {
    public static final String FORMAT = "format";
    public static final String SIZE = "size";
    public static final String HASH = "hash";
    public static final String PATH = "path";
    public static final String URL = "url";
    public static final String MIME_TYPE = "mime_type";
    public static final String MIME_SUBTYPE = "subtype";
    public static final String TYPE = "type";

    private final String format;
    private final long size;
    private final String hash;
    private final URI path;
    private final String url;
    private final String mimeType;
    private final String subType;
    private final String type;

    public FileVersion(@JsonProperty(FORMAT) String format, @JsonProperty(SIZE) long size,
                       @JsonProperty(HASH) String hash, @JsonProperty(PATH) URI path,
                       @JsonProperty(URL) String url, @JsonProperty(MIME_TYPE) String mimeType,
                       @JsonProperty(MIME_SUBTYPE) String subType, @JsonProperty(TYPE) String type) {
        this.format = format;
        this.size = size;
        this.hash = hash;
        this.path = path;
        this.url = url;
        this.mimeType = mimeType;
        this.subType = subType;
        this.type = type;
    }

    @JsonProperty(FORMAT)
    public String getFormat() {
        return format;
    }

    /**
     * the size of the file in bytes. The size may differ from the
     * actual size on the file system due to compression, support
     * for sparse files, or other reasons. The size of files that
     * are not regular files is implementation specific and therefore
     * unspecified.
     *
     * @return the file size, in bytes
     */
    @JsonProperty(SIZE)
    public long getSize() {
        return size;
    }

    @JsonProperty(HASH)
    public String getHash() {
        return hash;
    }

    @JsonProperty(PATH)
    public URI getPath() {
        return path;
    }

    /**
     * The path to get the file, note that when concatenated with the
     * the base url (e.g.: http://myserver.com:9000) it will provide
     * the url to download the original file content.
     * E.g.: /data/:id
     *
     * @return the url (path) to download this file (original content).
     */
    @JsonProperty(URL)
    public String getUrl() {
        return url;
    }

    /**
     * Full mime type of this file (base type).
     * E.g.: application/pdf
     *
     * @return mime type of the file.
     */
    @JsonProperty(MIME_TYPE)
    public String getMimeType() {
        return mimeType;
    }

    /**
     * Sub type of the file.
     * E.g.: pdf
     *
     * @return the base type of the file.
     */
    @JsonProperty(MIME_SUBTYPE)
    public String getSubType() {
        return subType;
    }

    /**
     * Type of the file (according to its mime type).
     * E.g.: application
     *
     * @return type of the file.
     */
    @JsonProperty(TYPE)
    public String getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FileVersion that = (FileVersion) o;

        if (size != that.size) return false;
        if (format != null ? !format.equals(that.format) : that.format != null) return false;
        if (hash != null ? !hash.equals(that.hash) : that.hash != null) return false;
        if (path != null ? !path.equals(that.path) : that.path != null) return false;
        if (url != null ? !url.equals(that.url) : that.url != null) return false;
        if (mimeType != null ? !mimeType.equals(that.mimeType) : that.mimeType != null) return false;
        if (subType != null ? !subType.equals(that.subType) : that.subType != null) return false;
        return type != null ? type.equals(that.type) : that.type == null;

    }

    @Override
    public int hashCode() {
        int result = format != null ? format.hashCode() : 0;
        result = 31 * result + (int) (size ^ (size >>> 32));
        result = 31 * result + (hash != null ? hash.hashCode() : 0);
        result = 31 * result + (path != null ? path.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (mimeType != null ? mimeType.hashCode() : 0);
        result = 31 * result + (subType != null ? subType.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(),
                "%s [format: %s, size: %d, hash:%s, path: %s, url: %s, mimeType: %s, subType: %s, type: %s]",
                this.getClass().getSimpleName(), format, size, hash, path, url, mimeType, subType, type);
    }

    @Override
    public JsonNode toJson(ObjectNode body) {
        body.put(FORMAT, format);
        body.put(SIZE, size);
        body.put(HASH, hash);
        body.put(URL, url);
        body.put(MIME_TYPE, mimeType);
        body.put(MIME_SUBTYPE, subType);
        body.put(TYPE, type);
        return body;
    }

}
