package com.lfcanals.testing.virtualizer.server;

import io.netty.channel.ChannelFuture;


/**
 * Interface for String Server
 *
 */
public interface NettyStringServer {

	/**
	 * Initialize the server resources
	 */
	public void initialize();

	/**
	 * Configure server properties
	 * 
	 * @param port
	 * @param packetLength
	 */
	public void configure(int port, int packetLength);

	/**
	 * Start server
	 * 
	 * @throws InterruptedException
	 * @throws Exception
	 */
	public ChannelFuture start() throws InterruptedException, Exception;

	/**
	 * Stop server
	 */
	public void stop();

}
