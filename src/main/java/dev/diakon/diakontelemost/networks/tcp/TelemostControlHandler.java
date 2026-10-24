package dev.diakon.diakontelemost.networks.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public class TelemostControlHandler extends ChannelInboundHandlerAdapter {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(TelemostControlHandler.class);
    private static long lastLogTime = 0;
    private static final long LOG_INTERVAL_MS = 1000;

    private static final String pong = "PONG";
    private static final String createRoom = "ROOM_CREATED: ";
    private static final String error = "ERROR: UNKNOWN_COMMAND";


    @Override
    public  void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteMessage = (ByteBuf)  msg;
        try {
            if (byteMessage.readableBytes() > 0) {
                byte commandId = byteMessage.readByte();
                String payload = byteMessage.toString(CharsetUtil.UTF_8);
                TelemostCommand command = new TelemostCommand(commandId, payload);

                switch (commandId){
                    case 0x01 -> sendMessage(ctx, pong);

                    case 0x02 -> sendMessage(ctx, createRoom + "[" + payload + "]");

                    default -> sendMessage(ctx, error);
                }


            } else {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastLogTime > LOG_INTERVAL_MS) {
                    lastLogTime = currentTime;
                    logger.warn("Received null bytes");
                }
            }

        } finally {
            ReferenceCountUtil.release(byteMessage);
        }
    }

    private static void sendMessage(ChannelHandlerContext ctx, String line) {
        byte[] byteResponse = line.getBytes(CharsetUtil.UTF_8);
        ByteBuf buffer = ctx.alloc().buffer(byteResponse.length);
        buffer.writeBytes(byteResponse);
        ctx.writeAndFlush(buffer);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
