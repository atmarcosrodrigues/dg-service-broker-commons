package com.silibrina.tecnova.commons.model.db;

import org.jongo.MongoCollection;

/**
 * Describes the access to a database drive.
 * This interface considers to use MongoDB (https://www.mongodb.com).
 * It instantiates a database and gives access to collections
 * in this database.
 */
public interface PersistenceDrive {

    /**
     * Retrieves the collection associated with a class in this database instance.
     * The database will be instantiated based on the application
     * configuration. The collection name is derived from the class name.
     *
     * @param entity The class (entity), which acts as a model in ActiveRecord
     *               https://en.wikipedia.org/wiki/Active_record_pattern.
     * @return A mongo collection object that represents the class's entity.
     */
    MongoCollection getCollection(Class<?> entity);

    /**
     * Closes a database connection. It must be called after executing an operation
     * (read or write).
     * Remember to keep as few as possible open connections and executing maximum
     * transaction in one connection. Keep in mind to close always as possible and
     * as few as possible.
     */
    void close();
}
