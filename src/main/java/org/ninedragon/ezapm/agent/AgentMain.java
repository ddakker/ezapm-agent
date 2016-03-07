package org.ninedragon.ezapm.agent;

import org.ninedragon.ezapm.agent.send.netty.NettyClient;
import org.ninedragon.ezapm.agent.timer.MBeanTimer;
import org.ninedragon.ezapm.agent.transformer.ServletTransformer;

import java.lang.instrument.Instrumentation;
import java.util.Timer;

/**
 * Created by ddakker on 2016-02-19.
 */
public class AgentMain {
    Timer timer = new Timer("ddakker Agent Timer", true);
    Instrumentation instrumentation;
    static AgentMain agentMain;
    public static boolean isExec 	= false;
    public static boolean isDebug 	= false;



    public static void premain(String args, Instrumentation instrumentation) throws Exception {
        if (AgentMain.isExec == false) {
            String pathConf = System.getProperty("ezapm.conf");
            if (pathConf == null) {
                System.setProperty("ezapm.conf", args);
            }

            System.out.println("NettyClient.c: " + NettyClient.c);

            //new Thread(Logger.initializer).start();

            /*AgentMain.nettyClientThread = new Thread(NettyClient.initializer);
            nettyClientThread.setDaemon(true);
            nettyClientThread.start();
            threads.add(nettyClientThread);*/
            NettyClient.startAndCheck();

            /*System.out.println("NettyClient.ctx: " + NettyClient.ctx);
            if (NettyClient.ctx != null) {
                NettyClient.ctx.close();
            }*/

            boolean isRequest   = Boolean.parseBoolean(Conf.getProperty("is.request"));
            boolean isMBean     = Boolean.parseBoolean(Conf.getProperty("is.mbean"));
            boolean isLog       = Boolean.parseBoolean(Conf.getProperty("is.log"));
            isDebug             = Boolean.parseBoolean(Conf.getProperty("is.debug"));

            System.setProperty("agent.server.nm", Conf.getProperty("server.nm"));


            //instrumentation.addTransformer(new TomcatTransformer());
            agentMain = new AgentMain(instrumentation);
            agentMain.start(isRequest, isMBean, isLog);


        }

        isExec				= true;
    }

	/*public static void agentmain(String args, Instrumentation inst) throws Exception {
		System.out.println("--- MonitorAgent agentmain()");
		premain(args, inst);
	}*/

    public AgentMain(Instrumentation instrumentation) {
        this.instrumentation = instrumentation;

        System.out.println("cl: " + System.getProperty("java.class.path"));

    }

    public final void start(boolean isRequest, boolean isMBean, boolean isLog) {


        System.out.println("== EZAPM-AGENT  Service Started. ===");
        System.out.println("| isRequest\t: " + isRequest);
        System.out.println("| isMBean\t: " + isMBean);
        System.out.println("| isDebug\t: " + isDebug);
        System.out.println("| isLog\t\t: " + isLog);
        System.out.println("| author\t: ddakker@naver.com");
        System.out.println("==============================================");

        System.out.println("============================================== 1");
        if (isRequest) {
            System.out.println("============================================== 2");
            setupTransformers(instrumentation);
            System.out.println("============================================== 3");
        }
        if (isMBean) {
            setupTimer();
        }
        if (isLog) {
            setupLogWatch();
        }
        System.out.println("============================================== Exit");

        //Runtime.getRuntime().addShutdownHook(new Test());
        final Runnable stop = new Runnable() {
            @Override
            public void run() {
                agentMain.shutdown();
            }
        };
        Thread thread = new Thread(stop, "stopHookThread");
        Runtime.getRuntime().addShutdownHook(thread);

    }

    private void setupTransformers(Instrumentation paramInstrumentation) {
        this.instrumentation.addTransformer(new ServletTransformer());
    }

    private void setupTimer() {
        MBeanTimer mBeanTimer = new MBeanTimer();
        timer.scheduleAtFixedRate(mBeanTimer, 5000, 5000);
    }

    private void setupLogWatch() {
        //LogWatch logWatch = new LogWatch();
        //Thread thread = new Thread(logWatch);
        //thread.setDaemon(true);
        //thread.start();
    }

    public void shutdown() {
        System.out.println("shutdown NettyClient.nettyClientThread: " + NettyClient.nettyClientThread);
        if (NettyClient.nettyClientThread != null) {
            NettyClient.nettyClientThread.interrupt();
        }
        /*System.out.println("agent shutdown() S agentMain: " + agentMain);
        System.out.println("agent shutdown() AgentMain.nettyClientThread: " + AgentMain.nettyClientThread + ", threads.size: " + threads.size());
        System.out.println("agent shutdown() agentMain: " + agentMain + ", agents.size: " + agents.size());
        AgentMain.nettyClientThread.interrupt();
        System.out.println("agent shutdown() E");*/
    }

	/*public static void start() {
		new java.util.Timer().schedule(new com.ezwel.monitor.agent.JobTimer(), 5000, 1000 * 5);
		System.out.println("--- MonitorAgent start()");
		if (timer == null) {
			timer = new Timer();
			System.out.println("timer: " + timer);
			jobTimer = new JobTimer();
			timer.schedule(jobTimer, 1000*10, 1000 * 5);	// 10초 후 부터 5초마다
		}
		System.out.println("--- MonitorAgent start() end timer: " + timer);
	}

	public static void destory() {
		System.out.println("--- MonitorAgent destory() timer: " + timer);
		if (jobTimer != null) {
			jobTimer.cancel();
			jobTimer = null;
		}
		if (timer != null) {
			timer.cancel();
			timer.purge();
			timer = null;
		}
		System.out.println("--- MonitorAgent destory() end timer: " + timer);
	}*/

    public static void main(String[] args) {
        //start();
    }







}