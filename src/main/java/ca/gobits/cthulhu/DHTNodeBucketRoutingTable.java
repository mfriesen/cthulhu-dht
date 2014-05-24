//
// Copyright 2013 Mike Friesen
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

package ca.gobits.cthulhu;

import static ca.gobits.dht.DHTConversion.toInetAddress;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;

import org.apache.log4j.Logger;

import ca.gobits.cthulhu.domain.DHTNode;
import ca.gobits.cthulhu.domain.DHTNode.State;
import ca.gobits.cthulhu.domain.DHTNodeComparator;
import ca.gobits.cthulhu.domain.DHTNodeFactory;

/**
 * Implementation of DHT Bucket Routing Table.
 *
 * http://www.bittorrent.org/beps/bep_0005.html
 *
 */
public final class DHTNodeBucketRoutingTable extends
    SimpleChannelInboundHandler<DatagramPacket> implements
    DHTNodeRoutingTable {

    /** LOGGER. */
    private static final Logger LOGGER = Logger
            .getLogger(DHTNodeBucketRoutingTable.class);

    /** Default Time in Millis to wait for a response from a DHTNode. */
    private static final int DEFAULT_REQUEST_TIMEOUT = 5000;

    /** Maximum number of nodes Routing Table holds. */
    private static final int MAX_NUMBER_OF_NODES = 1000000;

    /** Netty EventLoopGroup used to determine which nodes are 'good'. */
    private final EventLoopGroup group = new NioEventLoopGroup();

    /** Netty Bootstrap used to determine which nodes are 'good'. */
    private final Bootstrap bootstrap = new Bootstrap();

    /** root node of the routing table. */
    private final ConcurrentSortedList<DHTNode> nodes;

    /**
     * constructor.
     */
    public DHTNodeBucketRoutingTable() {
        this.nodes = new ConcurrentSortedList<DHTNode>(
                DHTNodeComparator.getInstance(), false);

        this.bootstrap
            .group(group)
            .channel(NioDatagramChannel.class)
            .handler(this);
    }

    @Override
    public void addNode(final byte[] infoHash, final InetSocketAddress addr,
            final State state) {

        if (nodes.size() < MAX_NUMBER_OF_NODES) {

            if (State.GOOD == state) {

                DHTNode node = DHTNodeFactory.create(infoHash, addr, state);

                addNodeLoggerDebug(node);
                nodes.add(node);

            } else {

                sendFindRequest(infoHash, addr);
            }

        } else {
            LOGGER.warn("MAXIMUM number of noded reached "
                    + MAX_NUMBER_OF_NODES);
        }
    }

    /**
     * Sends a Find Request to a node to determine
     * whether it is "good" or not.
     * @param infoHash  info hash
     * @param addr  address to send find request to
     */
    private void sendFindRequest(final byte[] infoHash,
            final InetSocketAddress addr) {

        String message = "d1:ad2:id20:abcdefghij01234567899:info_hash20:"
            + "mnopqrstuvwxyz123456e1:q9:get_peers1:t2:aa1:y1:qe";

        try {
            // Start the client.
            Channel ch = this.bootstrap.bind(0).sync().channel();

            ch.writeAndFlush(
                    new DatagramPacket(Unpooled.copiedBuffer(message,
                            CharsetUtil.UTF_8), addr)).sync();

            if (!ch.closeFuture().await(DEFAULT_REQUEST_TIMEOUT)) {
                LOGGER.info("Find Request to " + addr.getHostString()
                        + " timed out.");
            }
        } catch (Exception e) {
            LOGGER.warn("Cannot send Find Request", e);
        }
    }

    /**
     * Print debug information on adding a node. (LOGGER.isDebugEnabled())
     * @param node  DHTNode
     */
    private void addNodeLoggerDebug(final DHTNode node) {

        if (LOGGER.isDebugEnabled()) {

            String host = "unknown";

            try {

                InetAddress addr = toInetAddress(node.getAddress());
                host = addr.getHostAddress();

            } catch (Exception e) {

                LOGGER.info(e, e);
            }

            LOGGER.debug("adding node " + node.getInfoHash() + " "
                + host + ":" + node.getPort());
        }
    }

    @Override
    public List<DHTNode> findClosestNodes(final BigInteger nodeId) {
        return findClosestNodes(nodeId, DEFAULT_SEARCH_COUNT);
    }

    @Override
    public DHTNode findExactNode(final BigInteger nodeId) {

        DHTNode nodeMatch = null;
        DHTNode node = DHTNodeFactory.create(nodeId, DHTNode.State.UNKNOWN);
        int index = this.nodes.indexOf(node);

        if (index >= 0 && index < this.nodes.size()) {
            DHTNode foundNode = this.nodes.get(index);
            if (foundNode.getInfoHash().equals(nodeId)) {
                nodeMatch = foundNode;
            }
        }

        return nodeMatch;
    }

    @Override
    public List<DHTNode> findClosestNodes(final BigInteger nodeId,
            final int returnCount) {

        DHTNode node = findExactNode(nodeId);

        if (node != null) {
            node.setState(State.GOOD);
        } else {
            node = DHTNodeFactory.create(nodeId, State.UNKNOWN);
        }

        return findClosestNodes(node, returnCount);
    }

    /**
     * Finds the closest nodes list.
     * @param node  node to find
     * @param returnCount  number of nodes to return
     * @return List<DHTNode>
     */
    private List<DHTNode> findClosestNodes(final DHTNode node,
            final int returnCount) {

        int index = nodes.indexOf(node);

        int fromIndex = index > 0 ? index - 1 : 0;
        int toIndex = index < getTotalNodeCount() ? index + 1
                : getTotalNodeCount();
        int count = toIndex - fromIndex;

        while (count < returnCount && count < nodes.size()) {

            if (fromIndex > 0) {
                fromIndex--;
                count++;
            }

            if (count < returnCount
                    && toIndex < getTotalNodeCount()) {
                toIndex++;
                count++;
            }
        }

        return nodes.subList(fromIndex, toIndex);
    }

    @Override
    public void channelRead0(final ChannelHandlerContext ctx,
            final DatagramPacket msg) throws Exception {
        String response = msg.content().toString(CharsetUtil.UTF_8);
        System.out.println("Quote of the Moment: " + response);
        ctx.close();
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx,
            final Throwable cause) {
        LOGGER.info("Exception thrown while sending request to DHTNode: "
                + cause.getMessage());
        ctx.close();
    }

    /**
     * @return DHTBucket
     */
    public SortedCollection<DHTNode> getNodes() {
        return nodes;
    }

    @Override
    public int getTotalNodeCount() {
        return nodes.size();
    }

    @Override
    public int getMaxNodeCount() {
        return MAX_NUMBER_OF_NODES;
    }
}
