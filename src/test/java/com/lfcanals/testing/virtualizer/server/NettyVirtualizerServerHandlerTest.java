package com.lfcanals.testing.virtualizer.server;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import io.netty.buffer.Unpooled;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.List;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.Rule;
import org.mockito.Mock;
import org.mockito.junit.MockitoRule;
import org.mockito.junit.MockitoJUnit;
import static org.mockito.Mockito.*;


public class NettyVirtualizerServerHandlerTest {
    @Mock
    private ChannelHandlerContext ctx;

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();


    private NettyVirtualizerServerHandler handler;

    @Before
    public void setup() throws Exception {
        this.handler = new NettyVirtualizerServerHandler();
    }


    @Test
    public void testListedCases() throws Exception {
        for(final Path testCaseInputPath : Files.newDirectoryStream(
            Paths.get(ClassLoader.getSystemResource("samples").toURI()))) {
            if( ! testCaseInputPath.toString().endsWith(".input") ) {
                continue;
            }
            final Path testCaseOutputPath = Paths.get(testCaseInputPath
                    .toString().replace(".input", ".output"));
            final List<String> inputList = Files.readAllLines(
                    testCaseInputPath, Charset.forName("UTF-8"));
            final List<String> outputList = Files.readAllLines(
                    testCaseOutputPath, Charset.forName("UTF-8"));

            final StringBuilder output = new StringBuilder();
            for(final String l : outputList) {
                output.append(l);
                output.append("\n");
            }
            final StringBuilder input = new StringBuilder();
            boolean firstLine = true;
            for(final String l : inputList) {
                if(!firstLine) input.append("\n");
                input.append(l);
            }
            System.out.println("Checking " + testCaseOutputPath.toString());
            this.handler.messageReceived(this.ctx, input.toString());
            try {
                verify(this.ctx).writeAndFlush(Unpooled.copiedBuffer(
                    output.toString(), CharsetUtil.UTF_8)); 
            } catch(Exception e) {
                System.err.println("Failed Case:" + testCaseInputPath);
                System.err.println("Expected output:(BEGIN)" 
                        + output.toString() + "(END)");
                System.err.println("Received output:(BEGIN)" 
                        + output.toString() + "(END)");
                throw e;
            }
        }
    }
}
