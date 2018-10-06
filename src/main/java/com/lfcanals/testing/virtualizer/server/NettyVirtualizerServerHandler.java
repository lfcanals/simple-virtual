package com.lfcanals.testing.virtualizer.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;
import java.net.URISyntaxException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Simple handler for received string messages and dispatch to subscribed
 * objects.
 */
public class NettyVirtualizerServerHandler extends 
SimpleChannelInboundHandler<String> {

    // Read only, no worry about concurrency
    // not necessary a map, because the use a equential full scan looking for
    // pattern matching... but it's more expressive 
    private final Map<Pattern, String> patterns;

    public NettyVirtualizerServerHandler() throws IOException {
        try {
            this.patterns = new HashMap<>();
            for(final Path caseInputPath : Files.newDirectoryStream(
                Paths.get(ClassLoader.getSystemResource("patterns").toURI()))) {
                if( ! caseInputPath.toString().endsWith(".input") ) {
                    continue;
                }
                final Path caseOutputPath = Paths.get(
                        caseInputPath.toString().replace(".input", ".output"));
                final List<String> inputPatternList = Files.readAllLines(
                        caseInputPath, Charset.forName("UTF-8"));
                final List<String> outputPatternList = Files.readAllLines(
                        caseOutputPath, Charset.forName("UTF-8"));

                final StringBuilder outputPattern = new StringBuilder();
                boolean firstLine = true;
                for(final String l : outputPatternList) {
                    if(!firstLine) {
                        outputPattern.append("\n");
                    }
                    outputPattern.append(l);
                    firstLine = false;
                }
                final StringBuilder inputPattern = new StringBuilder();
                firstLine = true;
                for(final String l : inputPatternList) {
                    if(!firstLine) {
                        inputPattern.append("\n");
                    }
                    inputPattern.append(l);
                    firstLine = false;
                }

                this.patterns.put(Pattern.compile(inputPattern.toString()), 
                        outputPattern.toString());
            }
        } catch(URISyntaxException use) {
            throw new IOException(use);
        }
    }

	@Override
	public void messageReceived(ChannelHandlerContext ctx, String msg) {
        System.out.println();
        System.out.println(">" + msg);
        for(final Map.Entry<Pattern, String> entry : this.patterns.entrySet()) {
            final Matcher matcher = entry.getKey().matcher(msg);
            if(matcher.find()) {
                final String answer = matcher.replaceAll(entry.getValue()) 
                        + "\n";
                System.out.print("<" + answer);
                ctx.writeAndFlush(Unpooled.copiedBuffer(answer, CharsetUtil.UTF_8));
                break;
            }
        }
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
}
