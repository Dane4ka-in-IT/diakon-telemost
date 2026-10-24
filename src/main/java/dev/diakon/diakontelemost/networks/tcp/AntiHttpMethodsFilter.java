package dev.diakon.diakontelemost.networks.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.util.List;

public class AntiHttpMethodsFilter extends ByteToMessageDecoder {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(AntiHttpMethodsFilter.class);
    private static long lastLogTime = 0;
    private static final long LOG_INTERVAL_MS = 1000;
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception{
        if (in.readableBytes() < 4) {
            return;
        }

        int messLength = in.getInt(in.readerIndex());
        if (messLength < 2 || messLength > 1024 * 1024){

            long currentTime = System.currentTimeMillis();

            if(currentTime - lastLogTime > LOG_INTERVAL_MS){
                lastLogTime = currentTime;
                int firstByte = in.getByte(in.readerIndex()) & 0xFF;
                logger.warn("Invalid protocol! First byte: {}, Frame length: {}, Time {}: ",
                        firstByte,
                        messLength,
                        java.time.Instant.ofEpochMilli(currentTime)
                );
            }

            in.clear();
            ctx.close();
            return;
        }
        ctx.pipeline().remove(this);

    }
}
