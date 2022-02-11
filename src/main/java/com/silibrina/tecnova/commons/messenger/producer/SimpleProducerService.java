package com.silibrina.tecnova.commons.messenger.producer;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import com.silibrina.tecnova.commons.messenger.Message;
import com.silibrina.tecnova.commons.messenger.MessageStatus;
import com.silibrina.tecnova.commons.messenger.MessengerService;
import com.silibrina.tecnova.commons.messenger.consumer.MessageConsumer;
import play.Logger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeoutException;

/**
 * A simple messenger service to produce messages to the RabbitMQ.
 * This messages will be consumed by the coherence service.
 */
public class SimpleProducerService extends MessengerService implements ProducerService {
    private static final Logger.ALogger logger = Logger.of(SimpleProducerService.class);

    private static final String EXCHANGE = "";
    private static final long TIMEOUT = 300000L;

    private static final String CONTENT_TYPE = "application/json";
    private static final String CORRELATION_ID = "id-1";

    private final boolean isSync;

    /**
     * Instantiates a messenger producer service, creating connection and channel with
     * the message queue service.
     *
     * @param queueName The name of the queue to publish content.
     * @param isSync if publish should wait for the task to be executed (useful in tests).
     *
     * @throws IOException if an error occurs while creating connection or channels.
     * @throws TimeoutException if a connection could not be created.
     */
    public SimpleProducerService(String queueName, boolean isSync) throws IOException, TimeoutException {
        super(queueName);

        this.isSync = isSync;
    }

    @Override
    public int publish(Message message) throws IOException, InterruptedException {
        Channel channel = getChannel();
        AMQP.BasicProperties properties = defaultProperties();
        QueueingConsumer consumerQueue = null;
        String queue = getQueue();

        if (isSync) {
            consumerQueue = new QueueingConsumer(channel);
            String replyQueueName = declareReplyQueue(channel, consumerQueue);
            properties = defaultSyncProperties(replyQueueName);
        }

        logger.debug("channel: {}, queue: {}, message: {}", channel, queue, message);
        channel.basicPublish(EXCHANGE, queue, properties, message.toByteArray());

        return waitAnswerIfSync(consumerQueue);
    }

    private int waitAnswerIfSync(QueueingConsumer consumerQueue) throws InterruptedException, UnsupportedEncodingException {
        if (isSync) {
            QueueingConsumer.Delivery result = consumerQueue.nextDelivery(TIMEOUT);
            return receiveResponse(result);
        }
        return MessageStatus.UNDEFINED_STATUS.status;
    }

    private int receiveResponse(QueueingConsumer.Delivery result) throws UnsupportedEncodingException {
        if (result != null) {
            int status = MessageConsumer.getStatus(result.getBody());
            logger.debug("resultMessage: {}", status);
            return status;
        } else {
            logger.info("timed out waiting for an answer (message was queued).");
        }
        return MessageStatus.UNDEFINED_STATUS.status;
    }

    private AMQP.BasicProperties defaultProperties() {
        return new AMQP.BasicProperties()
                .builder()
                .contentType(CONTENT_TYPE)
                .build();
    }

    private AMQP.BasicProperties defaultSyncProperties(String replyQueueName) {
        return new AMQP.BasicProperties()
                .builder()
                .replyTo(replyQueueName)
                .contentType(CONTENT_TYPE)
                .correlationId(CORRELATION_ID)
                .build();
    }

    private String declareReplyQueue(Channel channel, QueueingConsumer consumerQueue) throws IOException {
        String replyQueueName = channel.queueDeclare().getQueue();
        channel.basicConsume(replyQueueName, true, consumerQueue);
        return replyQueueName;
    }
}
