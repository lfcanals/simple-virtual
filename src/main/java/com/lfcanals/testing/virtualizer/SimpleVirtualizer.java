package com.lfcanals.testing.virtualizer;

import com.lfcanals.testing.virtualizer.server.NettyStringServer;
import com.lfcanals.testing.virtualizer.server.NettyStringTCPServer;

/**
 * Main clas that starts the server
 */
public final class SimpleVirtualizer {

    private static final String UDP = "UDP";
    private static final String TCP = "TCP";

    private static NettyStringServer server;

    /**
     * @param args
     * @throws Exception
     * @throws InterruptedException
     */
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Try calling with: <port> "
                    + "[maximum_packet_length]");
        }

        // Read and assign connection type
        server = new NettyStringTCPServer();

        // Read and assign port
        Integer port = Integer.parseInt(args[0]);
        if (port < 1 || port > 65535) {
            System.err.println("Provided port is should be "
                    + "between 1..65535!");
            return;
        }

        // Read and assign maximum packet length
        Integer maxPacketLength;
        if (args.length > 1) {
            maxPacketLength = Integer.parseInt(args[1]);
        } else {
            maxPacketLength = 65535;
            System.out.println("Packet length is set to " 
                            + maxPacketLength);
        }

        System.out.println("Initializing the server...");
        server.initialize();

        System.out.println("Configuring the server...");
        server.configure(port, maxPacketLength);

        System.out.println("Starting the server...");
        server.start().channel().closeFuture().sync();
    }
}
