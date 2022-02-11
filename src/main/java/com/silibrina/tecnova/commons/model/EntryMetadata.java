package com.silibrina.tecnova.commons.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mongodb.WriteResult;
import com.silibrina.tecnova.commons.model.file.FileMetadata;
import com.silibrina.tecnova.commons.model.file.FileVersion;
import org.bson.types.ObjectId;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.silibrina.tecnova.commons.model.EntryMetadata.Status.TO_INDEX;
import static com.silibrina.tecnova.commons.model.file.FileMetadata.VERSIONS;
import static com.silibrina.tecnova.commons.utils.Preconditions.*;

/**
 * This class models the metadata that an entry will have in our system.
 * An entry can be created without a file and thus it's metadata attributes.
 */
public class EntryMetadata extends Model<EntryMetadata> {
    public static final String ID = "_id";
    public static final String AUTHOR = "author";
    public static final String ORG = "org";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String UPLOADER = "uploader";
    public static final String UPDATED_AT = "updated_at";
    public static final String CREATED_AT = "created_at";
    public static final String PERIOD_START = "period_start";
    public static final String PERIOD_END = "period_end";
    public static final String FILE_METADATA = "file_metadata";
    public static final String STATUS = "status";
    public static final String CONTENT = "content";

    public static final int DESCRIPTION_MAX_SIZE = 2048;

    private String author;
    private String org;
    private String title;
    private String description;
    private String uploader;

    private Date createdAt;
    private Date updatedAt;
    private Date startPeriod;
    private Date endPeriod;

    private Status status;

    private FileMetadata fileMetadata;

    public EntryMetadata(ObjectId _id, String author, String org, String title, String description, String uploader,
                         Date createdAt, Date updatedAt, Date startPeriod, Date endPeriod, Status status,
                         FileMetadata fileMetadata) throws ParseException {
        super(_id);

        setAuthor(author);
        setOrg(org);
        setTitle(title);
        setDescription(description);
        setUploader(uploader);
        setCreatedAt(createdAt);
        setUpdatedAt(updatedAt);
        setStartPeriod(startPeriod);
        setEndPeriod(endPeriod);
        setStatus(status);
        setFileMetadata(fileMetadata);
    }

    public EntryMetadata() {
        super(null);
    }

    public void setStatus(Status status) {
        checkNotNullCondition("status can not be null", status);

        this.status = status;
    }

    @JsonProperty(STATUS)
    public Status getStatus() {
        return status;
    }

    @JsonProperty(AUTHOR)
    @Nonnull
    public String getAuthor() {
        return author;
    }

    private void setAuthor(final String author) {
        checkValidString("Author can not be null", author);
        this.author = author;
    }

    @JsonIgnore
    private void setAuthor(final JsonNode body) {
        setAuthor(getField(body, AUTHOR, author));
    }

    @JsonProperty(PERIOD_START)
    @Nonnull
    public Date getStartPeriod() {
        return startPeriod;
    }

    private void setStartPeriod(final Date startPeriod) throws ParseException {
        checkNotNullCondition("Start period can not be null", startPeriod);
        this.startPeriod = getCalMin(startPeriod);
    }

    @JsonIgnore
    private void setStartPeriod(final JsonNode body) throws ParseException {
        setStartPeriod(getField(body, PERIOD_START, startPeriod));
    }

    @JsonProperty(PERIOD_END)
    @Nonnull
    public Date getEndPeriod() {
        return endPeriod;
    }

    private void setEndPeriod(final Date endPeriod) throws ParseException {
        checkNotNullCondition("End period can not be null", endPeriod);
        this.endPeriod = getCalMax(endPeriod);
    }

    @JsonIgnore
    private void setEndPeriod(final JsonNode body) throws ParseException {
        setEndPeriod(getField(body, PERIOD_END, endPeriod));
    }

    @JsonProperty(ORG)
    @Nonnull
    public String getOrg() {
        return org;
    }

    private void setOrg(final String org) {
        checkValidString("Org can not be null", org);
        this.org = org;
    }

    @JsonIgnore
    private void setOrg( final JsonNode body) {
        setOrg(getField(body, ORG, org));
    }

    @JsonProperty(DESCRIPTION)
    public String getDescription() {
        return description;
    }

