package com.silibrina.tecnova.commons.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.silibrina.tecnova.commons.fs.FileHandle;
import com.silibrina.tecnova.commons.fs.FileSystem;
import com.silibrina.tecnova.commons.fs.local.LocalFileSystem;
import com.silibrina.tecnova.commons.fs.swift.SwiftFileSystem;
import com.silibrina.tecnova.commons.model.EntryMetadata;
import com.silibrina.tecnova.commons.model.db.MongoDBPersistenceDrive;
import com.silibrina.tecnova.commons.model.file.FileMetadata;
import com.silibrina.tecnova.commons.model.file.FileVersion;
import com.silibrina.tecnova.commons.utils.ConstantUtils.FilesPathUtils;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.io.FileUtils;
import org.openstack4j.api.OSClient;
import org.openstack4j.model.storage.object.SwiftContainer;
import org.openstack4j.model.storage.object.SwiftObject;
import org.openstack4j.model.storage.object.options.ContainerListOptions;
import org.openstack4j.model.storage.object.options.ObjectLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.libs.Json;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.silibrina.tecnova.commons.conf.ConfigConstants.Strings.GATHERER_INDEX_DIR;
import static com.silibrina.tecnova.commons.conf.ConfigConstants.Strings.LOCAL_STORAGE_BASEDIR;
import static com.silibrina.tecnova.commons.conf.ConfigConstants.Strings.TESTS_TARGET_FS;
import static com.silibrina.tecnova.commons.fs.FileSystemFactory.getFileSystem;
import static com.silibrina.tecnova.commons.model.EntryMetadata.*;
import static com.silibrina.tecnova.commons.utils.CommonsFileUtils.getInputStream;
import static com.silibrina.tecnova.commons.utils.ODFileUtils.getCurrentDir;

public class TestsUtils {
    private static final Logger logger = LoggerFactory.getLogger(TestsUtils.class);
    private static final String TMP_ORIGINAL = "original";

    public static JsonNode generateFileMessagePayload(FilesPathUtils file, FileSystem fileSystem) throws Exception {
        URI resultFile = generateFileCopy(file, fileSystem);
        String originalName = new File(getCurrentDir(file.path)).getName();

        ObjectNode payload = Json.newObject();
        payload.put(FileVersion.PATH, String.valueOf(resultFile));
        payload.put(FileMetadata.ORIGINAL_NAME, originalName);
        return payload;
    }


    public static EntryMetadata createEntry(JsonNode body, FilesPathUtils file) throws Exception {
        EntryMetadata entry = EntryMetadata.buildEntryMetaData(body);
        entry.save();
        if (file != null) {
            entry.setFileMetadata(generateFileMetadata(file, new LocalFileSystem()));
            entry.save();
        }
        return entry;
    }

    public static URI generateFileCopy(FilesPathUtils file, FileSystem fileSystem) throws Exception {
        FileHandle destFile = generatePath(TMP_ORIGINAL, fileSystem);
        File original = new File(getCurrentDir(file.path));

        try (InputStream srcStream = getInputStream(original.toURI())) {
            fileSystem.copy(srcStream, destFile);
        }

        return destFile.toURI();
    }

    public static EntryMetadata updateEntry(EntryMetadata entry, JsonNode body, FilesPathUtils file)
            throws Exception {
        entry.updateEntry(body);
        if (file != null) {
            FileMetadata destFile = generateFileMetadata(file, new LocalFileSystem());
            entry.setFileMetadata(destFile);
        }

        entry.save();
        return entry;
    }

    public static ObjectNode generateBody(int id, int startOffset, int endOffset) throws IOException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        String startDate = formatter.format(generateDate(startOffset));
        String endDate = formatter.format(generateDate(endOffset));

