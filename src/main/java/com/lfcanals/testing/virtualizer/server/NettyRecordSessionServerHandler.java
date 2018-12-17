package com.lfcanals.testing.virtualizer.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * Simple handler for received string messages and let the user to 
 * provide an answer via stdin, saving the session.
 */
public class NettyRecordSessionServerHandler extends 
SimpleChannelInboundHandler<String> {
    private final Logger logger = LoggerFactory.getLogger(
            NettyVirtualizerServerHandler.class);

    // Read only, no worry about concurrency
    // not necessary a map, because the use a equential full scan looking for
    // pattern matching... but it's more expressive 
    private final Map<Pattern, ScriptOrPattern> patterns;

    public NettyRecordSessionServerHandler() throws IOException {
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
                        new OutputPattern(outputPattern.toString()));
            }
        } catch(URISyntaxException use) {
            throw new IOException(use);
        }
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, String msg) {
        System.out.println();
        System.out.println(">" + msg);
        for(final Map.Entry<Pattern, ScriptOrPattern> entry 
                : this.patterns.entrySet()) {
            final Matcher matcher = entry.getKey().matcher(msg);
            if(matcher.find()) {
                if(entry.getValue() instanceof OutputPattern) {
                    final String answer = matcher.replaceAll(
                            entry.getValue().getText()) + "\n";
                    System.out.print("<" + answer);
                    ctx.writeAndFlush(Unpooled.copiedBuffer(answer, 
                            CharsetUtil.UTF_8));
                } else {
                    logger.error("Output controlled by script not still "
                            + "implemented");
                }
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
