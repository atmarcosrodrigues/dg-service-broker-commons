package com.silibrina.tecnova.commons.fs.swift;

import com.silibrina.tecnova.commons.fs.FileHandle;
import com.silibrina.tecnova.commons.fs.local.LocalFileSystem;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.tika.io.TikaInputStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openstack4j.api.OSClient;
import org.openstack4j.api.storage.ObjectStorageContainerService;
import org.openstack4j.model.storage.object.SwiftContainer;
import org.openstack4j.model.storage.object.SwiftObject;
import org.openstack4j.model.storage.object.options.ContainerListOptions;
import org.openstack4j.model.storage.object.options.ObjectLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

import static com.silibrina.tecnova.commons.conf.ConfigConstants.Strings.TESTS_TARGET_FS;
import static com.silibrina.tecnova.commons.utils.CommonsFileUtils.getInputStream;
import static com.silibrina.tecnova.commons.utils.ConstantUtils.FilesPathUtils.XLS_FILE_1;
import static com.silibrina.tecnova.commons.utils.ODFileUtils.getCurrentDir;
import static com.silibrina.tecnova.commons.utils.TestsUtils.cleanUpEntries;
import static com.silibrina.tecnova.commons.utils.TestsUtils.cleanUpSwift;
import static com.silibrina.tecnova.commons.utils.TestsUtils.generateFileName;
import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

public class SwiftFileSystemTests {
    private static final Logger logger = LoggerFactory.getLogger(SwiftFileSystemTests.class);

    private static final String TYPE = "original";
    private static final String DIR_DST = "original-dst";

    private SwiftFileSystem fileSystem;

    @Before
    public void setUp() throws Exception {
        logger.debug("==> {}", new Object(){}.getClass().getEnclosingMethod().getName());
        if (!executingWithSwiftFs()) {
            return;
        }

        cleanUpEntries();

        fileSystem = new SwiftFileSystem();
        fileSystem.createBasedir();
        fileSystem.mkdir(TYPE);

        logger.debug("<== {} finished", new Object(){}.getClass().getEnclosingMethod().getName());
    }

    private boolean executingWithSwiftFs() {
        Config config = ConfigFactory.load();
        String[] targets = config.getString(TESTS_TARGET_FS.field).split(",");
        for (String target : targets) {
            if ("swift".equals(target)) {
                return true;
            }
        }
        return false;
    }

    @After
    public void cleanUp() throws IOException, ConfigurationException {
        logger.debug("==> {}", new Object(){}.getClass().getEnclosingMethod().getName());
        if (!executingWithSwiftFs()) {
            return;
        }

        cleanUpSwift(fileSystem);
        cleanUpEntries();

        logger.debug("<== {} finished", new Object(){}.getClass().getEnclosingMethod().getName());
    }

    @Test
    public void createBasedirTest() throws IOException {
        logger.debug("==> {}", new Object(){}.getClass().getEnclosingMethod().getName());
        assumeTrue("No swift test", executingWithSwiftFs());

        OSClient.OSClientV3 os = fileSystem.getOS();
        ObjectStorageContainerService containers = os.objectStorage().containers();
        List<? extends SwiftContainer> containersList = containers.list(ContainerListOptions
                .create().startsWith(fileSystem.getContainerName()));

        assertNotNull("should not be null", containersList);
        assertFalse("should not be empty", containersList.isEmpty());
        assertEquals("should have one container", 1, containersList.size());

        SwiftContainer container = containersList.get(0);
        assertEquals("should have created the container with the given name", fileSystem.getContainerName(), container.getName());
    }

    @Test
    public void mkdirTest() throws IOException {
        logger.debug("==> {}", new Object(){}.getClass().getEnclosingMethod().getName());
        assumeTrue("No swift test", executingWithSwiftFs());

        OSClient.OSClientV3 client = fileSystem.getOS();

        SwiftObject directory = client.objectStorage().objects().get(fileSystem.getContainerName(), TYPE);
        assertNotNull("should have find a directory", directory);
        assertEquals("should have the given directory name", directory.getName(), TYPE);
        assertTrue("should be a directory", directory.isDirectory());

        fileSystem.mkdir(DIR_DST);

        directory = client.objectStorage().objects().get(fileSystem.getContainerName(), DIR_DST);
        assertNotNull("should have find a directory", directory);
        assertEquals("should have the given directory name", directory.getName(), DIR_DST);
        assertTrue("should be a directory", directory.isDirectory());
    }

