package com.silibrina.tecnova.commons.fs.local;

import com.silibrina.tecnova.commons.fs.FileHandle;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.io.FileUtils;
import org.apache.tika.io.TikaInputStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Locale;

import static com.silibrina.tecnova.commons.utils.CommonsFileUtils.getInputStream;
import static com.silibrina.tecnova.commons.utils.ConstantUtils.FilesPathUtils.PDF_FILE_1;
import static com.silibrina.tecnova.commons.utils.ConstantUtils.FilesPathUtils.PDF_FILE_2;
import static com.silibrina.tecnova.commons.utils.ConstantUtils.FilesPathUtils.XLS_FILE_1;
import static com.silibrina.tecnova.commons.utils.ODFileUtils.getCurrentDir;
import static com.silibrina.tecnova.commons.utils.TestsUtils.cleanUpEntries;
import static com.silibrina.tecnova.commons.utils.TestsUtils.generateFileName;
import static org.apache.commons.io.FileUtils.readLines;
import static org.junit.Assert.*;

public class LocalFileSystemTests {
    private static final Logger logger = LoggerFactory.getLogger(LocalFileSystemTests.class);

    private static final String TYPE = "original";
    private static final String TYPE_2 = "original-dst";

    private LocalFileSystem fileSystem;

    @Before
    public void setUp() throws Exception {
        logger.debug("==> {}", new Object() {}.getClass().getEnclosingMethod().getName());
        cleanUpEntries();

        fileSystem = new LocalFileSystem();
        fileSystem.createBasedir();
        fileSystem.mkdir(TYPE);

        logger.debug("<== {} finished", new Object(){}.getClass().getEnclosingMethod().getName());
    }

    @After
    public void cleanUp() throws IOException, ConfigurationException {
        logger.debug("==> {}", new Object(){}.getClass().getEnclosingMethod().getName());

        cleanUpEntries();

        logger.debug("<== {} finished", new Object(){}.getClass().getEnclosingMethod().getName());
    }

    @Test
    public void createBasedirTest() throws IOException {
        logger.debug("==> {}", new Object(){}.getClass().getEnclosingMethod().getName());

        FileHandle handle = fileSystem.open(TYPE, generateFileName());
        File file = new File(handle.getBase());
        assertNotNull("file should not be null", file);
        assertEquals("base dir should be the one in config file",
                fileSystem.getBaseDir(), file.getAbsolutePath());
        assertTrue("base dir should exists", file.exists());
        assertTrue("base dir should be readable", file.canRead());
        assertTrue("should be a directory", file.isDirectory());
    }

    @Test
    public void mkdirTest() throws IOException {
        logger.debug("==> {}", new Object(){}.getClass().getEnclosingMethod().getName());

        File createdDir = new File(String.format(Locale.getDefault(),
                "%s/%s/%s", fileSystem.getBaseDir(), fileSystem.getContainerName(), TYPE));
        assertNotNull("directory should not be null", createdDir);
        assertTrue("directory should exists", createdDir.exists());
        assertTrue("directory should be readable", createdDir.canRead());
        assertTrue("should be a directory", createdDir.isDirectory());

        fileSystem.mkdir(TYPE_2);

        createdDir = new File(String.format(Locale.getDefault(),
                "%s/%s/%s", fileSystem.getBaseDir(), fileSystem.getContainerName(), TYPE_2));
        assertNotNull("directory should not be null", createdDir);
        assertTrue("directory should exists", createdDir.exists());
        assertTrue("directory should be readable", createdDir.canRead());
        assertTrue("should be a directory", createdDir.isDirectory());
    }

    @Test
    public void createTest() throws IOException, URISyntaxException {
        logger.debug("==> {}", new Object(){}.getClass().getEnclosingMethod().getName());

        FileHandle fileHandle = fileSystem.open(TYPE, generateFileName());
        logger.debug("fileHandle: {}", fileHandle);

        assertNotNull("file handle should not be null", fileHandle);
        assertEquals("scheme should not be null", "file", fileHandle.getScheme());
        assertEquals("host ",null, fileHandle.toURI().getHost());
        assertEquals(-1, fileHandle.toURI().getPort());
        assertEquals("container should be the same", "opendata", fileHandle.getContainer());
        assertEquals("container should be the same", TYPE, fileHandle.getType());

        assertEquals("file name should not be null", fileHandle.getFileName(),
                new File(fileHandle.toURI().getPath()).getName());

        assertEquals(String.format(Locale.getDefault(),
                "%s://%s/%s/%s/%s",
                fileHandle.getScheme(), fileHandle.getBase(), fileHandle.getContainer(),
                fileHandle.getType(), fileHandle.getFileName())
                , fileHandle.toURI().toString());
    }

