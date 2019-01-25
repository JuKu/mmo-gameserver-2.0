package com.jukusoft.mmo.gs.frontend.utils;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;

public class StringMessageCodec implements MessageCodec<String, String> {

    @Override
    public void encodeToWire(Buffer buffer, String s) {
        // Length of JSON: is NOT characters count
        int length = s.getBytes().length;

        // Write data into given buffer
        buffer.appendInt(length);
        buffer.appendString(s);
    }

    @Override
    public String decodeFromWire(int pos, Buffer buffer) {
        //custom message starting from this *position* of buffer
        int _pos = pos;

        //length of string
        int length = buffer.getInt(_pos);

        return buffer.getString(_pos+=4, _pos+=length);
    }

    @Override
    public String transform(String s) {
        return s;
    }

    @Override
    public String name() {
        return "string-to-string-codec";
    }

    @Override
    public byte systemCodecID() {
        return -1;
    }

}