    @Test
    public void copyTest() throws IOException {
        logger.debug("==> {}", new Object(){}.getClass().getEnclosingMethod().getName());
        assumeTrue("No swift test", executingWithSwiftFs());

        File file = new File(getCurrentDir(XLS_FILE_1.path));
        FileHandle dst = fileSystem.open(DIR_DST, generateFileName());

        fileSystem.copy(getInputStream(file.toURI()), dst);

        OSClient.OSClientV3 client = fileSystem.getOS();
        SwiftObject copiedFile = client.objectStorage().objects().get(ObjectLocation.create(fileSystem.getContainerName(),
                dst.getLocalPath()));

        assertNotNull("copied file should not be null", copiedFile);
        assertFalse("should be a file", copiedFile.isDirectory());
        assertEquals("should be the same name of the handle", dst.getLocalPath(), copiedFile.getName());

        BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
        assertEquals("should have the same size of the original", attr.size(), copiedFile.getSizeInBytes());
    }

    @Test
    public void deleteTest() throws IOException {
        logger.debug("==> {}", new Object(){}.getClass().getEnclosingMethod().getName());
        assumeTrue("No swift test", executingWithSwiftFs());

        FileHandle handle = fileSystem.open(TYPE, generateFileName());
        File file = new File(getCurrentDir(XLS_FILE_1.path));

        fileSystem.copy(TikaInputStream.get(file.toURI()), handle);

        OSClient.OSClientV3 client = fileSystem.getOS();
        SwiftObject object = client.objectStorage().objects().get(fileSystem.getContainerName(), handle.getLocalPath());

        BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
        assertEquals("should have the same size of the original", attr.size(), object.getSizeInBytes());

        fileSystem.delete(handle);

        object = client.objectStorage().objects().get(fileSystem.getContainerName(), handle.getLocalPath());
        assertNull("object should be null (since it was deleted)", object);
    }


    @Test
    public void openTest() throws IOException {
        logger.debug("==> {}", new Object(){}.getClass().getEnclosingMethod().getName());
        assumeTrue("No swift test", executingWithSwiftFs());

        FileHandle handle = fileSystem.open(TYPE, generateFileName());
        File file = new File(getCurrentDir(XLS_FILE_1.path));

        fileSystem.copy(TikaInputStream.get(file.toURI()), handle);

        OSClient.OSClientV3 client = fileSystem.getOS();
        SwiftObject object = client.objectStorage().objects().get(fileSystem.getContainerName(), handle.getLocalPath());

        BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
        assertEquals("should have the same size of the original", attr.size(), object.getSizeInBytes());

        FileHandle handleOpen = fileSystem.open(TYPE, handle.getFileName());
        assertEquals("should be the same handle", handle, handleOpen);

        fileSystem.delete(handleOpen);

        object = client.objectStorage().objects().get(fileSystem.getContainerName(), handle.getLocalPath());
        assertNull("object should be null (since it was deleted)", object);
    }

    @Test
    public void eTagChecksumTest() throws IOException {
        logger.debug("==> {}", new Object(){}.getClass().getEnclosingMethod().getName());
        assumeTrue("No swift test", executingWithSwiftFs());

        FileHandle handle = fileSystem.open(TYPE, generateFileName());
        File file = new File(getCurrentDir(XLS_FILE_1.path));

        String checksumSwift = fileSystem.copy(TikaInputStream.get(file.toURI()), handle);
        assertNotNull("checksumSwift should not be null", checksumSwift);

        LocalFileSystem localFileSystem = new LocalFileSystem();
        FileHandle localHandle = localFileSystem.open(TYPE, generateFileName());

        String checksumLocal = localFileSystem.copy(getInputStream(file.toURI()), localHandle);
        assertNotNull("checksumLocal should not be null", checksumLocal);

        assertEquals("checksumSwift should be the same of checksumLocal", checksumSwift, checksumLocal);
    }

    @Test
    public void toURLTest() throws IOException, URISyntaxException {
        logger.debug("==> {}", new Object() {}.getClass().getEnclosingMethod().getName());
        assumeTrue("No swift test", executingWithSwiftFs());

        FileHandle handle = fileSystem.open(TYPE, generateFileName());
        URL url = fileSystem.toURL(handle);

        assertNotNull("url should not be null", url);
        assertEquals("url should match", handle.toURI().toURL(), url);
    }
}
