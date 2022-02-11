package com.silibrina.tecnova.commons.messenger.producer;

import com.silibrina.tecnova.commons.messenger.Message;

import java.io.Closeable;
import java.io.IOException;

/**
 * A service to produce messages for task creation. Messages will be
 * consumed by the coherence service to create tasks related to indexing
 * and deletion.
 */
public interface ProducerService extends Closeable {

    /**
     * Publishes a message to the message queue (RabbitMQ).
     * Primarily, this method is non blocking, except when specified
     * in configuration.
     *
     * @param message a message representing a task to be created at the coherence service.
     *
     * @throws IOException if some error occurs while declaring queue or parsing
     *                      the response of the published message.
     * @throws InterruptedException if it is interrupted while waiting for an answer.
     *                      This can only happen if application was configured as sync.
     *
     * @return a number representing the status of this action.
     */
    int publish(Message message) throws IOException, InterruptedException;
}