    private void setDescription(final String description) {
        checkOptionalValidString(String.format(Locale.getDefault(),
                "Description must be not null and have maximum length of %d",
                        DESCRIPTION_MAX_SIZE),
                description, DESCRIPTION_MAX_SIZE);
        this.description = description;
    }

    @JsonIgnore
    private void setDescription(final JsonNode body) {
        setDescription(getField(body, DESCRIPTION, description));
    }

    @JsonProperty(TITLE)
    @Nonnull
    public String getTitle() {
        return title;
    }

    private void setTitle(final String title) {
        checkValidString("Title can not be null", title);
        this.title = title;
    }

    @JsonIgnore
    private void setTitle(final JsonNode body) {
        setTitle(getField(body, TITLE, title));
    }

    @JsonProperty(UPLOADER)
    @Nonnull
    public String getUploader() {
        return uploader;
    }

    private void setUploader(final String uploader) {
        checkValidString("Uploader can not be null", uploader);
        this.uploader = uploader;
    }

    @JsonIgnore
    private void setUploader(final JsonNode body) {
        setUploader(getField(body, UPLOADER, uploader));
    }

    @JsonProperty(CREATED_AT)
    @Nonnull
    public Date getCreatedAt() {
        return createdAt;
    }

    private void setCreatedAt(Date createdAt) {
        checkNotNullCondition("createdAt date can not be null", createdAt);
        this.createdAt = createdAt;
    }
    @JsonProperty(UPDATED_AT)
    @Nonnull
    public Date getUpdatedAt() {
        return updatedAt;
    }

    private void setUpdatedAt(Date updatedAt) {
        checkNotNullCondition("updatedAt date can not be null", updatedAt);
        this.updatedAt = updatedAt;
    }

    @JsonProperty(FILE_METADATA)
    public FileMetadata getFileMetadata() {
        return fileMetadata;
    }

    public void setFileMetadata(FileMetadata fileMetadata) {
        this.fileMetadata = fileMetadata;
    }

    private static Date getCalMax(Date maxDate) throws ParseException {
        Calendar calMaxDate = getCalendarDate(maxDate);
        calMaxDate.set(Calendar.HOUR_OF_DAY, 23);
        calMaxDate.set(Calendar.MINUTE, 59);
        calMaxDate.set(Calendar.SECOND, 59);
        calMaxDate.set(Calendar.MILLISECOND, 0);
        return calMaxDate.getTime();
    }

    private static Date getCalMin(Date minDate) throws ParseException {
        Calendar calMinDate = getCalendarDate(minDate);
        calMinDate.set(Calendar.HOUR_OF_DAY, 0);
        calMinDate.set(Calendar.MINUTE, 0);
        calMinDate.set(Calendar.SECOND, 0);
        calMinDate.set(Calendar.MILLISECOND, 0);
        return calMinDate.getTime();
    }

    private static Calendar getCalendarDate(Date dateSource) throws ParseException {
        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTime(dateSource);
        return dateCalendar;
    }

    /**
     * Updates information (body and attached file) of an entry.
     * Leave it null to not update body information or attached file.
     *
     * @param body general information about the entry. Leave it null
     *             (or any field) to not update it.
     *
     * @throws IOException if some error occurs while dealing with the
     *                      new file.
     * @throws ParseException if some error occurs while extracting date
     *                      from parameters. Note that date must be in the
     *                      format: dd-MM-yyyy
     */
    public void updateEntry(JsonNode body) throws IOException, ParseException {
        if(body != null && !body.isNull() && body.size() > 0) {
            setTitle(body);
            setAuthor(body);
            setDescription(body);
            setOrg(body);
            setUploader(body);
            setStartPeriod(body);
            setEndPeriod(body);
            setUpdatedAt(Calendar.getInstance().getTime());
        }
    }

    private String getField(JsonNode body, String field, String defaultValue) {
        return body.has(field) ? body.get(field).asText() : defaultValue;
    }

    private Date getField(JsonNode body, String field, Date defaultValue) throws ParseException {
        return body.has(field) ? extractDate(body.get(field).asText()) : defaultValue;
    }

