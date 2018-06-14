package com.tracy.agent.dubbo;

import com.tracy.agent.dubbo.model.Bytes;
import com.tracy.agent.dubbo.model.JsonUtils;
import com.tracy.agent.dubbo.model.Request;
import com.tracy.agent.dubbo.model.RpcInvocation;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class DubboRpcEncoder extends MessageToByteEncoder {
    private static final int HEADER_LENGTH = 16;
    /**
     * 魔数，标记是dubbo协议
     */
    private static final short MAGIC = (short) 0xdabb;
    /**
     * 二进制 10000000
     */
    private static final byte FLAG_REQUEST = (byte) 0x80;
    /**
     * 二进制 1000000
     */
    private static final byte FLAG_TWOWAY = (byte) 0x40;
    /**
     * 二进制 100000
     */
    private static final byte FLAG_EVENT = (byte) 0x20;

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf buffer) throws Exception {
        Request req = (Request) msg;

        // header.
        //1.设置高一位、二位为协议代号
        byte[] header = new byte[HEADER_LENGTH];
        // set magic number.
        Bytes.short2bytes(MAGIC, header);

        //2.开始设置第三位.第一个bit是 req/res.第二个bit是2Way标记是否需要调用服务端.第三个bit是Event,标记是否是事件，例如是心跳.
        //第4-8位用来定义序列化的方案,fast_json 是 6 (题目要求,这个不能变……)
        //10000000
        // set request and serialization flag.
        // 二进制 6 -- 110
        header[2] = (byte) (FLAG_REQUEST | 6);

        if (req.isTwoWay()) {
            header[2] |= FLAG_TWOWAY;
        }
        if (req.isEvent()) {
            header[2] |= FLAG_EVENT;
        }

        // set request id.
        Bytes.long2bytes(req.getId(), header, 4);

        // encode request data.
        int savedWriteIndex = buffer.writerIndex();
        buffer.writerIndex(savedWriteIndex + HEADER_LENGTH);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        encodeRequestData(bos, req.getData());

        int len = bos.size();
        buffer.writeBytes(bos.toByteArray());
        Bytes.int2bytes(len, header, 12);

        // write
        buffer.writerIndex(savedWriteIndex);
        buffer.writeBytes(header);
        buffer.writerIndex(savedWriteIndex + HEADER_LENGTH + len);
    }

    private void encodeRequestData(OutputStream out, Object data) throws Exception {
        RpcInvocation inv = (RpcInvocation) data;

        PrintWriter writer = new PrintWriter(new OutputStreamWriter(out));

        JsonUtils.writeObject(inv.getAttachment("dubbo", "2.0.1"), writer);
        JsonUtils.writeObject(inv.getAttachment("path"), writer);
        JsonUtils.writeObject(inv.getAttachment("version"), writer);
        JsonUtils.writeObject(inv.getMethodName(), writer);
        JsonUtils.writeObject(inv.getParameterTypes(), writer);

        JsonUtils.writeBytes(inv.getArguments(), writer);
        JsonUtils.writeObject(inv.getAttachments(), writer);
    }

}
