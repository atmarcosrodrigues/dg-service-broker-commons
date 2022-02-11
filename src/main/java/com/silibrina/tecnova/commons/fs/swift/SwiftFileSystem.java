package com.silibrina.tecnova.commons.fs.swift;

import com.silibrina.tecnova.commons.fs.FileHandle;
import com.silibrina.tecnova.commons.fs.FileSystem;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.openstack4j.api.OSClient;
import org.openstack4j.api.storage.ObjectStorageContainerService;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.common.Identifier;
import org.openstack4j.model.common.Payloads;
import org.openstack4j.model.storage.object.options.CreateUpdateContainerOptions;
import org.openstack4j.openstack.OSFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Locale;

import static com.silibrina.tecnova.commons.conf.ConfigConstants.Strings.*;
import static com.silibrina.tecnova.commons.utils.CommonsFileUtils.getInputStream;
import static com.silibrina.tecnova.commons.utils.Preconditions.checkNotNullCondition;
import static com.silibrina.tecnova.commons.utils.Preconditions.checkValidString;

/**
 * File system for swift storage.
 */
public class SwiftFileSystem extends FileSystem {
    private static final Logger logger = LoggerFactory.getLogger(SwiftFileSystem.class);

    private final Config config;

    private final String username;
    private final String password;
    private final String domainName;
    private final String projectId;
    private final String endpoint;
    private final String baseUrl;
    private final String container;

    public SwiftFileSystem() {
        config = ConfigFactory.load();

        username = getUser();
        password = getPassword();
        domainName = getDomainName();
        projectId = getProjectId();
        endpoint = getEndpoint();
        baseUrl = getSwiftEndpoint(projectId);
        container = getContainerName();
    }

    @Override
    public void createBasedir() throws IOException {
        String containerName = getContainerName();

        logger.debug("containerName: {}", containerName);

        OSClient.OSClientV3 client = getClient();

        ObjectStorageContainerService containers = client.objectStorage().containers();
        ActionResponse container = containers.create(containerName, CreateUpdateContainerOptions
                .create().accessAnybodyRead());

        if (!container.isSuccess()) {
            logger.error("code: {}, fault: {}, is_success: {}",
                    container.getCode(), container.getFault(), container.isSuccess());
            throw new IOException("An error occurred while trying to create the container");
        }
    }

    @Override
    public void mkdir(String path) throws IOException {
        logger.debug("path: {}", path);

        checkValidString("path can not be null", path);

        OSClient.OSClientV3 client = getClient();

        ObjectStorageContainerService objects = client.objectStorage().containers();
        objects.createPath(getContainerName(), path);
    }

    @Override
    public String copy(FileHandle src, FileHandle dst) throws IOException {
        logger.debug("src: {}, dst: {}", src, dst);

        checkNotNullCondition("open src target file first (src handle was null)", src);
        checkNotNullCondition("open dst target file first (dst handle was null)", dst);

        try {
            try (InputStream srcInputStream = getInputStream(src.toURI())) {
                return copy(srcInputStream, dst);
            }
        } catch (URISyntaxException e) {
            logger.error("an error occurred while trying to mount uri from handle ({})", src, e);
            throw new IOException(e);
        }
    }

    @Override
    public String copy(InputStream srcInputStream, FileHandle dst) throws IOException {
        logger.debug("srcInputStream: {}, dst: {}", srcInputStream, dst);

        checkNotNullCondition("srcInputStream can not be null", srcInputStream);
        checkNotNullCondition("open dst target file first (dst handle was null)", dst);

        String localPath = dst.getLocalPath();

        OSClient.OSClientV3 client = getClient();

        String checksum = client.objectStorage().objects()
                .put(container, localPath, Payloads.create(srcInputStream));
        logger.debug("dst result - checksum: {}", checksum);

        if (checksum == null || checksum.trim().isEmpty()) {
            logger.error("An error occurred while uploading file");
            throw new IOException("An error occurred while trying to upload the file");
        }

        return checksum.toLowerCase();
    }

    @Override
    public void delete(FileHandle handle) throws IOException {
        logger.debug("handle: {}", handle);

        checkNotNullCondition("open target file first (handle was null)", handle);

        OSClient.OSClientV3 client = getClient();

        ActionResponse result = client.objectStorage().objects().delete(container, handle.getLocalPath());

        if (!result.isSuccess()) {
            logger.error("code: {}, fault: {}, is_success: {}",
                    result.getCode(), result.getFault(), result.isSuccess());
            throw new IOException("An error occurred while trying to delete the object");
        }
    }

    @Override
    public FileHandle open(String type, String fileName) throws IOException {
        logger.debug("type: {}, fileName: {}", type, fileName);

        checkValidString("type can not be null", type);
        checkValidString("fileName can not be null", fileName);

        return new FileHandle(baseUrl, container, type, fileName);
    }

    @Override
    public URL toURL(FileHandle handle) throws MalformedURLException {
        logger.debug("handle: {}", handle);

        checkNotNullCondition("handle should not be null", handle);

        try {
            return handle.toURI().toURL();
        } catch (URISyntaxException e) {
            throw new MalformedURLException(e.getMessage());
        }
    }

    private String getEndpoint() {
        return config.getString(SWIFT_IDENTITY_ENDPOINT.field);
    }

    private String getPassword() {
        return config.getString(SWIFT_PASSWORD.field);
    }

    private String getUser() {
        return config.getString(SWIFT_USER.field);
    }

    private String getSwiftEndpoint(String projectId) {
        String swiftEndpoint = config.getString(SWIFT_ENDPOINT.field);
        return String.format(Locale.getDefault(),
                "%s%s", swiftEndpoint, projectId);
    }

    public String getContainerName() {
        return config.getString(SWIFT_CONTAINER.field);
    }

    private String getProjectId() {
        return config.getString(SWIFT_PROJECT_ID.field);
    }

    private String getDomainName() {
        return config.getString(SWIFT_DOMAIN_NAME.field);
    }

    private OSClient.OSClientV3 getClient() {
        return OSFactory.builderV3()
                .endpoint(endpoint)
                .credentials(username, password, Identifier.byName(domainName))
                .scopeToProject(Identifier.byId(projectId))
                .authenticate();
    }

    public OSClient.OSClientV3 getOS() {
        return getClient();
    }

}
