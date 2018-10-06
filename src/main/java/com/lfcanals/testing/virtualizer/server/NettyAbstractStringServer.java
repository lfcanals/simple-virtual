package com.lfcanals.testing.virtualizer.server;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

/**
 * Abstract Netty String Server instance to handle common operations
 * 
 */
public abstract class NettyAbstractStringServer implements NettyStringServer {

	/**
	 * Boss event group to handle connections
	 */
	protected EventLoopGroup bossGroup;

	/**
	 * Worker event group to handle I/O
	 */
	protected EventLoopGroup workerGroup;

	/**
	 * Port for server
	 */
	protected int port;

	/**
	 * Packet length for packets
	 */
	protected int packetLength;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() {
		bossGroup = new NioEventLoopGroup(1);
		workerGroup = new NioEventLoopGroup();

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void configure(int port, int packetLength) {
		this.port = port;
		this.packetLength = packetLength;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stop() {
		workerGroup.shutdownGracefully();
		bossGroup.shutdownGracefully();
	}

}
