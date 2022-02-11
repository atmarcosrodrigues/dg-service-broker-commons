package com.silibrina.tecnova.commons.fs;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.UUID;

import static com.silibrina.tecnova.commons.utils.Preconditions.checkValidString;

/**
 * This is the handle for a object (file, storage object etc).
 */
public class FileHandle {
    /**
     * Local: file
     * Swift: http
     */
    private final Scheme scheme;

    /**
     * base should hold:
     * Local: /tmp/opendata
     * Swift: server:8080/v1/AUTH_HASH
     */
    private final String base;

    /**
     * Local: opendata
     * Swift: opendata
     */
    private final String container;

    /**
     * Local: original (json, xml, csv etc)
     * Swift: original (json, xml, csv etc)
     */
    private final String type;

    /**
     * Local: generated {@link UUID}.
     * Swift: generated {@link UUID}.
     */
    private final String fileName;

    public FileHandle(String base, String container, String type, String fileName) {
        checkValidString("base should be a valid string", base);
        checkValidString("container should be a valid string", container);
        checkValidString("type should be a valid string", type);
        checkValidString("fileName should be a valid string", fileName);

        this.scheme = getScheme(base);
        this.base = base;
        this.container = container;
        this.type = type;
        this.fileName = fileName;
    }

    private Scheme getScheme(String base) {
        return base.startsWith("http") ? Scheme.HTTP : Scheme.FILE;
    }

    public String getScheme() {
        return String.valueOf(scheme).toLowerCase();
    }

    public String getBase() {
        return base;
    }

    public String getContainer() {
        return container;
    }

    public String getType() {
        return type;
    }

    public String getFileName() {
        return fileName;
    }

    public String getLocalPath() {
        return String.format(Locale.getDefault(), "%s/%s", type, fileName);
    }

    public URI toURI() throws URISyntaxException {
        switch (scheme) {
            case HTTP:
                return URI.create(String.format(Locale.getDefault(),
                        "%s/%s/%s/%s",
                        base, container, type, fileName));
            case FILE:
            default:
                return URI.create(String.format(Locale.getDefault(),
                        getStringFormatter(),
                        base, container, type, fileName));
        }
    }

    private String getStringFormatter() {
        return base.startsWith("file:") ? "%s/%s/%s/%s" : "file://%s/%s/%s/%s";
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(),
                "scheme: %s, base: %s, container: %s, type: %s, fileName: %s",
                scheme, base, container, type, fileName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FileHandle that = (FileHandle) o;

        if (scheme != that.scheme) return false;
        if (!base.equals(that.base)) return false;
        if (!container.equals(that.container)) return false;
        if (!type.equals(that.type)) return false;
        return fileName.equals(that.fileName);
    }

    @Override
    public int hashCode() {
        int result = scheme.hashCode();
        result = 31 * result + base.hashCode();
        result = 31 * result + container.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + fileName.hashCode();
        return result;
    }

    private enum Scheme {
        HTTP,
        FILE
    }
}
