package bgu.spl.net.impl.echo;

import bgu.spl.net.api.MessageEncoderDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class LineMessageEncoderDecoder implements MessageEncoderDecoder<String> {

    private byte[] bytes = new byte[1 << 10]; //start with 1k
    private short opcode = -1 ;
    private int len = 0;

    @Override
    public String decodeNextByte(byte nextByte) {
        //notice that the top 128 ascii characters have the same representation as their utf-8 counterparts
        //this allows us to do the following comparison
        if (nextByte == '\0' & (len != 0 | opcode != -1)) {
            pushByte((byte) ' '); //push space between arguments
        } else if (nextByte == ';') {
            String toSend = popString();
            opcode = -1;
            return toSend;
        } else pushByte(nextByte); //push 0 for opcode
        if (len == 2 & opcode == -1) {
            opcode = bytesToShort(bytes);
            len = 0;
        }
        return null;
    }

    @Override
    public byte[] encode(String message) {
        String[] words = message.split(" ");
        String msg;
        if(words.length == 2){
            msg = ";";
        }else{
            msg = message.substring(words[0].length() + words[1].length() + 2) + ";";
        }
        byte[] bytes = (msg).getBytes();
        byte[] bytesArr = new byte[5 + bytes.length - 1];
        bytesArr[0] = (byte) ((Short.parseShort(words[0]) >> 8) & 0xFF);
        bytesArr[1] = (byte) (Short.parseShort(words[0]) & 0xFF);
        bytesArr[2] = (byte) ((Short.parseShort(words[1]) >> 8) & 0xFF);
        bytesArr[3] = (byte) (Short.parseShort(words[1]) & 0xFF);
        for (int i = 0; i < bytes.length; i++) {
            bytesArr[i+4] = bytes[i];
        }
        return bytesArr;
    }

    private short bytesToShort(byte[] byteArr) {
        short result = (short)((byteArr[0] & 0xff) << 8);
        result += (short)(byteArr[1] & 0xff);
        return result;
    }
    private void pushByte(byte nextByte) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }

        bytes[len++] = nextByte;
    }

    private String popString() {
        String result = new String(bytes, 0, len, StandardCharsets.UTF_8);
        len = 0;
        bytes = new byte[1 << 10];
        return opcode + " " + result;
    }
}
