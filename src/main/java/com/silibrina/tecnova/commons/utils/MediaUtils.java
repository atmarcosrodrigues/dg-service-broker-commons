package com.silibrina.tecnova.commons.utils;

import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.detect.Detector;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MimeTypes;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import static com.silibrina.tecnova.commons.utils.CommonsFileUtils.getInputStream;

public class MediaUtils {
    public static final String PDF_MIME = "application/pdf";
    public static final String TXT_MIME = "text/plain";
    public static final String DOC_MIME = "application/msword";
    public static final String ODT_MIME = "application/vnd.oasis.opendocument.text";
    public static final String XLS_MIME = "application/vnd.ms-excel";
    public static final String XLSX_MIME = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    public static final String HTML_MIME = "text/html";
    public static final String XML_MIME = "application/xml";
    public static final String JSON_MIME = "application/json";
    public static final String CSV_MIME = "text/csv";

    private static final String EXTENSION_SEPARATOR = ".";
    private static final String JSON_EXTENSION = EXTENSION_SEPARATOR + "json";
    private static final String CSV_EXTENSION = EXTENSION_SEPARATOR + "csv";
    private static final String XML_EXTENSION = EXTENSION_SEPARATOR + "xml";

    public static MediaType extractMediaType(URI uri, String originalName) throws IOException {
        try (InputStream stream = getInputStream(uri)) {
            Metadata metadata = new Metadata();
            Detector detector = new DefaultDetector(MimeTypes.getDefaultMimeTypes());
            MediaType mediaMetadata = detector.detect(stream, metadata);

            if (TXT_MIME.equals(String.valueOf(mediaMetadata.getBaseType()))) {
                mediaMetadata = mimeFromExtension(originalName, mediaMetadata);
            } else if (HTML_MIME.equals(String.valueOf(mediaMetadata.getBaseType()))) {
                mediaMetadata = mimeFromExtension(originalName, mediaMetadata);
            }
            return mediaMetadata;
        }
    }

    private static MediaType mimeFromExtension(String originalName, MediaType mediaType) {
        if (hasExtension(originalName, JSON_EXTENSION)) {
            return new MediaType("application", "json");
        } else if (hasExtension(originalName, CSV_EXTENSION)) {
            return new MediaType("text", "csv");
        } else if (hasExtension(originalName, XML_EXTENSION)) {
            return new MediaType("application", "xml");
        }
        return mediaType;
    }

    private static boolean hasExtension(String fileName, String extension) {
        int index = fileName.lastIndexOf(EXTENSION_SEPARATOR);
        return index >= 0
                && fileName.substring(index).trim().equals(extension);
    }
}
