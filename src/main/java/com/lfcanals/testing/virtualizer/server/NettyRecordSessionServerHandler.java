package com.lfcanals.testing.virtualizer.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.Scanner;

/**
 * Simple handler for received string messages and let the user to 
 * provide an answer via stdin, saving the session.
 */
public class NettyRecordSessionServerHandler extends 
SimpleChannelInboundHandler<String> {
    private final Logger logger = LoggerFactory.getLogger(
           NettyRecordSessionServerHandler .class);

    private final Scanner stdin;

    public NettyRecordSessionServerHandler() throws IOException {
        this.stdin = new Scanner(System.in);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, String msg) {
        System.out.println();
        System.out.println(">" + msg);

        final String answer = stdin.nextLine() + "\n";
        System.out.print("<" + answer);
        ctx.writeAndFlush(Unpooled.copiedBuffer(answer, CharsetUtil.UTF_8));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.err.println("Exception caught on channel!");
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Channel is inactive");
        super.channelInactive(ctx);
    }


    //
    // Private class
    //

    /**
     * Interface marker for a script to execute on pattern matching or
     * a pattern to substitute in case of pattern matching.
     */
    private interface ScriptOrPattern {
        public String getText();
    }

    private class Script implements ScriptOrPattern {
        @Override
        public String getText() {
            return "NOT IMPLEMENTED";
        }
    }

    private class OutputPattern implements ScriptOrPattern {
        private final String text;
        public OutputPattern(final String text) {
            this.text = text;
        }

        @Override
        public String getText() {
            return this.text;
        }
    }
}
