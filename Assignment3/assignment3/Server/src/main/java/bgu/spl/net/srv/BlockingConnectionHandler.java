package bgu.spl.net.srv;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.impl.objects.BidiMessagingProtocolImpl;
import bgu.spl.net.impl.objects.ConnectionHandler;
import bgu.spl.net.impl.objects.ConnectionsImpl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

public class BlockingConnectionHandler<T> implements Runnable, ConnectionHandler<T>, bgu.spl.net.srv.ConnectionHandler<T> {
    //check the implementation please
    private final BidiMessagingProtocolImpl<T> protocol; //updated this from MessagingProtocol
    private final MessageEncoderDecoder<T> encdec;
    private final Socket sock;
    private BufferedInputStream in;
    private BufferedOutputStream out;
    private volatile boolean connected = true;
    private int connectionId;

    public BlockingConnectionHandler(Socket sock, MessageEncoderDecoder<T> reader, BidiMessagingProtocolImpl<T> protocol) {
        this.sock = sock;
        this.encdec = reader;
        this.protocol = protocol;
        ConnectionsImpl<T> connections = ConnectionsImpl.getInstance();
        this.connectionId = connections.connect(this);
        protocol.start(connectionId,ConnectionsImpl.getInstance());
    }

    @Override
    public void setId(int intValue){}

    @Override
    public void run() {
        try (Socket sock = this.sock) { //just for automatic closing
            int read;

            in = new BufferedInputStream(sock.getInputStream());
            out = new BufferedOutputStream(sock.getOutputStream());

            while (!protocol.shouldTerminate() && connected && (read = in.read()) >= 0) {
                T nextMessage = encdec.decodeNextByte((byte) read);
                if (nextMessage != null) {
                    protocol.process(nextMessage);
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void close() throws IOException {
        connected = false;
        sock.close();
    }

    @Override
    public void send(T msg) {
        try{
            if(msg != null){
                System.out.println("Sending to User");
                System.out.println(msg);
                out.write(encdec.encode(msg));
                out.flush();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
