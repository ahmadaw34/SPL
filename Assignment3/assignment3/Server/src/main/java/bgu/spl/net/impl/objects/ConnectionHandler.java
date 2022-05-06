package bgu.spl.net.impl.objects;

import java.io.Closeable;

/**
 * The ConnectionHandler interface for Message of type T
 */
public interface ConnectionHandler<T> extends Closeable {
    void send(T msg);
}

