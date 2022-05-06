package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.impl.echo.LineMessageEncoderDecoder;
import bgu.spl.net.impl.objects.BidiMessagingProtocolImpl;
import bgu.spl.net.srv.Server;

public class ReactorMain {
    public static void main(String[] args) {
        int port = Integer.valueOf(args[0]);
        int numberOfThreads = Integer.valueOf(args[1]);
        Server.reactor(
                numberOfThreads,
                port, //port
                BidiMessagingProtocolImpl::new, //protocol factory
                LineMessageEncoderDecoder::new //message encoder decoder factory
        ).serve();
    }
}