        ObjectNode result = Json.newObject();
        result.put(TITLE, "some_title_" + id);
        result.put(DESCRIPTION, "some_description_" + id);
        result.put(AUTHOR, "some_author_" + id);
        result.put(ORG, "some_org_" + id);
        result.put(PERIOD_START, startDate);
        result.put(PERIOD_END, endDate);
        result.put(UPLOADER, "some_uploader_" + id);
        return result;
    }

    public static Date generateDate(int offsetDate) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) - 1);
        cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + offsetDate);
        return cal.getTime();
    }

    public static FileHandle generatePath(String type, FileSystem fileSystem) throws Exception {
        return fileSystem.open(type, generateFileName());
    }

    public static String getStorageDir() throws ConfigurationException {
        Config config = ConfigFactory.load();
        return config.getString(LOCAL_STORAGE_BASEDIR.field);
    }

    public static String getIndexDir() throws ConfigurationException {
        Config config = ConfigFactory.load();
        return config.getString(GATHERER_INDEX_DIR.field);
    }

    private static void cleanUpStorage() {
        try {
            File dir = new File(getStorageDir());
            if (dir.exists()) {
                FileUtils.forceDelete(dir);
            }
        } catch (Throwable e) {
            logger.error("An error happened while cleaning up storage", e);
        }
    }

    private static void cleanUpIndex() {
        try {
            File dir = new File(getIndexDir());
            if (dir.exists()) {
                FileUtils.forceDelete(dir);
            }
        } catch (Throwable e) {
            logger.error("An error happened while cleaning up indexes", e);
        }
    }

    public static void cleanUpEntries() throws IOException, ConfigurationException {
        cleanUpIndex();
        cleanUpStorage();
        new MongoDBPersistenceDrive(ConfigFactory.load()).getCollection(EntryMetadata.class).drop();
    }

    public static void cleanUpSwift(SwiftFileSystem swiftFileSystem) {
        OSClient.OSClientV3 client = swiftFileSystem.getOS();
        List<? extends SwiftContainer> containersList = client.objectStorage().containers()
                .list(ContainerListOptions.create().startsWith(swiftFileSystem.getContainerName()));

        for (SwiftContainer container : containersList) {
            List<? extends SwiftObject> objects = client.objectStorage().objects().list(container.getName());
            for (SwiftObject swiftObject : objects) {
                client.objectStorage().objects().delete(ObjectLocation.create(container.getName(), swiftObject.getName()));
            }
            client.objectStorage().containers().delete(container.getName());
        }
    }

    /**
     * Generates {@link EntryMetadata} without {@link FileMetadata}
     *
     * @param id and id to identify the entry.
     * @param startOffset for the month in the start date.
     * @param endOffset for the month in the end date.
     *
     * @return the generated entry metadata.
     *
     * @throws ParseException if an error happens while parsing dates.
     */
    public static EntryMetadata generateEntryMetadata(int id, int startOffset, int endOffset)
            throws ParseException {
        String author = String.format(Locale.getDefault(), "some_author_%d", id);
        String org = String.format(Locale.getDefault(), "some_org_%d", id);
        String title = String.format(Locale.getDefault(), "some_title_%d", id);
        String description = String.format(Locale.getDefault(), "some_description_%d", id);
        String uploader = String.format(Locale.getDefault(), "some_uploader_%d", id);

        Date createdAt = new Date();
        Date updatedAt = new Date();
        Date startPeriod = generateDate(startOffset);
        Date endPeriod = generateDate(endOffset);
        EntryMetadata.Status status = Status.TO_INDEX;

        EntryMetadata entryMetadata = new EntryMetadata(null, author, org, title, description, uploader,
                createdAt, updatedAt, startPeriod, endPeriod, status, null);
        entryMetadata.save();
        return entryMetadata;
    }

    public static EntryMetadata generateEntryMetadata(int id, int startOffset, int endOffset,
                                                      FilesPathUtils file, FileSystem fileSystem) throws Exception {
        EntryMetadata entry = generateEntryMetadata(id, startOffset, endOffset);

        if (file != null) {
            FileMetadata destFile = generateFileMetadata(file, fileSystem);
            entry.setFileMetadata(destFile);
        }
        entry.save();
        return entry;
    }

    public static EntryMetadata generateEntryMetadata(int id, FilesPathUtils file) throws Exception {
        return generateEntryMetadata(id, 0, 1, file, new LocalFileSystem());
    }

    public static FileMetadata generateFileMetadata(FilesPathUtils file, FileSystem fileSystem) throws Exception {
        FileVersion fileVersion = generateFileVersion(file, fileSystem, "original");

        String originalName = new File(getCurrentDir(file.path)).getName();
        String name = new File(fileVersion.getPath().getPath()).getName();

        Map<String, FileVersion> fileVersions = new HashMap<>();
        fileVersions.put("original", fileVersion);
        return new FileMetadata(originalName, name, fileVersions);
    }

    public static FileVersion generateFileVersion(FilesPathUtils file, FileSystem fileSystem, String type) throws Exception {
        fileSystem.createBasedir();
        fileSystem.mkdir(type);

        File original = new File(getCurrentDir(file.path));
        FileHandle dstHandle = fileSystem.open(type, generateFileName());

        try (InputStream inputStream = getInputStream(original.toURI())) {
            fileSystem.copy(inputStream, dstHandle);
        }

        URL url = fileSystem.toURL(dstHandle);

        return new FileVersion(file.format, file.size, file.hash, dstHandle.toURI(), url.toString(), file.mimeType, file.subType, file.type);
    }

    public static boolean fileVersionExists(FileVersion fileVersion) {
        return uriExists(fileVersion.getPath());
    }

    public static boolean uriExists(URI uri) {
        try {
            try (InputStream inputStream = getInputStream(uri)) {
                return inputStream.available() > 0;
            }
        } catch (IOException e) {
            return false;
        }
    }

    public static FileSystem[] getFileSystemsToTest() {
        Config config = ConfigFactory.load();
        String[] targets = config.getString(TESTS_TARGET_FS.field).split(",");
        FileSystem[] fileSystems = new FileSystem[targets.length];

        for (int i = 0; i < fileSystems.length; i++) {
            fileSystems[i] = getFileSystem(targets[i]);
        }

        return fileSystems;
    }

    public static String generateFileName() {
        return String.valueOf(UUID.randomUUID());
    }

}
