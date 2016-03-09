package org.ezdevgroup.ezapm.agent;


import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ddakker on 2016-02-26.
 */
public class Logger {
    static PrintWriter pw = null;
    static File logfile = null;

    public static void println(String msg) {
        println(msg, null);
    }
    public static void println(String msg, Throwable t) {
        try {
            if (pw != null) {
                pw.println(msg);
                if (t != null) {
                    pw.println(getStackTrace(t));
                }
                pw.flush();
                return;
            }
        } catch (Throwable e) {
            System.err.println(e.getMessage());
        }
    }


    static Runnable initializer = new Runnable() {

        public void run() {
            System.out.println("111");
            try {
                process();
                Logger.println("2222");
            } catch (Throwable t) {
            }
        }

        private synchronized void process() {
            try {
                File root = new File("./logs");
                if (root.canWrite() == false) {
                    root.mkdirs();
                }
                if (root.canWrite() == false) {
                    return;
                }

                SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
                File file = new File(root, "ezapm-" + formatter.format(new Date()) + ".log");
                System.out.println("file: " + file);
                FileWriter fw = new FileWriter(file, true);
                pw = new PrintWriter(fw);
                logfile = file;
            } catch (Throwable t) {
                System.err.println(t.getMessage());
            }
        }
    };

    private static String getStackTrace(Throwable t) {
        String CRLF = System.getProperty("line.separator");
        StringBuffer sb = new StringBuffer();
        sb.append(t.toString() + CRLF);
        StackTraceElement[] se = t.getStackTrace();
        if (se != null) {
            for (int i = 0; i < se.length; i++) {
                if (se[i] != null) {
                    sb.append("\t" + se[i].toString());
                    if (i != se.length - 1) {
                        sb.append(CRLF);
                    }
                }
            }
        }

        return sb.toString();
    }
}
