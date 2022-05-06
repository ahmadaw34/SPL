package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.impl.echo.LineMessageEncoderDecoder;
import bgu.spl.net.impl.objects.BidiMessagingProtocolImpl;
import bgu.spl.net.srv.Server;

public class TPCMain {
    public static void main(String[] args) {
        int port = Integer.valueOf(args[0]);
        Server.threadPerClient(
                port, //port
                BidiMessagingProtocolImpl::new, //protocol factory
                LineMessageEncoderDecoder::new //message encoder decoder factory
        ).serve();
    }
}
