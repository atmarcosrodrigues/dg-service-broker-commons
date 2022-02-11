package com.silibrina.tecnova.commons.conf;

import java.util.Locale;

import static com.silibrina.tecnova.commons.utils.Preconditions.checkValidString;

/**
 * General constant for configuration fields.
 */
public class ConfigConstants {
    private static final String STORAGE = "storage";
    private static final String LOCAL = "local";
    private static final String SWIFT = "swift";
    private static final String GATHERER = "gatherer";
    private static final String RABBITMQ = "rabbitmq";
    private static final String MONGODB = "mongodb";
    private static final String TESTS = "tests";

    /**
     * Constants for {@link Boolean} fields.
     */
    public enum Booleans {
        // Swift and Local
        STORAGE_SYNC(STORAGE + ".sync"),

        // RabbitMQ
        RABBITMQ_DURABLE(RABBITMQ + ".durable");

        public final String field;

        Booleans(String field) {
            this.field = field;
        }

        @Override
        public String toString() {
            return String.format(Locale.getDefault(), "%s [field: %s]",
                    this.getClass().getSimpleName(), field);
        }
    }

    /**
     * Constants for {@link Boolean} fields.
     */
    public enum Integers {
        // RabbitMQ
        RABBITMQ_PREFETCH(RABBITMQ + ".prefetch"),
        RABBITMQ_PORT(RABBITMQ + ".port"),

        // MongoDB
        MONGODB_PORT(MONGODB + ".port");

        public final String field;

        Integers(String field) {
            this.field = field;
        }

        @Override
        public String toString() {
            return String.format(Locale.getDefault(), "%s [field: %s]",
                    this.getClass().getSimpleName(), field);
        }
    }

    /**
     * Constants for {@link String} fields.
     */
    public enum Strings {
        // RabbitMQ
        RABBITMQ_USERNAME(RABBITMQ + ".username"),
        RABBITMQ_PASSWORD(RABBITMQ + ".password"),
        RABBITMQ_HOST(RABBITMQ + ".host"),
        RABBITMQ_VHOST(RABBITMQ + ".vhost"),
        RABBITMQ_QUEUE_NAME(RABBITMQ + ".queue"),

        // MongoDB
        MONGODB_URI(MONGODB + ".uri"),

        // Index
        GATHERER_FS(GATHERER + ".fs"),
        GATHERER_INDEX_DIR(GATHERER + ".index_dir"),

        // Local Storage
        LOCAL_STORAGE_BASEDIR(LOCAL + ".storage"),
        LOCAL_CONTAINER_DIR(LOCAL + ".container_dir"),
        LOCAL_STORAGE_URL(LOCAL + ".storage_url"),

        // Swift Storage
        SWIFT_CONTAINER(SWIFT + ".container_name"),
        SWIFT_USER(SWIFT + ".user"),
        SWIFT_PASSWORD(SWIFT + ".password"),
        SWIFT_IDENTITY_ENDPOINT(SWIFT + ".endpoint"),
        SWIFT_ENDPOINT(SWIFT + ".swift_endpoint"),
        SWIFT_PROJECT_ID(SWIFT  + ".project_id"),
        SWIFT_DOMAIN_NAME(SWIFT  + ".domain_name"),

        // Tests parameters
        TESTS_TARGET_FS(TESTS + ".target_fs");

        public final String field;

        Strings(String field) {
            this.field = field;
        }

        @Override
        public String toString() {
            return String.format(Locale.getDefault(), "%s [field: %s]",
                    this.getClass().getSimpleName(), field);
        }
    }
}
