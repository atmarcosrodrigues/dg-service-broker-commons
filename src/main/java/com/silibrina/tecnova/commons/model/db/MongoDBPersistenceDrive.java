package com.silibrina.tecnova.commons.model.db;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import play.Logger;

import java.util.Locale;

import static com.silibrina.tecnova.commons.conf.ConfigConstants.Strings.*;
import static com.silibrina.tecnova.commons.exceptions.ExitStatus.DB_ERROR_STATUS;
import static com.silibrina.tecnova.commons.utils.Preconditions.checkNotNullCondition;

/**
 * Realizes a database drive for MongoDB, the configuration for this instance
 * is retrieved from the main configuration file for a play application.
 * Note that it accepts split declaration or uri. It will try uri first,
 * if not declared, it will try the split declaration (mongodb.host, mongodb.port,
 * mongodb.database, mongodb.username and mongodb.password).
 */
public class MongoDBPersistenceDrive implements PersistenceDrive {
    private static final Logger.ALogger logger = Logger.of(MongoDBPersistenceDrive.class);
    private final Config config;

    private final MongoClientURI mongodbURI;
    private final Jongo jongo;
    private final MongoClient mongo;

    public MongoDBPersistenceDrive(Config config) {
        this.config = config;

        mongodbURI = getMongoURI();
        mongo = new MongoClient(mongodbURI);
        jongo = getJongo();
    }

    @Override
    public MongoCollection getCollection(Class<?> entity) {
        checkNotNullCondition("Collection name can not be null", DB_ERROR_STATUS, entity);

        return jongo.getCollection(entity.getSimpleName());
    }

    @Override
    public void close() {
        mongo.close();
    }

    private MongoClientURI getMongoURI() {
        try {
            String uri = config.getString(MONGODB_URI.field);
            return new MongoClientURI(uri);
        } catch (ConfigException.Missing | ConfigException.WrongType e) {
            logger.error("Database configuration error", e);
            throw e;
        }
    }

    private Jongo getJongo() {
        return new Jongo(getDataBase());
    }

    /**
     * FIXME: waiting update on jongo to accept new db type.
     */
    @SuppressWarnings("deprecation")
    private DB getDataBase() {
        return mongo.getDB(getDataBaseName());
    }

    private String getDataBaseName() {
        return mongodbURI.getDatabase();
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(),
                "%s [uri: %s]",
                this.getClass().getSimpleName(), mongodbURI);
    }
}
