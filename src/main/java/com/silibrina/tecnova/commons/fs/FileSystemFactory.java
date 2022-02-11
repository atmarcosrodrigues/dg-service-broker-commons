package com.silibrina.tecnova.commons.fs;

import com.silibrina.tecnova.commons.exceptions.UnrecoverableErrorException;
import com.silibrina.tecnova.commons.fs.local.LocalFileSystem;
import com.silibrina.tecnova.commons.fs.swift.SwiftFileSystem;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import static com.silibrina.tecnova.commons.conf.ConfigConstants.Strings.GATHERER_FS;
import static com.silibrina.tecnova.commons.exceptions.ExitStatus.CONFIGURATION_ERROR_STATUS;

/**
 * Provides a file system instance capable of storing, retrieving and deleting
 * a file, based on the configuration section gatherer.fs.
 *
 * Examples of file system that this factory can provide include:
 * - local: files are stored locally (Also GlusterFS, Ceph, NFS etc.).
 *
 * Future:
 * - swift: files are stored in a swift instance.
 * - s3: amazon bucket for file storage.
 */
public class FileSystemFactory {
    /**
     * Gets a file system instance based on the configuration.
     *
     * @return the file system instance
     */
    public static FileSystem getFileSystem() {
        Config config = ConfigFactory.load();
        String type = config.getString(GATHERER_FS.field);

        return getFileSystem(type);
    }

    public static FileSystem getFileSystem(String fileSystem) {
        FileSystemType fileSystemType = getFileSystemType(fileSystem);
        switch (fileSystemType) {
            case LOCAL:
                return new LocalFileSystem();
            case SWIFT:
                return new SwiftFileSystem();
            default:
                throw new UnrecoverableErrorException("Unknown file system: " + fileSystemType, CONFIGURATION_ERROR_STATUS);

        }
    }

    private static FileSystemType getFileSystemType(String type) {
        try {
            return FileSystemType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new UnrecoverableErrorException("File system does not exists: " + type, CONFIGURATION_ERROR_STATUS);
        }
    }

    private enum FileSystemType {
        LOCAL,
        SWIFT
    }
}
