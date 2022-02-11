package com.silibrina.tecnova.commons.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mongodb.WriteResult;
import com.silibrina.tecnova.commons.conf.ConfigLoader;
import com.silibrina.tecnova.commons.exceptions.InvalidConditionException;
import com.silibrina.tecnova.commons.model.db.MongoDBPersistenceDrive;
import com.silibrina.tecnova.commons.model.db.PersistenceDrive;
import com.typesafe.config.Config;
import org.bson.types.ObjectId;
import org.jongo.MongoCollection;
import org.jongo.MongoCursor;

import java.util.*;
import java.util.stream.Collectors;

import static com.silibrina.tecnova.commons.utils.Preconditions.checkNotNullCondition;
import static com.silibrina.tecnova.commons.utils.Preconditions.checkValidString;

/**
 * This is a model for a type that can be stored in database.
 * It works like Active Record, where a type knows how to manipulate
 * him self to store, remove, read etc.
 *
 * A type extending this class can automatically be saved in a
 * MongoDB collection with the same name of the class.
 *
 * @param <T> The type extending this class.
 */
public abstract class Model<T extends Model> extends JsonParseable {
    private final ObjectId _id;

    @JsonIgnore
    private final Config config;

    Model(ObjectId _id) {
        this._id = _id;
        config = ConfigLoader.load();
    }

    /**
     * Save (insert or update) the current object in his current state.
     *
     * @return the result of the save.
     */
    @JsonIgnore
    public WriteResult save() {
        PersistenceDrive drive = new MongoDBPersistenceDrive(config);
        MongoCollection collection = drive.getCollection(this.getClass());
        try {
            return collection.save(this);
        } finally {
            drive.close();
        }
    }

    public WriteResult update(String modifier, Object... values) {
        PersistenceDrive drive = new MongoDBPersistenceDrive(config);
        MongoCollection collection = drive.getCollection(this.getClass());
        try {
            return collection.update("{_id : #}", _id()).with(modifier, values);
        } finally {
            drive.close();
        }
    }

    /**
     * Finds an entry in the collection of the current type.
     *
     * @param id the hex representation of the id (if using default)
     *           or the id of the entry (if specifying when saving).
     *
     * @return the entry matching the given id or null if not found.
     */
    @SuppressWarnings("unchecked")
    @JsonIgnore
    public T find(String id) {
        checkValidString("id must be a valid string (non empty and non null)", id);
        try {
            return find(new ObjectId(id));
        } catch (IllegalArgumentException e) {
            throw new InvalidConditionException(e.getMessage());
        }
    }

    /**
     * Finds an entry in the collection of the current type.
     *
     * @param id the id of the entry.
     *
     * @return the entry matching the given id or null if not found.
     */
    @SuppressWarnings("unchecked")
    @JsonIgnore
    public T find(ObjectId id) {
        checkNotNullCondition("id can not be null", id);

        PersistenceDrive drive = new MongoDBPersistenceDrive(config);
        MongoCollection collection = drive.getCollection(this.getClass());
        try {
            Class<? extends Model> c = this.getClass();
            return collection.findOne(id).as((Class<T>) c);
        } finally {
            drive.close();
        }
    }

    /**
     * Finds all entries of the current type (in the same collection).
     *
     * @return a list with all the current entries.
     */
    @SuppressWarnings("unchecked")
    @JsonIgnore
    public List<T> find() {
        PersistenceDrive drive = new MongoDBPersistenceDrive(config);
        MongoCollection collection = drive.getCollection(this.getClass());
        try {
            MongoCursor<T> cursor = collection.find().as((Class<T>) this.getClass());

            List<T> entities = new LinkedList<>();
            for (T entity : cursor) {
                entities.add(entity);
            }

            return entities;
        } finally {
            drive.close();
        }
    }

    public static List<ObjectId> toObjectIds(Collection<String> stringIds) {
        List<ObjectId> ids = new ArrayList<>(stringIds.size());
        ids.addAll(stringIds.stream().map(ObjectId::new).collect(Collectors.toList()));
        return ids;
    }

    /**
     * Retrieves entities from the given list of ids.
     *
     * @param ids a set of ids.
     *
     * @return a list of entities.
     */
    @SuppressWarnings("unchecked")
    @JsonIgnore
    public List<T> find(Set<String> ids) {
        PersistenceDrive drive = new MongoDBPersistenceDrive(config);
        MongoCollection collection = drive.getCollection(this.getClass());
        try {
            MongoCursor<T> cursor = collection.find("{_id : {$in : #}}", toObjectIds(ids)).as((Class<T>) this.getClass());

            List<T> entities = new LinkedList<>();
            for (T entity : cursor) {
                entities.add(entity);
            }

            return entities;
        } finally {
            drive.close();
        }
    }

    /**
     * Find entries based on a set of rules with their related parameters.
     * Remember, if your are querying ids, you must pass the {@link ObjectId}.
     *
     * @param query a mongodb based query. Where # are substituted by the parameters.
     *              E.g.: query = '{name : #}, parameters = ['SomeName']
     *              The engine will turn it to:
     *              '{name : 'SomeName'}
     *
     * @param parameters a list of parameters to match with the query octothorpe (#).
     *                   Note that the number of parameters must match the number of
     *                   octothorpes in the query.
     * @param page the page to start retrieving items.
     * @param limit number of items to retrieve.
     * @param sort the field name to sort the content based on it.
     *
     * @return A list of the found entities.
     */
    @SuppressWarnings("unchecked")
    @JsonIgnore
    public List<T> find(String query, Object[] parameters, int page, int limit, String sort) {
        PersistenceDrive drive = new MongoDBPersistenceDrive(config);
        MongoCollection collection = drive.getCollection(this.getClass());
        try {
            MongoCursor<T> cursor = collection.find(query, parameters)
                    .skip(page)
                    .limit(limit)
                    .sort(sort)
                    .as((Class<T>) this.getClass());
            List<T> entities = new ArrayList<>(cursor.count());
            for (T entity : cursor) {
                entities.add(entity);
            }

            return entities;
        } finally {
            drive.close();
        }
    }

    @JsonIgnore
    public long count(String query, Object[] parameters) {
        PersistenceDrive drive = new MongoDBPersistenceDrive(config);
        MongoCollection collection = drive.getCollection(this.getClass());
        try {
            return collection.count(query, parameters);
         } finally {
             drive.close();
        }
    }

    /**
     * Removes the current entry from the collection.
     *
     * @return the result of the remove.
     */
    @JsonIgnore
    public WriteResult remove() {
        PersistenceDrive drive = new MongoDBPersistenceDrive(config);
        MongoCollection collection = drive.getCollection(this.getClass());
        try {
            return collection.remove(_id());
        } finally {
            drive.close();
        }
    }

    /**
     * The id of the current object.
     * This implementation considers to use the default type for ids.
     * If not specified during insert, it will be null until the object
     * is saved.
     * After saving, the id is populated automatically.
     *
     * @return the id of the current object.
     */
    public ObjectId _id() {
        return _id;
    }
}
