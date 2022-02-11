package com.silibrina.tecnova.commons.fs;

import com.silibrina.tecnova.commons.exceptions.InvalidConditionException;
import com.silibrina.tecnova.commons.fs.swift.SwiftFileSystem;
import com.silibrina.tecnova.commons.model.EntryMetadata;
import com.silibrina.tecnova.commons.model.file.FileMetadata;
import com.silibrina.tecnova.commons.model.file.FileVersion;
import org.apache.commons.configuration.ConfigurationException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

import static com.silibrina.tecnova.commons.utils.CommonsFileUtils.getCurrentDir;
import static com.silibrina.tecnova.commons.utils.ConstantUtils.FilesPathUtils.TXT_FILE_1;
import static com.silibrina.tecnova.commons.utils.ConstantUtils.FilesPathUtils.XLSX_FILE_1;
import static com.silibrina.tecnova.commons.utils.TestsUtils.*;
import static org.junit.Assert.assertNotNull;

@RunWith(Parameterized.class)
public class FileSystemTests {
    private static final Logger logger = LoggerFactory.getLogger(FileSystemTests.class);

    private final static String TYPE = "original";

    private final FileSystem fileSystem;

    public FileSystemTests(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    @Before
    public void setUp() throws Exception {
        logger.debug("==> {}", new Object() {}.getClass().getEnclosingMethod().getName());

        cleanUpEntries();

        logger.debug("<== {} finished", new Object(){}.getClass().getEnclosingMethod().getName());
    }

    @After
    public void cleanUp() throws IOException, ConfigurationException {
        logger.debug("==> {}", new Object(){}.getClass().getEnclosingMethod().getName());

        if (fileSystem instanceof SwiftFileSystem) {
            cleanUpSwift((SwiftFileSystem) fileSystem);
        }
        cleanUpEntries();

        logger.debug("<== {} finished", new Object(){}.getClass().getEnclosingMethod().getName());
    }

    @Parameters
    public static FileSystem[] fileSystems() throws ConfigurationException {
        return getFileSystemsToTest();
    }

    @Test(expected = InvalidConditionException.class)
    public void openWithNullTypeTest() throws IOException {
        logger.debug("==> {}", new Object(){}.getClass().getEnclosingMethod().getName());

        fileSystem.open(null, "fileName");
    }

    @Test(expected = InvalidConditionException.class)
    public void openWithEmptyTypeTest() throws IOException {
        logger.debug("==> {}", new Object(){}.getClass().getEnclosingMethod().getName());

        fileSystem.open(" ", "fileName");
    }

    @Test(expected = InvalidConditionException.class)
    public void openWithNullFileNameTest() throws IOException {
        logger.debug("==> {}", new Object(){}.getClass().getEnclosingMethod().getName());

        fileSystem.open("type", null);
    }

    @Test(expected = InvalidConditionException.class)
    public void openWithEmptyFileNameTest() throws IOException {
        logger.debug("==> {}", new Object(){}.getClass().getEnclosingMethod().getName());

        fileSystem.open("type", " ");
    }

    @Test(expected = InvalidConditionException.class)
    public void mkdirWithEmptyPathTest() throws IOException {
        logger.debug("==> {}", new Object(){}.getClass().getEnclosingMethod().getName());

        fileSystem.mkdir(" ");
    }

    @Test(expected = InvalidConditionException.class)
    public void mkdirWithNullPathTest() throws IOException {
        logger.debug("==> {}", new Object(){}.getClass().getEnclosingMethod().getName());

        fileSystem.mkdir(null);
    }

    @Test(expected = InvalidConditionException.class)
    public void copyWithNullSrcTest() throws IOException {
        logger.debug("==> {}", new Object(){}.getClass().getEnclosingMethod().getName());

        FileHandle handleDst = fileSystem.open(TYPE, generateFileName());
        FileHandle handleSrc = null;
        //noinspection ConstantConditions
        fileSystem.copy(handleSrc, handleDst);
    }

    @Test(expected = InvalidConditionException.class)
    public void copyWithNullDstTest() throws IOException {
        logger.debug("==> {}", new Object(){}.getClass().getEnclosingMethod().getName());

        FileHandle handleSrc = fileSystem.open(TYPE, generateFileName());
        fileSystem.copy(handleSrc, null);
    }

    @Test(expected = InvalidConditionException.class)
    public void streamCopyWithNullDstTest() throws IOException, URISyntaxException {
        logger.debug("==> {}", new Object(){}.getClass().getEnclosingMethod().getName());

        InputStream inputStream = new FileInputStream(new File(getCurrentDir(XLSX_FILE_1.path)));
        fileSystem.copy(inputStream, null);
    }

    @Test(expected = InvalidConditionException.class)
    public void streamCopyWithNullSrcTest() throws IOException, URISyntaxException {
        logger.debug("==> {}", new Object(){}.getClass().getEnclosingMethod().getName());

        FileHandle handleDst = fileSystem.open(TYPE, generateFileName());
        InputStream inputStream = null;
        //noinspection ConstantConditions
        fileSystem.copy(inputStream, handleDst);
    }

    @Test
    public void openFileVersionTest() throws Exception {
        logger.debug("==> {}", new Object(){}.getClass().getEnclosingMethod().getName());

        EntryMetadata entry = generateEntryMetadata(0, TXT_FILE_1);
        FileMetadata fileMetadata = entry.getFileMetadata();
        FileVersion fileVersion = fileMetadata.getVersion("original");

        FileHandle handle = fileSystem.open(fileVersion);

        assertNotNull("handle should not be null", handle);
    }
}
