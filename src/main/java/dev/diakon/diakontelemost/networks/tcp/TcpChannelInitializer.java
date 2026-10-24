package dev.diakon.diakontelemost.networks.tcp;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

public class TcpChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline()
                .addLast(new AntiHttpMethodsFilter())
                .addLast(new LengthFieldBasedFrameDecoder(
                        1024*1024,
                        0,
                        4,
                        0,
                        4
                ))
                .addLast(new LengthFieldPrepender(4))
                .addLast(new TelemostControlHandler());
    }
}