    /**
     * Builds an object from the body (json) and the given file.
     *
     * @param body json body to extract the information. (mandatory)
     *
     * @return An instance with the given information.
     *
     * @throws IOException if some error occurs while dealing with the
     *                      new file.
     * @throws ParseException if some error occurs while extracting date
     *                      from parameters. Note that date must be in the
     *                      format: dd-MM-yyyy
     */
    public static EntryMetadata buildEntryMetaData(JsonNode body) throws IOException, ParseException {
        checkNotNullCondition("body can not be null", body);

        String title = body.has(TITLE) ? body.get(TITLE).asText() : null;
        String description = body.has(DESCRIPTION) ? body.get(DESCRIPTION).asText() : null;
        String author = body.has(AUTHOR) ? body.get(AUTHOR).asText() : null;
        String org = body.has(ORG) ? body.get(ORG).asText() : null;
        String uploader = body.has(UPLOADER) ? body.get(UPLOADER).asText() : null;

        Date createdAt = Calendar.getInstance().getTime();
        Date periodStart = body.has(PERIOD_START) ? extractDate(body.get(PERIOD_START).asText()) : null;
        Date periodEnd = body.has(PERIOD_END) ? extractDate(body.get(PERIOD_END).asText()) : null;

        return new EntryMetadata(null, author, org, title, description, uploader, createdAt,
                createdAt, periodStart, periodEnd, TO_INDEX, null);
    }

    private static Date extractDate(String date) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        return formatter.parse(date);
    }

    private static String formatDate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        return formatter.format(date);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EntryMetadata)) return false;

        EntryMetadata that = (EntryMetadata) o;

        return _id().equals(that._id());

    }

    @Override
    public int hashCode() {
        return _id().hashCode();
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(),
                "%s [_id: %s, author: %s, org: %s, title: %s, uploader: %s, " +
                        "startPeriod: %s, endPeriod: %s, createdAt: %s, updatedAt: %s, status; %s, fileMetadata: %s]",
                this.getClass().getSimpleName(), _id(), author, org, title, uploader,
                startPeriod, endPeriod, createdAt, updatedAt, status, fileMetadata);
    }

    @Override
    public JsonNode toJson(ObjectNode body) {
        body.put(ID, String.valueOf(_id()));
        body.put(AUTHOR, getAuthor());
        body.put(ORG, getOrg());
        body.put(TITLE, getTitle());
        body.put(DESCRIPTION, getDescription());
        body.put(UPLOADER, getUploader());

        body.put(CREATED_AT, String.valueOf(getCreatedAt()));
        body.put(UPDATED_AT, String.valueOf(getUpdatedAt()));
        body.put(PERIOD_START, formatDate(getStartPeriod()));
        body.put(PERIOD_END, formatDate(getEndPeriod()));
        body.put(STATUS, String.valueOf(getStatus()));

        if (getFileMetadata() != null) {
            getFileMetadata().toJson(body.putObject(FILE_METADATA));
        } else {
            body.putNull(FILE_METADATA);
        }
        return body;
    }

    @Override
    public WriteResult save() {
        checkValidString("Author can not be null", author);
        checkValidString("Org can not be null", org);
        checkValidString("Title can not be null", title);
        checkOptionalValidString("Description must have maximum length of " + DESCRIPTION_MAX_SIZE,
                description, DESCRIPTION_MAX_SIZE);
        checkValidString("Uploader can not be null", uploader);
        checkNotNullCondition("createdAt can not be null", createdAt);
        checkNotNullCondition("updatedAt can not be null", updatedAt);
        checkNotNullCondition("Start period can not be null", startPeriod);
        checkNotNullCondition("End period can not be null", endPeriod);
        checkNotNullCondition("Status can not be null", status);
        checkCondition("Start date can not be after end date", getStartPeriod().compareTo(getEndPeriod()) < 1);
        return super.save();
    }

    public WriteResult updateStatus() {
        checkNotNullCondition("Status can not be null", getStatus());

        return super.update("{$set : { status : #}}", getStatus());
    }

    public WriteResult updateFileFormat(FileVersion fileVersion) {
        String modifier = String.format(Locale.getDefault(),
                "{$set : { file_metadata.%s.%s : #}}",
                VERSIONS, fileVersion.getFormat());
        WriteResult writeResult = super.update(modifier, fileVersion);
        if (writeResult.wasAcknowledged()) {
            fileMetadata.putVersion(fileVersion.getFormat(), fileVersion);
        }
        return writeResult;
    }

    public enum Status {
        TO_INDEX,
        DELETED,
        READY,
        MISSING_FILE,
        ALL
    }

}
