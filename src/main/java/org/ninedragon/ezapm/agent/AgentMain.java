package org.ninedragon.ezapm.agent;

import org.ninedragon.ezapm.agent.send.netty.NettyClient;
import org.ninedragon.ezapm.agent.timer.MBeanTimer;
import org.ninedragon.ezapm.agent.transformer.ServletTransformer;

import java.io.*;
import java.lang.instrument.Instrumentation;
import java.net.InetAddress;
import java.util.Properties;
import java.util.Timer;

/**
 * Created by ddakker on 2016-02-19.
 */
public class AgentMain {
    private static Instrumentation instrumentation;

    Timer timer = new Timer("EZAPM TIMER", true);

    public static boolean isDebug 	= false;
    public static boolean isExec 	= false;

    public static void premain(String args, Instrumentation inst) throws Exception {
        if (AgentMain.isExec == true) {
            return;
        }
        if (AgentMain.instrumentation != null) {
            return;
        }
        /*String pathConf = System.getProperty("ezapm.conf");
        if (pathConf == null) {
            System.setProperty("ezapm.conf", args);
        }
        new Thread(Logger.initializer).start();
        Logger.println("args: " + args);
        intro(args);*/


        /*boolean isRequest = args != null && args.contains("isRequest=true");
        boolean isMBean = args != null && args.contains("isMBean=true");
        boolean isLog = args != null && args.contains("isLog=true");
        isDebug = args != null && args.contains("isDebug=true");*/

        /*String serverNm     = Conf.getProperty("server.nm");
        boolean isRequest   = Boolean.parseBoolean(Conf.getProperty("is.request"));
        boolean isMBean     = Boolean.parseBoolean(Conf.getProperty("is.mbean"));
        boolean isLog       = Boolean.parseBoolean(Conf.getProperty("is.log"));
        isDebug             = Boolean.parseBoolean(Conf.getProperty("is.debug"));*/
        //String serverNm     = Conf.getProperty("server.nm");
        boolean isRequest   = true;
        boolean isMBean     = false;
        boolean isLog       = false;
        isDebug             = false;

        NettyClient.init();

        AgentMain.instrumentation = inst;

        //instrumentation.addTransformer(new TomcatTransformer());
        AgentMain agentMain = new AgentMain(instrumentation);
        agentMain.start(isRequest, isMBean, isLog);

        AgentMain.isExec				= true;
    }

    private static void intro(String conf) {
        try {
            System.setProperty("scouter.enabled", "true");
            String nativeName = AgentMain.class.getName().replace('.', '/') + ".class";
            ClassLoader cl = AgentMain.class.getClassLoader();
            if (cl == null) {
                System.out.println("loaded by system classloader ");
                System.out.println("1: " + ClassLoader.getSystemClassLoader());
                System.out.println("2: " + ClassLoader.getSystemClassLoader().getResource(nativeName));
            } else {
                System.out.println("loaded by app classloader ");
                System.out.println("3: " + cl.getResource(nativeName));
            }
        } catch (Throwable t) {
        }



    }

    public AgentMain(Instrumentation instrumentation) {
        AgentMain.instrumentation = instrumentation;
    }

    public final void start(boolean isRequest, boolean isMBean, boolean isLog) {
        System.out.println("====== EZAPM-AGENT  Service Started. =======");
        System.out.println("| isRequest\t: " + isRequest);
        System.out.println("| isMBean\t: " + isMBean);
        System.out.println("| isDebug\t: " + isDebug);
        System.out.println("| isLog\t\t: " + isLog);
        System.out.println("| author\t: ddakker@naver.com");
        System.out.println("==============================================");


        if (isRequest) {
            AgentMain.instrumentation.addTransformer(new ServletTransformer());
        }
        if (isMBean) {
            //setupTimer();
        }
        if (isLog) {
            //setupLogWatch();
        }
        System.setProperty("is.start.ezapm", "true");
    }

    private void setupTimer() {
        System.out.println("startTimer");
        MBeanTimer mBeanTimer = new MBeanTimer();
        timer.scheduleAtFixedRate(mBeanTimer, 5000, 5000);
    }
}