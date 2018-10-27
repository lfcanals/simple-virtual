package com.lfcanals.testing.virtualizer.util;

import org.netcrusher.core.reactor.*;
import org.netcrusher.core.filter.*;
import org.netcrusher.tcp.*;


/**
 * Main class that starts a proxy to save session.
 * 
 */
public final class SessionRecorder {
    private static String localIp, targetIp;
    private static int localPort, targetPort;

    /**
     * @param args
     * @throws Exception
     * @throws InterruptedException
     */
    public static void main(String[] args) throws Exception {
        final NioReactor reactor = new NioReactor();

        final TcpCrusher crusher = TcpCrusherBuilder.builder()
                .withReactor(reactor)
                .withBindAddress(localIp, localPort)
                .withConnectAddress(targetIp, targetPort)
                .withIncomingTransformFilterFactory((addr) ->
                        new LoggingFilter(addr, "incoming", 
                            LoggingFilter.Level.INFO))
                .withOutgoingTransformFilterFactory((addr) ->
                        new LoggingFilter(addr, "outgoing", 
                            LoggingFilter.Level.INFO))
                .buildAndOpen();
        

        //crusher.close();
        //reactor.close();
    }


    // Ok ok, fast and dirty, no complications
    public static void parseParams(final String args[]) {
        try {
            localIp = args[1].split(":")[0];
            localPort = Integer.parseInt(args[1].split(":")[1]);
            targetIp = args[2].split(":")[0];
            targetPort = Integer.parseInt(args[2].split(":")[1]);
            if(args[0].equals("TCP")) {
            } else if(args[0].equals("UDP")) {
            } else {
                throw new Exception("Protocol can be only TCP or UDP");
            }
        } catch(Exception e) {
            System.err.println("Try calling with: TCP|UDP "
                    + "<localIp>:<port> <targetIp>:<port>");
            System.exit(1);
        }
    }
}
