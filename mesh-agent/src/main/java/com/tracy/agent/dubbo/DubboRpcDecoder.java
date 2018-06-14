package com.tracy.agent.dubbo;

import com.tracy.agent.dubbo.model.Bytes;
import com.tracy.agent.dubbo.model.RpcResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.Arrays;
import java.util.List;

public class DubboRpcDecoder extends ByteToMessageDecoder {
    private static final int HEADER_LENGTH = 16;

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) {

        try {
            do {
                int savedReaderIndex = byteBuf.readerIndex();
                Object msg;
                try {
                    msg = decode2(byteBuf);
                } catch (Exception e) {
                    throw e;
                }
                if (msg == DecodeResult.NEED_MORE_INPUT) {
                    byteBuf.readerIndex(savedReaderIndex);
                    break;
                }

                list.add(msg);
            } while (byteBuf.isReadable());
        } finally {
            if (byteBuf.isReadable()) {
                byteBuf.discardReadBytes();
            }
        }
    }

    enum DecodeResult {
        NEED_MORE_INPUT, SKIP_INPUT
    }

    private Object decode2(ByteBuf byteBuf) {

        int savedReaderIndex = byteBuf.readerIndex();
        int readable = byteBuf.readableBytes();

        if (readable < HEADER_LENGTH) {
            return DecodeResult.NEED_MORE_INPUT;
        }

        byte[] header = new byte[HEADER_LENGTH];
        byteBuf.readBytes(header);

        //12位到16位是data长度
        byte[] dataLen = Arrays.copyOfRange(header, 12, 16);
        int len = Bytes.bytes2int(dataLen);
        int tt = len + HEADER_LENGTH;
        if (readable < tt) {
            return DecodeResult.NEED_MORE_INPUT;
        }

        byteBuf.readerIndex(savedReaderIndex);
        byte[] data = new byte[tt];
        byteBuf.readBytes(data);
        //以上取出了0~16位，也就是
        // dubbo返回的body中，前后各有一个换行，去掉
        byte[] subArray = Arrays.copyOfRange(data, HEADER_LENGTH + 2, data.length - 1);

        byte[] requestIdBytes = Arrays.copyOfRange(data, 4, 12);
        long requestId = Bytes.bytes2long(requestIdBytes, 0);

        RpcResponse response = new RpcResponse();
        response.setRequestId(String.valueOf(requestId));
        response.setBytes(subArray);
        return response;
    }
}
