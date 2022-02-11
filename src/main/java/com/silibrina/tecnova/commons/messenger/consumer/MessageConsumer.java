package com.silibrina.tecnova.commons.messenger.consumer;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;
import com.silibrina.tecnova.commons.exceptions.InvalidConditionException;
import com.silibrina.tecnova.commons.messenger.Message;
import com.silibrina.tecnova.commons.messenger.MessageStatus;
import com.silibrina.tecnova.commons.messenger.MessengerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeoutException;

/**
 * Executor for RabbitMQ.
 *
 * This connects with the service with the channel, with vhost /indexer
 * and queue name coherence-service.
 * Starting this service is to declare a basicConsume to handle messages.
 * Closing this service closes the channel with RabbitMQ committing data to disk
 * before executing it.
 */
public abstract class MessageConsumer extends MessengerService implements ConsumerService, Closeable {
    private static final Logger logger = LoggerFactory.getLogger(MessageConsumer.class);
    private static final boolean AUTO_ACK = false;

    public MessageConsumer(String queueName) throws IOException, TimeoutException {
        super(queueName);
    }

    @Override
    public void start() throws IOException {
        String queueName = getQueue();
        Channel channel = getChannel();

        channel.basicConsume(queueName, AUTO_ACK, this);
    }

    @Override
    public void handleConsumeOk(String consumerTag) {
        logger.debug("tag: {}", consumerTag);
    }

    @Override
    public void handleCancelOk(String consumerTag) {
        logger.debug("tag: {}", consumerTag);
    }

    @Override
    public void handleCancel(String consumerTag) throws IOException {
        logger.debug("tag: {}", consumerTag);
    }

    @Override
    public void handleShutdownSignal(String consumerTag, ShutdownSignalException sig) {
        logger.info("shutting down consumer...");
    }

    @Override
    public void handleRecoverOk(String consumerTag) {
        logger.debug("tag: {}", consumerTag);
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        int status = MessageStatus.UNDEFINED_STATUS.status;
        try {
            status = handle(Message.fromByteArray(body));
        } catch (InvalidConditionException e) {
            logger.warn(e.getMessage());
            status = e.getStatus().status;
        } catch (IOException e) {
            logger.error("An error happened during I/O", e);
            status = MessageStatus.IO_ERROR.status;
        } catch (Exception e) {
            logger.error("An error happened", e);
            status = MessageStatus.UNKNOWN_ERROR.status;
        } finally {
            getChannel().basicAck(envelope.getDeliveryTag(), false);
            replyIfAsked(properties, status);
        }
    }

    /**
     * This method is used by the tests.
     * It is a workaround to wait for a result while testing.
     *
     * @param properties queue properties to respond this execution.
     */
    private void replyIfAsked(AMQP.BasicProperties properties, int status) {
        try {
            String replyTo = properties.getReplyTo();

            if (replyTo != null && !replyTo.isEmpty()) {
                AMQP.BasicProperties thisProps = new AMQP.BasicProperties().builder()
                        .correlationId(properties.getCorrelationId())
                        .contentType("application/text")
                        .build();

                getChannel().basicPublish("", replyTo, thisProps, getStatus(status));
            }
        } catch (Throwable e) {
            logger.error("A catastrophic error happened while replying message", e);
        }
    }

    public static byte[] getStatus(int status) {
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.putInt(status);
        return buffer.array();
    }

    public static int getStatus(byte[] status) {
        ByteBuffer buffer = ByteBuffer.wrap(status);
        return buffer.getInt();
    }

}
