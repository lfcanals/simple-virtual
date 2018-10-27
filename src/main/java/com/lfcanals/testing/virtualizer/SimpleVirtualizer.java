package com.lfcanals.testing.virtualizer;

import com.lfcanals.testing.virtualizer.server.NettyStringServer;
import com.lfcanals.testing.virtualizer.server.NettyStringTCPServer;

/**
 * Main clas that starts the server
 */
public final class SimpleVirtualizer {

    /**
     * @param args
     * @throws Exception
     * @throws InterruptedException
     */
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Try calling with: TCP|UDP <port>");
            System.exit(1);
        }

        // Read and assign connection type
        final NettyStringServer server;
        if(args[0].equals("TCP")) {
            server = new NettyStringTCPServer();
        } else {
            System.err.println("At this moment, only TCP protocol is accepted");
            throw new UnsupportedOperationException();
        }

        final int port = Integer.parseInt(args[1]);
        if (port < 1 || port > 65535) {
            System.err.println("Provided port is should be "
                    + "between 1..65535!");
            System.exit(1);
        }

        System.out.println("Initializing the server...");
        server.initialize();

        System.out.println("Configuring the server...");
        server.configure(port, 65535);

        System.out.println("Starting the server...");
        server.start().channel().closeFuture().sync();
    }
}
