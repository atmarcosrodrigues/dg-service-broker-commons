package com.silibrina.tecnova.commons.messenger;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.silibrina.tecnova.commons.utils.Preconditions;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.silibrina.tecnova.commons.conf.ConfigConstants.Booleans.RABBITMQ_DURABLE;
import static com.silibrina.tecnova.commons.conf.ConfigConstants.Integers.RABBITMQ_PORT;
import static com.silibrina.tecnova.commons.conf.ConfigConstants.Integers.RABBITMQ_PREFETCH;
import static com.silibrina.tecnova.commons.conf.ConfigConstants.Strings.*;
import static com.silibrina.tecnova.commons.exceptions.ExitStatus.CONFIGURATION_ERROR_STATUS;

/**
 * This is a service to create a connection with the RabbitMQ service.
 */
public class MessengerService implements Closeable {
    private static final Logger logger = LoggerFactory.getLogger(MessengerService.class);

    private final Config config;
    private final Connection connection;
    private final Channel channel;
    private final String queueName;

    protected MessengerService(String queueName) throws IOException, TimeoutException {
        Preconditions.checkValidString("A queue name must be declared", CONFIGURATION_ERROR_STATUS, queueName);


        this.config = ConfigFactory.load();
        this.queueName = queueName;

        this.connection = getConnection();
        this.channel = getChannel(connection);
    }

    private Connection getConnection() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setVirtualHost(config.getString(RABBITMQ_VHOST.field));
        factory.setHost(config.getString(RABBITMQ_HOST.field));
        factory.setPort(config.getInt(RABBITMQ_PORT.field));
        factory.setPassword(config.getString(RABBITMQ_PASSWORD.field));
        factory.setUsername(config.getString(RABBITMQ_USERNAME.field));
        return factory.newConnection();
    }

    private Channel getChannel(Connection connection) throws IOException {
        Channel channel = connection.createChannel();
        boolean durable = config.getBoolean(RABBITMQ_DURABLE.field);
        String queueName = getQueue();

        channel.queueDeclare(queueName, durable, false, false, null);
        channel.basicQos(config.getInt(RABBITMQ_PREFETCH.field));
        return channel;
    }

    @Override
    public void close() throws IOException {
        logger.debug("closing channel...");
        if (channel != null && channel.isOpen()) {
            try {
                channel.close();
            } catch (TimeoutException e) {
                logger.error("A timeout happened", e);
            }
        }
        logger.debug("closing connection...");
        if (connection != null && connection.isOpen()) {
            connection.close();
        }
    }

    protected Channel getChannel() {
        return channel;
    }

    public String getQueue() {
        return queueName;
    }
}
