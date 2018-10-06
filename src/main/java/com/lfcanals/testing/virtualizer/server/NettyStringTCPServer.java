package com.lfcanals.testing.virtualizer.server;

import java.io.IOException;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.util.CharsetUtil;

/**
 * Netty String Server based on TCP
 *
 */
public final class NettyStringTCPServer extends NettyAbstractStringServer {
    /**
     * {@inheritDoc}
     */
    public ChannelFuture start() throws InterruptedException {

        ServerBootstrap serverStarter = new ServerBootstrap();
        serverStarter.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) {
                try {
                    ChannelPipeline pipeline = ch.pipeline();
                    // Line delimeter based decoder
                    pipeline.addLast("frameDecoder", 
                        new DelimiterBasedFrameDecoder(
                                packetLength, Delimiters.lineDelimiter()));
                    // String decoder
                    pipeline.addLast("stringDecoder", 
                    new StringDecoder(CharsetUtil.UTF_8));
                    // Business logic handler
                    pipeline.addLast(
                    new NettyVirtualizerServerHandler());
                } catch(IOException ioe) {
                    throw new RuntimeException(ioe);
                }
            }
        });

        return serverStarter.bind(port).sync();
    }
}
