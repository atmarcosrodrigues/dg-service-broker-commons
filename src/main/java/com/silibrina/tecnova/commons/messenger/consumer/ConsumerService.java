package com.silibrina.tecnova.commons.messenger.consumer;

import com.rabbitmq.client.Consumer;
import com.silibrina.tecnova.commons.messenger.Message;

import java.io.Closeable;
import java.io.IOException;

/**
 * Service responsible for consume messages from the queue and
 * execute the task associated with it.
 */
public interface ConsumerService extends Closeable, Consumer {

    /**
     * Starts the consumer service.
     *
     * @throws IOException if something wrong happens while starting this service.
     */
    void start() throws IOException ;

    /**
     * Executes the task based on the message.
     *
     * @param message The message to be consumed.
     * @throws Exception if an error occurs while dealing with I/O.
     *
     * @return a number representing the status of this operation.
     */
    int handle(Message message) throws Exception;
}
