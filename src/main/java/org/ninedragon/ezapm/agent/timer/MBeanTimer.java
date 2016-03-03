package org.ninedragon.ezapm.agent.timer;

import org.ninedragon.ezapm.agent.Logger;
import org.ninedragon.ezapm.agent.send.netty.NettyClient;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import java.lang.management.ManagementFactory;
import java.util.TimerTask;

/**
 * Created by ddakker on 2016-02-26.
 */
 public class MBeanTimer extends TimerTask {
    public void run() {
        try {
            ObjectName om = new ObjectName("java.lang:type=Memory");

            MBeanServer connection = ManagementFactory.getPlatformMBeanServer();


            Object attrValue = connection.getAttribute(om, "HeapMemoryUsage");

            long max = Long.parseLong(((CompositeData)attrValue).get("max").toString());
            long used = Long.parseLong(((CompositeData)attrValue).get("used").toString());
            long heapUsedPercent = Math.round((used*1.0 / max*1.0) * 100.0);

            attrValue = connection.getAttribute(om, "NonHeapMemoryUsage");

            long nonHeapUsed = Long.parseLong(((CompositeData)attrValue).get("used").toString());

            String msg = "{serverNm: '" + System.getProperty("agent.server.nm") + "', heapUsedPercent: '" + heapUsedPercent + "', heapMax: '" + max + "', heapUsed: '" + used + "', nonHeapUsed: '" + nonHeapUsed + "', time: '" + System.currentTimeMillis() + "'}";
            Logger.println("msg: " + msg);

            NettyClient.send(NettyClient.GRP_WAS_MEM, msg);
        } catch (Exception e) {
            System.err.println("agent error: " + e);
        }

    }
}
