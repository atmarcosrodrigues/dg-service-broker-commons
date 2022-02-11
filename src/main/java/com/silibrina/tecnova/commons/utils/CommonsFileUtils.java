package com.silibrina.tecnova.commons.utils;

import org.apache.tika.io.TikaInputStream;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

/**
 * Utils class to deal with file system generals.
 */
public class CommonsFileUtils {

    /**
     * This method is a workaround to permit running junit tests inside intellij.
     * The problem is that during execution, intellij changes the working dir to
     * .idea/modules and some files stays in the root of the project, so paths
     * considering relative path would have problems trying to access those files.
     *
     * @param file relative path to the file inside the current dir.
     *
     * @return a path to the root of the project
     */
    public static String getCurrentDir(String file) {
        File current = new File("");

        if (current.getAbsolutePath().endsWith(".idea/modules")) {
            current = new File("../../" + file);
        } else {
            current = new File(file);
        }

        return current.getAbsolutePath();
    }

    /**
     * Gets an {@link InputStream} to a local file or remote storage.
     *
     * @param uri uri to the file.
     *
     * @return the input stream to the file or remote location.
     *
     * @throws IOException if the resource can not be accessed
     */
    public static InputStream getInputStream(URI uri) throws IOException {
        return TikaInputStream.get(uri);
    }
}