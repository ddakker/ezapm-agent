/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.ezdevgroup.extlib.io.netty.channel.oio;


import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.ezdevgroup.extlib.io.netty.channel.Channel;
import org.ezdevgroup.extlib.io.netty.channel.ChannelException;
import org.ezdevgroup.extlib.io.netty.channel.ChannelPromise;
import org.ezdevgroup.extlib.io.netty.channel.EventLoop;
import org.ezdevgroup.extlib.io.netty.channel.EventLoopGroup;
import org.ezdevgroup.extlib.io.netty.channel.ThreadPerChannelEventLoopGroup;

/**
 * {@link EventLoopGroup} which is used to handle OIO {@link Channel}'s. Each {@link Channel} will be handled by its
 * own {@link EventLoop} to not block others.
 */
public class OioEventLoopGroup extends ThreadPerChannelEventLoopGroup {

    /**
     * Create a new {@link OioEventLoopGroup} with no limit in place.
     */
    public OioEventLoopGroup() {
        this(0);
    }

    /**
     * Create a new {@link OioEventLoopGroup}.
     *
     * @param maxChannels       the maximum number of channels to handle with this instance. Once you try to register
     *                          a new {@link Channel} and the maximum is exceed it will throw an
     *                          {@link ChannelException} on the {@link #register(Channel)} and
     *                          {@link #register(Channel, ChannelPromise)} method.
     *                          Use {@code 0} to use no limit
     */
    public OioEventLoopGroup(int maxChannels) {
        this(maxChannels, Executors.defaultThreadFactory());
    }

    /**
     * Create a new {@link OioEventLoopGroup}.
     *
     * @param maxChannels       the maximum number of channels to handle with this instance. Once you try to register
     *                          a new {@link Channel} and the maximum is exceed it will throw an
     *                          {@link ChannelException} on the {@link #register(Channel)} and
     *                          {@link #register(Channel, ChannelPromise)} method.
     *                          Use {@code 0} to use no limit
     * @param threadFactory     the {@link ThreadFactory} used to create new {@link Thread} instances that handle the
     *                          registered {@link Channel}s
     */
    public OioEventLoopGroup(int maxChannels, ThreadFactory threadFactory) {
        super(maxChannels, threadFactory);
    }
}
