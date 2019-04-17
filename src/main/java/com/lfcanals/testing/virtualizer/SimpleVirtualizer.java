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
            System.err.println("Try calling with: TCP|UDP <port> [save]");
            System.exit(1);
        }

        boolean savingMode = false;
        if(args.length >= 3) {
            if(args[2].toLowerCase().equals("save")) {
                savingMode = true;
                System.out.println("Saving mode enabled. "
                        + "Use standard input to provide answers to requests");
            } else if(args[2].toLowerCase().equals("play")) {
                savingMode = false;
            } else {
                System.err.println("Third parameter only can be either 'save'"
                        + " or 'play', in order to enable saving mode or "
                        + "player mode");
            }
        } else {
            savingMode = false;
        }

        // Read and assign connection type
        final NettyStringServer server;
        if(args[0].equals("TCP")) {
            server = new NettyStringTCPServer(savingMode);
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

        System.out.println("Starting the server...port " + port);
        server.start().channel().closeFuture().sync();
    }
}
