package com.silibrina.tecnova.commons.models;

import com.fasterxml.jackson.databind.JsonNode;
import com.silibrina.tecnova.commons.exceptions.InvalidConditionException;
import com.silibrina.tecnova.commons.fs.FileSystem;
import com.silibrina.tecnova.commons.model.EntryMetadata;
import com.silibrina.tecnova.commons.model.file.FileMetadata;
import com.silibrina.tecnova.commons.model.db.MongoDBPersistenceDrive;
import com.silibrina.tecnova.commons.model.file.FileVersion;
import com.typesafe.config.ConfigFactory;
import org.apache.commons.configuration.ConfigurationException;
import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;

import static com.silibrina.tecnova.commons.model.EntryMetadata.DESCRIPTION_MAX_SIZE;
import static com.silibrina.tecnova.commons.model.EntryMetadata.Status.READY;
import static com.silibrina.tecnova.commons.model.EntryMetadata.Status.TO_INDEX;
import static com.silibrina.tecnova.commons.utils.ConstantUtils.FilesPathUtils.*;
import static com.silibrina.tecnova.commons.utils.TestsUtils.*;
import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class EntryMetadataTests {
    private static final Logger logger = LoggerFactory.getLogger(EntryMetadataTests.class);

    private EntryMetadata entity1;
    private EntryMetadata entity2;

    private final FileSystem fileSystem;

    public EntryMetadataTests(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    @Before
    public void setUp() throws Exception {
        logger.debug("==> {}", new Object(){}.getClass().getEnclosingMethod().getName());

        new MongoDBPersistenceDrive(ConfigFactory.load()).getCollection(EntryMetadata.class).drop();

        entity1 = createEntry(generateBody(0, -1, 2), XLSX_FILE_1);
        entity1.save();

        entity2 = createEntry(generateBody(1, -5, 5), XLSX_FILE_1);
        entity2.save();

        EntryMetadata entity3 = createEntry(generateBody(2, -3, 12), XLSX_FILE_1);
        entity3.save();
        logger.debug("<== {} finished", new Object(){}.getClass().getEnclosingMethod().getName());
    }

    @After
    public void cleanUp() {
        logger.debug("==> {}", new Object(){}.getClass().getEnclosingMethod().getName());

        new MongoDBPersistenceDrive(ConfigFactory.load()).getCollection(EntryMetadata.class).drop();
        logger.debug("<== {} finished", new Object(){}.getClass().getEnclosingMethod().getName());
    }

    @Parameterized.Parameters
    public static FileSystem[] fileSystems() throws ConfigurationException {
        return getFileSystemsToTest();
    }

    @Test
    public void findTest() {
        logger.debug("==> {}", new Object(){}.getClass().getEnclosingMethod().getName());

        List<EntryMetadata> result = new EntryMetadata().find();
        assertNotNull("should have found entries", result);
        assertEquals("Should have found all entries", 3, result.size());

        EntryMetadata em = result.get(0);
        assertNotNull("Should have got an entry", em);
    }

    @Test
    public void findById() {
        logger.debug("==> {}", new Object(){}.getClass().getEnclosingMethod().getName());

        EntryMetadata result = new EntryMetadata().find(entity1._id().toHexString());
        assertNotNull("should have found an entry", result);
        assertEquals("should have found entity1", entity1, result);
        assertNotEquals("entity1 should not be equals to entity2", entity1, entity2);
    }

    @Test
    public void buildFromJsonTest() throws Exception {
        logger.debug("==> {}", new Object(){}.getClass().getEnclosingMethod().getName());

        EntryMetadata entity1 = createEntry(generateBody(0, -1, 2), PDF_FILE_1);

        JsonNode entityJson = entity1.toJson();
        EntryMetadata entity2 = EntryMetadata.buildEntryMetaData(entityJson);

        assertEquals("Should be the same author", entity1.getAuthor(), entity2.getAuthor());
        assertEquals("Should be the same org", entity1.getOrg(), entity2.getOrg());
        assertEquals("Should be the same title", entity1.getTitle(), entity2.getTitle());
        assertEquals("Should be the same uploader", entity1.getUploader(), entity2.getUploader());
        assertNotNull("Should have some createdAt", entity2.getCreatedAt());
        assertNotNull("Should have some updatedAt", entity2.getUpdatedAt());
        assertEquals("Should be the same start date", entity1.getStartPeriod().getTime()/1000, entity2.getStartPeriod().getTime()/1000);
        assertEquals("Should be the same end date", entity1.getEndPeriod().getTime()/1000, entity2.getEndPeriod().getTime()/1000);
    }

    @Test
    public void nullObjectIdTest() throws ParseException {
        logger.debug("==> {}", new Object(){}.getClass().getEnclosingMethod().getName());

        new EntryMetadata(null, "author", "org", "title", "description", "uploader", new Date(),
                new Date(), new Date(), new Date(), TO_INDEX, null);
    }

    @Test(expected = Exception.class)
    public void nullAuthorTest() throws ParseException {
        logger.debug("==> {}", new Object(){}.getClass().getEnclosingMethod().getName());

        new EntryMetadata(ObjectId.get(), null, "org", "title", "description", "uploader", new Date(),
                new Date(), new Date(), new Date(), TO_INDEX, null);
    }

    @Test(expected = InvalidConditionException.class)
    public void emptyAuthorTest() throws ParseException {
        logger.debug("==> {}", new Object(){}.getClass().getEnclosingMethod().getName());

        new EntryMetadata(ObjectId.get(), "  ", "org", "title", "description", "uploader", new Date(),
                new Date(), new Date(), new Date(), TO_INDEX, null);
    }

    @Test(expected = Exception.class)
    public void nullOrgTest() throws ParseException {
        logger.debug("==> {}", new Object(){}.getClass().getEnclosingMethod().getName());

        new EntryMetadata(ObjectId.get(), "author", null, "title", "description", "uploader", new Date(),
                new Date(), new Date(), new Date(), TO_INDEX, null);
    }

    @Test(expected = InvalidConditionException.class)
    public void emptyOrgTest() throws ParseException {
        logger.debug("==> {}", new Object(){}.getClass().getEnclosingMethod().getName());

        new EntryMetadata(ObjectId.get(), "author", "  ", "title", "description", "uploader", new Date(),
                new Date(), new Date(), new Date(), TO_INDEX, null);
    }

    @Test(expected = Exception.class)
    public void nullTitleTest() throws ParseException {
        logger.debug("==> {}", new Object(){}.getClass().getEnclosingMethod().getName());

        new EntryMetadata(ObjectId.get(), "author", "org", null, "description", "uploader", new Date(),
                new Date(), new Date(), new Date(), TO_INDEX, null);
    }

    @Test(expected = InvalidConditionException.class)
    public void emptyTitleTest() throws ParseException {
        logger.debug("==> {}", new Object(){}.getClass().getEnclosingMethod().getName());

        new EntryMetadata(ObjectId.get(), "author", "org", "  ", "description", "uploader", new Date(),
                new Date(), new Date(), new Date(), TO_INDEX, null);
    }

    @Test
    public void nullOrEmptyDescriptionTest() throws ParseException {
        logger.debug("==> {}", new Object() {}.getClass().getEnclosingMethod().getName());

        EntryMetadata entry1 = new EntryMetadata(ObjectId.get(), "author", "org", "title", " ", "uploader", new Date(),
                new Date(), new Date(), new Date(), TO_INDEX, null);

        assertNotNull("should have a description", entry1.getDescription());

        entry1 = new EntryMetadata(ObjectId.get(), "author", "org", "title", null, "uploader", new Date(),
                new Date(), new Date(), new Date(), TO_INDEX, null);

        assertNull("should have a description", entry1.getDescription());
    }

    @Test(expected = InvalidConditionException.class)
    public void maxSizeDescription() throws ParseException {
        logger.debug("==> {}", new Object(){}.getClass().getEnclosingMethod().getName());

        String description = "";
        for (int i = 0; i <= DESCRIPTION_MAX_SIZE; i++) {
            description += "x";
        }
        new EntryMetadata(ObjectId.get(), "author", "org", "title", description, "uploader", new Date(),
                new Date(), new Date(), new Date(), TO_INDEX, null);
    }

    @Test(expected = Exception.class)
    public void nullUploaderTest() throws ParseException {
        logger.debug("==> {}", new Object(){}.getClass().getEnclosingMethod().getName());

        new EntryMetadata(ObjectId.get(), "author", "org", "title", "description", null, new Date(),
                new Date(), new Date(), new Date(), TO_INDEX, null);
    }

    @Test(expected = InvalidConditionException.class)
    public void emptyUploaderTest() throws ParseException {
        logger.debug("==> {}", new Object(){}.getClass().getEnclosingMethod().getName());

        new EntryMetadata(ObjectId.get(), "author", "org", "title", "description", "  ", new Date(),
                new Date(), new Date(), new Date(), TO_INDEX, null);
    }

    @Test(expected = InvalidConditionException.class)
    public void nullCreatedAtDateTest() throws ParseException {
        logger.debug("==> {}", new Object(){}.getClass().getEnclosingMethod().getName());

        new EntryMetadata(ObjectId.get(), "author", "org", "title", "description", "uploader", null,
                new Date(), new Date(), new Date(), TO_INDEX, null);
    }

    @Test(expected = InvalidConditionException.class)
    public void nullUpdatedAtDateTest() throws ParseException {
        logger.debug("==> {}", new Object(){}.getClass().getEnclosingMethod().getName());

        new EntryMetadata(ObjectId.get(), "author", "org", "title", "description", "uploader", new Date(),
                null, new Date(), new Date(), TO_INDEX, null);
    }

    @Test(expected = Exception.class)
    public void nullStartDateTest() throws ParseException {
        logger.debug("==> {}", new Object(){}.getClass().getEnclosingMethod().getName());

        new EntryMetadata(ObjectId.get(), "author", "org", "title", "description", "uploader", new Date(),
                new Date(), null, new Date(), TO_INDEX, null);
    }

    @Test(expected = Exception.class)
    public void nullEndDateTest() throws ParseException {
        logger.debug("==> {}", new Object(){}.getClass().getEnclosingMethod().getName());

        new EntryMetadata(ObjectId.get(), "author", "org", "title", "description", "uploader", new Date(),
                new Date(), new Date() , null, TO_INDEX, null);
    }

    @Test(expected = InvalidConditionException.class)
    public void nullStatusTest() throws ParseException {
        logger.debug("==> {}", new Object(){}.getClass().getEnclosingMethod().getName());

        new EntryMetadata(ObjectId.get(), "author", "org", "title", "description", "uploader", new Date(),
                new Date(), new Date(), new Date(), null, null);
    }

    @Test
    public void validConstructor() throws Exception {
        logger.debug("==> {}", new Object(){}.getClass().getEnclosingMethod().getName());

        new EntryMetadata(ObjectId.get(), "author", "org", "title",
                "description", "uploader", new Date(),
                new Date(), new Date(), new Date(), TO_INDEX, generateFileMetadata(PDF_FILE_1, fileSystem));
    }

    @Test
    public void saveAndRemoveTest() throws IOException, ParseException {
        logger.debug("==> {}", new Object(){}.getClass().getEnclosingMethod().getName());

        EntryMetadata entry1 = new EntryMetadata(null, "author", "org",
                "title", "description", "uploader",
                new Date(), new Date(), new Date(), new Date(), TO_INDEX, null);
        assertNull("id should be null", entry1._id());
        assertNull("file metadata should be null", entry1.getFileMetadata());

        entry1.save();
        assertNotNull("should have gotten an id", entry1._id());
        EntryMetadata entry2 = new EntryMetadata().find(entry1._id());
        assertEquals("should have retrieved the same entry", entry1, entry2);
        assertNull("file metadata should be null", entry2.getFileMetadata());

        entry2.remove();
        EntryMetadata entry3 = new EntryMetadata().find(entry1._id());
        assertNull("should have removed the entry", entry3);
    }

    @Test
    public void updateStatusTest() throws ParseException {
        logger.debug("==> {}", new Object(){}.getClass().getEnclosingMethod().getName());

        EntryMetadata entry1 = new EntryMetadata(null, "author", "org",
                "title", "description", "uploader", new Date(),
                new Date(), new Date(), new Date(), TO_INDEX, null);
        assertNull("id should be null", entry1._id());
        assertNull("file metadata should be null", entry1.getFileMetadata());
        entry1.save();

        EntryMetadata entry2 = new EntryMetadata().find(entry1._id());
        assertNotNull("should have found entry1", entry2);
        assertEquals("should be the same entry", entry1, entry2);
        assertEquals("should have the same status", entry1.getStatus(), entry2.getStatus());
        assertEquals("should have to index status", TO_INDEX, entry2.getStatus());

        entry2.setStatus(READY);
        entry2.updateStatus();

        EntryMetadata entry3 = new EntryMetadata().find(entry1._id());
        assertNotNull("should have found entry1", entry3);
        assertEquals("should be the same entry", entry1, entry3);
        assertNotEquals("should have the same status", entry1.getStatus(), entry3.getStatus());
        assertEquals("should have to index status", READY, entry3.getStatus());
    }

    @Test
    public void updatePathsTest() throws Throwable {
        logger.debug("==> {}", new Object(){}.getClass().getEnclosingMethod().getName());

        EntryMetadata entity = generateEntryMetadata(0, 0, 0, TXT_FILE_1, fileSystem);
        entity.save();

        EntryMetadata fromDB = new EntryMetadata().find(entity._id());
        assertEquals("must have 1 available format (original)", 1, fromDB.getFileMetadata().getVersionsAsList().size());

        FileVersion txtFileVersion = generateFileVersion(TXT_FILE_1, fileSystem, "original");
        entity.updateFileFormat(txtFileVersion);
        fromDB = new EntryMetadata().find(entity._id());
        assertEquals("must have 2 available format", 2, fromDB.getFileMetadata().getVersionsAsList().size());
        assertEquals("should be the new FileVersion", txtFileVersion,
                fromDB.getFileMetadata().getVersionsAsList().get(1));

        FileVersion xmlFileVersion = generateFileVersion(XML_FILE_1, fileSystem, "original");
        entity.updateFileFormat(xmlFileVersion);

        fromDB = new EntryMetadata().find(entity._id());
        assertEquals("must have 3 available format", 3, fromDB.getFileMetadata().getVersionsAsList().size());
        assertEquals("should be the new txt FileVersion", txtFileVersion,
                fromDB.getFileMetadata().getVersion(txtFileVersion.getFormat()));
        assertEquals("should be the xml FileVersion", xmlFileVersion,
                fromDB.getFileMetadata().getVersion(xmlFileVersion.getFormat()));
    }

    @Test
    public void statusIsToIndexWhenAddingFile() throws Exception {
        logger.debug("==> {}", new Object(){}.getClass().getEnclosingMethod().getName());

        EntryMetadata entry1 = new EntryMetadata(null, "author", "org", "title", "description", "uploader", new Date(),
                new Date(), new Date(), new Date(), TO_INDEX, generateFileMetadata(PDF_FILE_1, fileSystem));
        assertNull("id should be null", entry1._id());
        assertNotNull("file metadata should be null", entry1.getFileMetadata());
        entry1.save();

        EntryMetadata entry2 = new EntryMetadata().find(entry1._id());
        assertNotNull("should have found entry1", entry2);
        assertEquals("should be the same entry", entry1, entry2);
        assertEquals("should have the same status", entry1.getStatus(), entry2.getStatus());
        assertEquals("should have to index status", TO_INDEX, entry2.getStatus());

        entry2.setStatus(READY);
        entry2.updateStatus();

        EntryMetadata entry3 = new EntryMetadata().find(entry1._id());
        assertNotNull("should have found entry1", entry3);
        assertEquals("should be the same entry", entry1, entry3);
        assertNotEquals("should have the same status", entry1.getStatus(), entry3.getStatus());
        assertEquals("should have to index status", READY, entry3.getStatus());
    }

    @Test
    public void statusNotUpdatedAfterSaveTest() throws ParseException {
        logger.debug("==> {}", new Object(){}.getClass().getEnclosingMethod().getName());

        EntryMetadata entry1 = new EntryMetadata(null, "author", "org", "title", "description", "uploader", new Date(),
                new Date(), new Date(), new Date(), TO_INDEX, null);
        assertNull("id should be null", entry1._id());
        assertNull("file metadata should be null", entry1.getFileMetadata());
        entry1.save();

        EntryMetadata entry2 = new EntryMetadata().find(entry1._id());
        assertNotNull("should have found entry1", entry2);
        assertEquals("should be the same entry", entry1, entry2);
        assertEquals("should have the same status", entry1.getStatus(), entry2.getStatus());
        assertEquals("should have to index status", TO_INDEX, entry2.getStatus());

        entry2.setStatus(READY);
        entry2.save();

        EntryMetadata entry3 = new EntryMetadata().find(entry1._id());
        assertNotNull("should have found entry1", entry3);
        assertEquals("should be the same entry", entry1, entry3);
        assertEquals("should have the same status", entry2.getStatus(), entry3.getStatus());
        assertEquals("should have to index status", READY, entry3.getStatus());
    }

    @Test
    public void fileMetadataPersistenceTest() throws Exception {
        logger.debug("==> {}", new Object(){}.getClass().getEnclosingMethod().getName());

        FileMetadata fileMetadata = generateFileMetadata(PDF_FILE_1, fileSystem);
        EntryMetadata entry1 = new EntryMetadata(null, "author", "org", "title", "description", "uploader", new Date(),
                new Date(), new Date(), new Date(), TO_INDEX, fileMetadata);

        assertNull("id should be null", entry1._id());
        assertNotNull("file metadata should be not null", entry1.getFileMetadata());

        entry1.save();
        assertNotNull("should have gotten an id", entry1._id());
        EntryMetadata entry2 = new EntryMetadata().find(entry1._id());
        assertEquals("should have retrieved the same entry", entry1, entry2);
        assertNotNull("file metadata should be not null", entry2.getFileMetadata());
        assertEquals("should have retrieved the same file metadata", fileMetadata, entry2.getFileMetadata());
    }

    @Test(expected = InvalidConditionException.class)
    public void saveEmptyEntryMetadata() {
        logger.debug("==> {}", new Object(){}.getClass().getEnclosingMethod().getName());

        EntryMetadata entry = new EntryMetadata();
        entry.save();
    }

    @Test
    public void findByListOfIds() throws Exception {
        logger.debug("==> {}", new Object(){}.getClass().getEnclosingMethod().getName());

        EntryMetadata entity1 = createEntry(generateBody(0, 0, 0), XLSX_FILE_1);
        entity1.save();

        EntryMetadata entity2 = createEntry(generateBody(0, 0, 0), XLSX_FILE_1);
        entity2.save();

        Set<String> stringIds = new HashSet<>();
        stringIds.add(entity1._id().toHexString());
        stringIds.add(entity2._id().toHexString());

        List<EntryMetadata> result = new EntryMetadata().find(stringIds);
        assertNotNull("result can not be null", result);
        assertEquals("should have 2 entries", 2, result.size());
        assertTrue("must contain entity1", result.contains(entity1));
        assertTrue("must contain entity2", result.contains(entity2));
    }

    @Test
    public void findByListOfIdsAndDates() throws Exception {
        logger.debug("==> {}", new Object(){}.getClass().getEnclosingMethod().getName());

        EntryMetadata entity1 = createEntry(generateBody(0, 0, 0), XLSX_FILE_1);
        entity1.save();

        EntryMetadata entity2 = createEntry(generateBody(1, 0, 0), XLSX_FILE_1);
        entity2.save();

        EntryMetadata entity3 = createEntry(generateBody(2, 0, 0), XLSX_FILE_1);
        entity3.save();

        EntryMetadata entity4 = createEntry(generateBody(3, 0, 0), XLSX_FILE_1);
        entity4.save();

        Set<String> stringIds = new HashSet<>();
        stringIds.add(entity1._id().toHexString());
        stringIds.add(entity2._id().toHexString());

        List<EntryMetadata> result = new EntryMetadata().find(stringIds);
        assertNotNull("result can not be null", result);
        assertEquals("should have 2 entries", 2, result.size());
        assertTrue("must contain entity1", result.contains(entity1));
        assertTrue("must contain entity2", result.contains(entity2));
    }

}
