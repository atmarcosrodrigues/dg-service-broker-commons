package com.silibrina.tecnova.commons.fs;

import com.silibrina.tecnova.commons.model.file.FileVersion;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

/**
 * Describes the operations a file system must offer.
 *
 * A file system is responsible for file storing, retrieving and deletion.
 */
public abstract class FileSystem {
    private static final int FILE_NAME_INDEX = 1;
    private static final int TYPE_INDEX = 2;
    private static final int CONTAINER_INDEX = 3;
    private static final int BASEDIR_INDEX = 3;

    /**
     * Creates the base directory or container,
     * where files will be stored. This creates the necessary structure
     * to store files in the underlining file system.
     *
     * @throws IOException if an error happens while trying to create
     *                      base structure.
     */
    public abstract void createBasedir() throws IOException;

    /**
     * Creates the given directory tree under the base dir.
     *
     * @param path the path of the given directory inside the base dir.
     * @throws IOException if an error happens while trying to create
     *                      base structure.
     */
    public abstract void mkdir(String path) throws IOException;

    /**
     * Copies the content of the {@code src} handle to the given {@code dst} handle.
     * This method can be used to copy a file from the current fie system to the
     * file system storing the files for this service.
     *
     * @param src the origin handle where the content will be copied.
     * @param dst the target handle where the content will be copied to.
     *
     * @return the checksum of this file, or null if something went wrong during copy.
     *
     * @throws IOException if an error happens while trying copy the content.
     */
    public abstract String copy(FileHandle src, FileHandle dst) throws IOException;

    /**
     * Copies the content of the {@code srcInputStream} stream to the given {@code dst} handle.
     * This method can be used to copy a file from the current fie system to the
     * file system storing the files for this service or any general location.
     *
     * @param srcInputStream the stream for the src content (file, general stream etc).
     * @param dst the target handle where the content will be copied to.
     *
     * @return the checksum of this file, or null if something went wrong during copy.
     *
     * @throws IOException if an error happens while trying copy the content.
     */
    public abstract String copy(InputStream srcInputStream, FileHandle dst) throws IOException;

    /**
     * Deletes the file associated with the given handle.
     * This call will fail if the given file does not exists.
     *
     * @param handle the handle to the file to be deleted.
     *
     * @throws IOException if an error happens while trying to delete the file.
     */
    public abstract void delete(FileHandle handle) throws IOException;

    /**
     * Creates a handle for the given file name of the given type.
     * The file does not need to exist and this method will not make any
     * assumptions based on this, since files are effectively created
     * when content is copied to it using {@code copy} method.
     *
     * @param type the type of the file to create the handle.
     * @param fileName the name of the file.
     *
     * @return the handle for this file (like a file descriptor).
     *
     * @throws IOException if an error happens while trying to create the handle for the file.
     */
    public abstract FileHandle open(String type, String fileName) throws IOException;

    /**
     * Generates an URL for the given file based on its underlining file system.
     *
     * @param handle The handle to the file to have a url generated.
     *
     * @return An URL for the given file
     *
     * @throws MalformedURLException If a protocol handler for the URL could not be found,
     *                              or if some other error occurred while constructing the URL
     */
    public abstract URL toURL(FileHandle handle) throws MalformedURLException;

    /**
     * Creates a handle for the given {@link FileVersion}.
     * This method will try to get the {@link FileHandle} from the {@code path} in the
     * {@link FileVersion}, start from the end to begin.
     * It expects a path with the following format:
     *
     * Swift: http://host:port/basedir/container/type/file_name
     * Local: file:///basedir/container/type/file_name
     *
     * @param fileVersion the version of the file to get the handle to.
     *
     * @return the handle for this file (like a file descriptor).
     *
     * @throws IOException if an error happens while trying to create the handle for the file.
     */
    public FileHandle open(FileVersion fileVersion) throws IOException {
        URI uri = fileVersion.getPath();
        String uriAsString = uri.toString();
        String[] uriAsArray = uriAsString.split(File.separator);

        String base = eliminateLastDirs(uriAsString, BASEDIR_INDEX);
        String container = uriAsArray[uriAsArray.length - CONTAINER_INDEX];
        String type = uriAsArray[uriAsArray.length - TYPE_INDEX];
        String fileName = uriAsArray[uriAsArray.length - FILE_NAME_INDEX];

        return new FileHandle(base, container, type, fileName);
    }

    private String eliminateLastDirs(String uri, int dirNumber) {
        uri = normalize(uri);

        for (int i = 0; i < dirNumber; i++) {
            uri = normalize(uri.substring(0, uri.lastIndexOf(File.separator)));
        }

        return uri;
    }

    /**
     * Eliminates the last / (file separator) occurrence, if it is the last character.
     *
     * @param uri string to be normalized.
     *
     * @return normalized string.
     */
    private String normalize(String uri) {
        while(uri.endsWith(File.separator)) {
            uri = uri.substring(0, uri.length() - 1);
        }
        return uri;
    }
}
