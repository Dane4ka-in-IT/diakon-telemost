package dev.diakon.diakontelemost.networks.tcp;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class TcpControlServer {
    public static void main(String[] args) {
        EventLoopGroup serverWorker = new NioEventLoopGroup(1);
        EventLoopGroup clientWorker = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(serverWorker,clientWorker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new TcpChannelInitializer());

            ChannelFuture cf = bootstrap.bind(8080).sync();
            cf.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            serverWorker.shutdownGracefully();
            clientWorker.shutdownGracefully();
        }
    }
}
