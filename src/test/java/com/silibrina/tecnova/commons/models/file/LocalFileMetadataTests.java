package com.silibrina.tecnova.commons.models.file;

import com.silibrina.tecnova.commons.model.EntryMetadata;
import com.silibrina.tecnova.commons.model.db.MongoDBPersistenceDrive;
import com.silibrina.tecnova.commons.model.file.FileMetadata;
import com.silibrina.tecnova.commons.model.file.FileVersion;
import com.silibrina.tecnova.commons.utils.ConstantUtils.FilesPathUtils;
import com.silibrina.tecnova.commons.utils.ODFileUtils;
import com.typesafe.config.ConfigFactory;
import org.apache.commons.configuration.ConfigurationException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.libs.Json;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static com.silibrina.tecnova.commons.utils.ConstantUtils.FilesPathUtils.PDF_FILE_1;
import static com.silibrina.tecnova.commons.utils.TestsUtils.createEntry;
import static com.silibrina.tecnova.commons.utils.TestsUtils.generateBody;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class LocalFileMetadataTests {
    private static final Logger logger = LoggerFactory.getLogger(LocalFileMetadataTests.class);

    @Before
    public void setUp() throws ConfigurationException {
        logger.debug("==> {}", new Object(){}.getClass().getEnclosingMethod().getName());
        new MongoDBPersistenceDrive(ConfigFactory.load()).getCollection(EntryMetadata.class).drop();
        logger.debug("<== {} finished", new Object(){}.getClass().getEnclosingMethod().getName());
    }

    @After
    public void cleanUp() {
        logger.debug("==> {}", new Object(){}.getClass().getEnclosingMethod().getName());
        new MongoDBPersistenceDrive(ConfigFactory.load()).getCollection(EntryMetadata.class).drop();
        logger.debug("<== {} finished", new Object(){}.getClass().getEnclosingMethod().getName());
    }

    @Test
    public void storeAndRetrieveTest() throws Exception {
        logger.debug("==> {}", new Object(){}.getClass().getEnclosingMethod().getName());

        EntryMetadata entry1 = createEntry(generateBody(3, 0, 0), null);
        FileMetadata fileMetadata = getLocalFileMetadata(entry1);
        entry1.setFileMetadata(fileMetadata);
        entry1.save();

        EntryMetadata entryFromDB = new EntryMetadata().find(entry1._id());
        assertNotNull("should not be null", entryFromDB);
        assertEquals("should have the same file metadata",
                entry1.getFileMetadata().toJson(Json.newObject()),
                entryFromDB.getFileMetadata().toJson(Json.newObject()));
    }

    // FIXME: Remove-me - TestUtils has methods for this
    private FileMetadata getLocalFileMetadata(EntryMetadata entry) throws IOException, URISyntaxException {
        FilesPathUtils pdfFile1 = PDF_FILE_1;
        File file = new File(ODFileUtils.getCurrentDir(pdfFile1.path));
        String name = file.getName();
        String originalName = "someName.pdf";
        URI uri = file.toURI();
        String url = "/data/" + entry._id();

        FileVersion fileVersion = new FileVersion(pdfFile1.format, pdfFile1.size, pdfFile1.hash,
                uri, url, pdfFile1.mimeType, pdfFile1.subType, pdfFile1.type);

        Map<String, FileVersion> fileVersions = new HashMap<>();
        fileVersions.put(fileVersion.getFormat(), fileVersion);

        return new FileMetadata(originalName, name, fileVersions);
    }
}
