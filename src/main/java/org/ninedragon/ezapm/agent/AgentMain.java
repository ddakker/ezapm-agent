package org.ninedragon.ezapm.agent;

import org.ninedragon.ezapm.agent.transformer.ServletTransformer;

import java.lang.instrument.Instrumentation;

/**
 * Created by ddakker on 2016-02-19.
 */
public class AgentMain {
    private static AgentMain agentMain;

    private Instrumentation instrumentation;

    public static boolean isExec 	= false;
    public static boolean isDebug 	= false;

    public static void premain(String args, Instrumentation instrumentation) throws Exception {
        if (isExec == false) {
            boolean isRequest = args != null && args.contains("isRequest=true");
            boolean isMBean = args != null && args.contains("isMBean=true");
            boolean isLog = args != null && args.contains("isLog=true");
            isDebug = args != null && args.contains("isDebug=true");

            //instrumentation.addTransformer(new TomcatTransformer());
            agentMain = new AgentMain(instrumentation);
            agentMain.start(isRequest, isMBean, isLog);
        }

        isExec = true;
    }

    public AgentMain(Instrumentation instrumentation) {
        this.instrumentation = instrumentation;
    }

    public final void start(boolean isRequest, boolean isMBean, boolean isLog) {
        System.out.println("====== DAK-APM-AGENT  Service Started. =======");
        System.out.println("| isRequest\t: " + isRequest);
        System.out.println("| isMBean\t: " + isMBean);
        System.out.println("| isDebug\t: " + isDebug);
        System.out.println("| isLog\t\t: " + isLog);
        System.out.println("| author\t: ddakker@ezwel.com");
        System.out.println("==============================================");

        if (isRequest) {
            this.instrumentation.addTransformer(new ServletTransformer());
        }
        if (isMBean) {
            //setupTimer();
        }
        if (isLog) {
            //setupLogWatch();
        }
    }
}