    @Test
    public void copyTest() throws IOException, URISyntaxException {
        logger.debug("==> {}", new Object(){}.getClass().getEnclosingMethod().getName());

        File file = new File(getCurrentDir(XLS_FILE_1.path));
        FileHandle dst = fileSystem.open(TYPE_2, generateFileName());

        fileSystem.copy(new BufferedInputStream(new FileInputStream(file)), dst);

        File copiedFile = new File(dst.toURI());

        assertNotNull("copied file should not be null", copiedFile);
        assertFalse("should be a file", copiedFile.isDirectory());

        assertEquals("should be the same name of the handle", dst.getFileName(), copiedFile.getName());

        BasicFileAttributes attrSrc = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
        BasicFileAttributes attrDst = Files.readAttributes(copiedFile.toPath(), BasicFileAttributes.class);
        assertEquals("should have the same size of the original", attrSrc.size(), attrDst.size());
    }

    @Test
    public void deleteTest() throws IOException, URISyntaxException {
        logger.debug("==> {}", new Object(){}.getClass().getEnclosingMethod().getName());

        FileHandle handle = fileSystem.open(TYPE, generateFileName());
        File file = new File(getCurrentDir(XLS_FILE_1.path));

        fileSystem.copy(TikaInputStream.get(file.toURI()), handle);

        File copiedFile = new File(handle.toURI());

        assertTrue("copied file should exists", copiedFile.exists());
        assertEquals("file content should be the same",
                FileUtils.readFileToString(file), FileUtils.readFileToString(copiedFile));

        fileSystem.delete(handle);

        assertFalse("copied file should exists", copiedFile.exists());
    }

    @Test
    public void openTest() throws IOException, URISyntaxException {
        logger.debug("==> {}", new Object(){}.getClass().getEnclosingMethod().getName());

        FileHandle handle = fileSystem.open(TYPE, generateFileName());
        File file = new File(getCurrentDir(XLS_FILE_1.path));

        fileSystem.copy(TikaInputStream.get(file.toURI()), handle);

        File copiedFile = new File(handle.toURI());

        BasicFileAttributes attrSrc = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
        BasicFileAttributes attrDst = Files.readAttributes(copiedFile.toPath(), BasicFileAttributes.class);
        assertEquals("should have the same size of the original", attrSrc.size(), attrDst.size());

        FileHandle handleOpen = fileSystem.open(TYPE, handle.getFileName());
        assertEquals("should be the same handle", handle, handleOpen);

        fileSystem.delete(handleOpen);

        assertFalse("copied file should exists", copiedFile.exists());
    }

    @Test
    public void toURLTest() throws IOException, URISyntaxException {
        logger.debug("==> {}", new Object() {}.getClass().getEnclosingMethod().getName());

        FileHandle handle = fileSystem.open(TYPE, generateFileName());
        URL url = fileSystem.toURL(handle);

        assertNotNull("url should not be null", url);
        assertEquals("protocol should be a valid protocol", "http", url.getProtocol());
        assertEquals("protocol should be a valid host", "localhost", url.getHost());
        assertEquals("protocol should be a valid port", 9000, url.getPort());
        assertEquals("protocol should be a valid port", String.format(Locale.getDefault(),
                "/storage/%s/%s", TYPE, handle.getFileName()), url.getPath());
        assertNull("protocol should be a valid port", url.getQuery());
    }

    @Test
    public void writeTest() throws Exception {
        logger.debug("==> {}", new Object() {}.getClass().getEnclosingMethod().getName());

        File src = new File(getCurrentDir(PDF_FILE_1.path));
        FileHandle dst = fileSystem.open(TYPE, generateFileName());
        File dstFile = new File(dst.toURI());

        fileSystem.copy(getInputStream(src.toURI()), dst);

        assertNotNull("should have a file instance", dst);
        assertTrue("should have created a file", dstFile.exists());
        assertEquals("Content of the files must be equal", readLines(src), readLines(dstFile));

        File newSrc = new File(getCurrentDir(PDF_FILE_2.path));

        fileSystem.copy(getInputStream(newSrc.toURI()), dst);

        assertNotNull("should have a file instance", dst);
        assertTrue("should have created a file", dstFile.exists());
        assertEquals("Content of the files must be equal", readLines(newSrc), readLines(dstFile));
    }

    @Test
    public void deleteFileTest() throws Exception {
        logger.debug("==> {}", new Object() {}.getClass().getEnclosingMethod().getName());

        File src = new File(getCurrentDir(PDF_FILE_1.path));
        FileHandle dst = fileSystem.open(TYPE, generateFileName());
        File dstFile = new File(dst.toURI());

        fileSystem.copy(getInputStream(src.toURI()), dst);

        assertNotNull("should have a file instance", dst);
        assertTrue("should have created a file", dstFile.exists());

        fileSystem.delete(dst);

        assertNotNull("should have a file instance", dst);
        assertFalse("should have deleted the file", dstFile.exists());
    }
}
