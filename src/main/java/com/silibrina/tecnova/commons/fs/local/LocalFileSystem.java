package com.silibrina.tecnova.commons.fs.local;

import com.silibrina.tecnova.commons.fs.FileHandle;
import com.silibrina.tecnova.commons.fs.FileSystem;
import com.silibrina.tecnova.commons.fs.InputStreamAttrExtractor;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Locale;

import static com.silibrina.tecnova.commons.conf.ConfigConstants.Strings.LOCAL_CONTAINER_DIR;
import static com.silibrina.tecnova.commons.conf.ConfigConstants.Strings.LOCAL_STORAGE_BASEDIR;
import static com.silibrina.tecnova.commons.conf.ConfigConstants.Strings.LOCAL_STORAGE_URL;
import static com.silibrina.tecnova.commons.utils.Preconditions.*;

/**
 * Controls files stored locally.
 * Note that, it works with distributed system based on fuse or fuse like,
 * since it is user transparent. This means that it would work over NFS,
 * GlusterFS, Ceph etc.
 */
public class LocalFileSystem extends FileSystem {
    private static final Logger logger = LoggerFactory.getLogger(LocalFileSystem.class);

    private final Config config;

    private final String baseUrl;
    private final String baseDir;
    private final String container;

    public LocalFileSystem() {
        config = ConfigFactory.load();

        baseUrl = getBaseUrl();
        baseDir = getBaseDir();
        container = getContainerName();
    }

    @Override
    public void createBasedir() throws IOException {
        String absolutePath = String.format(Locale.getDefault(),
                "%s/%s", baseDir, container);

        logger.debug("absolutePath: {}", absolutePath);

        FileUtils.forceMkdir(new File(absolutePath));
    }

    @Override
    public void mkdir(String path) throws IOException {
        logger.debug("path: {}", path);

        checkValidString("path can not be null", path);

        String absolutePath = String.format(Locale.getDefault(),
                "%s/%s/%s", baseDir, container, path);
        FileUtils.forceMkdir(new File(absolutePath));
    }

    @Override
    public String copy(FileHandle src, FileHandle dst) throws IOException {
        logger.debug("src: {}, dst: {}", src, dst);

        checkNotNullCondition("open src target file first (src handle was null)", src);
        checkNotNullCondition("open dst target file first (dst handle was null)", dst);

        try {
            File srcFile = new File(src.toURI());
            try (FileInputStream fileInputStream = new FileInputStream(srcFile)) {
                try (InputStream inputStream = new BufferedInputStream(fileInputStream)) {
                    return copy(inputStream, dst);
                }
            }
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }
    }

    @Override
    public String copy(InputStream srcInputStream, FileHandle dst) throws IOException {
        logger.debug("srcInputStream: {}, dst: {}", srcInputStream, dst);

        checkNotNullCondition("srcInputStream can not be null", srcInputStream);
        checkNotNullCondition("open dst target file first (dst handle was null)", dst);

        try {
            File outputFile = new File(dst.toURI());

            FileUtils.copyInputStreamToFile(srcInputStream, outputFile);
            InputStreamAttrExtractor.InputStreamAttr hash = InputStreamAttrExtractor.extractAttr(outputFile);
            return hash == null ? null : hash.checksum.toLowerCase();
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void delete(FileHandle handle) throws IOException {
        logger.debug("handle: {}", handle);

        checkNotNullCondition("open target file first (handle was null)", handle);

        try {
            File file = new File(handle.toURI());

            checkIOCondition("file do not exist: " + file.getAbsolutePath(), file.exists());
            checkIOCondition("this does not seem to be a file", file.isFile());
            checkIOCondition("can not open the file", file.canRead());

            File parent = new File(file.getParent());
            checkIOCondition("missing permission to delete the file", parent.canExecute() && parent.canWrite());

            FileUtils.forceDelete(file);
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }
    }

    @Override
    public FileHandle open(String type, String fileName) throws IOException {
        logger.debug("type: {}, fileName: {}", type, fileName);

        checkValidString("type can not be null", type);
        checkValidString("fileName can not be null", fileName);

        return new FileHandle(baseDir, container, type, fileName);
    }

    @Override
    public URL toURL(FileHandle handle) throws MalformedURLException {
        logger.debug("handle: {}", handle);

        checkNotNullCondition("handle should not be null", handle);

        return new URL(String.format(Locale.getDefault(),
                "%s/storage/%s/%s",
                baseUrl, handle.getType(), handle.getFileName()));
    }

    String getBaseDir() {
        return config.getString(LOCAL_STORAGE_BASEDIR.field);
    }

    String getContainerName() {
        return config.getString(LOCAL_CONTAINER_DIR.field);
    }

    private String getBaseUrl() {
        return config.getString(LOCAL_STORAGE_URL.field);
    }
}
