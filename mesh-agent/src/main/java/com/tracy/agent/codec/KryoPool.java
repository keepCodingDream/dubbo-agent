package com.tracy.agent.codec;

import com.tracy.agent.kryo.KryoSerialization;
import com.tracy.agent.kryo.KyroFactory;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Component
public class KryoPool {

    private static final byte[] LENGTH_PLACEHOLDER = new byte[4];

    private KyroFactory kyroFactory;

    @Value("${serialize.kryo.pool.maxTotal}")
    private int maxTotal;

    @Value("${serialize.kryo.pool.minIdle}")
    private int minIdle;

    @Value("${serialize.kryo.pool.maxWaitMillis}")
    private long maxWaitMillis;

    @Value("${serialize.kryo.pool.minEvictableIdleTimeMillis}")
    private long minEvictableIdleTimeMillis;

    @PostConstruct
    public void init() {
        kyroFactory = new KyroFactory(maxTotal, minIdle, maxWaitMillis, minEvictableIdleTimeMillis);
    }

    public void encode(final ByteBuf out, final Object message) throws IOException {
        ByteBufOutputStream bout = new ByteBufOutputStream(out);
        bout.write(LENGTH_PLACEHOLDER);
        KryoSerialization kryoSerialization = new KryoSerialization(kyroFactory);
        kryoSerialization.serialize(bout, message);
    }

    public Object decode(final ByteBuf in) throws IOException {
        KryoSerialization kryoSerialization = new KryoSerialization(kyroFactory);
        return kryoSerialization.deserialize(new ByteBufInputStream(in));
    }
}
