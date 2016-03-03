package org.ninedragon.ezapm.agent.transformer;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import org.ninedragon.extlib.javassist.CannotCompileException;
import org.ninedragon.extlib.javassist.ClassPool;
import org.ninedragon.extlib.javassist.CtBehavior;
import org.ninedragon.extlib.javassist.CtClass;
import org.ninedragon.extlib.javassist.LoaderClassPath;
import org.ninedragon.extlib.javassist.NotFoundException;

/**
 * Tomcat 요청 가로채서 수정!!
 * @author ddakker 2015. 6. 14.
 */
public class ServletTransformer implements ClassFileTransformer {
    ClassPool pool = null;
    public ServletTransformer() {
        this.pool = ClassPool.getDefault();
    }

    public byte[] transform(ClassLoader loader, String className, Class redefiningClass, ProtectionDomain domain, byte[] bytes) throws IllegalClassFormatException {
		/*if (className.contains("StandardEngineValve")) {
			System.out.println("className: " + className);
			return transformClass(redefiningClass, bytes);
		} else
		*/
        if (className.equals("javax/servlet/http/HttpServlet")) {
            //if (className.equals("org/apache/catalina/core/ApplicationFilterChain")) {
            //if (className.equals("org/apache/catalina/core/StandardWrapperValve")) {
            //if (className.equals("org/apache/tomcat/websocket/server/WsFilter")) {
            //if (className.contains("org/apache/catalina/core/StandardEngineValve")) {
            System.out.println("className: " + className);
            return transformClass(redefiningClass, bytes);
        } else if (className.equals("org/apache/catalina/core/StandardEngineValve")) {
            System.out.println("className: " + className);
            return transformClass(redefiningClass, bytes);
        } else if (className.equals("org/apache/jasper/servlet/JspServlet")) {
            System.out.println("className: " + className);
            return transformClass(redefiningClass, bytes);
        } else {
            return bytes;
        }
    }

    private byte[] transformClass(Class classToTransform, byte[] b) {
        CtClass cl = null;
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader(); // or something else

            ClassPool pool = ClassPool.getDefault();
            pool.insertClassPath(new LoaderClassPath(classLoader));
            if (pool != null) {
                cl = pool.makeClass(new java.io.ByteArrayInputStream(b));
                if (cl.isInterface() == false) {
                    CtBehavior[] methods = cl.getDeclaredBehaviors();
                    for (int i = 0; i < methods.length; i++) {
                        System.out.println("methods[" + i + "].getLongName(): " + methods[i].getLongName());
                        if (methods[i].isEmpty() == false) {
                            doTransform(methods[i]);
                        }
                    }
                }
                b = cl.toBytecode();
            }
        } catch (Exception e) {
            System.err.println("e222: " + e);
        } finally {
            if (cl != null) {
                cl.detach();
            }
        }
        return b;
    }

    private void doTransform(CtBehavior method) throws NotFoundException, CannotCompileException {
        //if (method.getLongName().equals("javax.servlet.http.HttpServlet.service(javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse)")) {
        //if (method.getLongName().equals("javax.servlet.http.HttpServlet.service(javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse)") ||
        //		method.getLongName().equals("org.apache.jasper.servlet.JspServlet.service(javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse)")) {
        //if (method.getLongName().equals("org.apache.catalina.core.ApplicationFilterChain.doFilter(javax.servlet.ServletRequest,javax.servlet.ServletResponse)")) {
        //if (method.getLongName().equals("org.apache.catalina.core.StandardWrapperValve.invoke(org.apache.catalina.connector.Request,org.apache.catalina.connector.Response)")) {
        //if (method.getLongName().contains("doFilter")) {
        if (method.getLongName().equals("org.apache.catalina.core.StandardEngineValve.invoke(org.apache.catalina.connector.Request,org.apache.catalina.connector.Response)")) {
            System.out.println("--- tomcat start");
            //methods[i].insertBefore("System.out.println(\"==== tomcat start\"); com.ezwel.monitor.agent.MonitorAgent.start(); System.out.println(\"==== tomcat start end\");");
            //methods[i].insertBefore("System.out.println(\"==== tomcat start\"); new java.util.Timer().schedule(new com.ezwel.monitor.agent.JobTimer(), 5000, 1000 * 5); System.out.println(\"==== tomcat start end\");");
            //methods[i].insertBefore("System.out.println(\"==== test 1: \" + ((javax.servlet.http.HttpServletRequest)$1).getRequestURI());");
            try {
                method.insertBefore(""
                        + "javax.servlet.http.HttpServletRequest request = ((javax.servlet.http.HttpServletRequest)$1);"
                        //+ "org.apache.catalina.connector.Request request = $1;"
                        + "String uri = request.getRequestURI();"
                        + "String jvmRoute = System.getProperty(\"agent.server.nm\");"
                        + "String sessionId = request.getSession().getId();"
                        + "long threadId = Thread.currentThread().getId();"
                        + "String ip = request.getRemoteAddr();"
                        + "org.ninedragon.ezapm.agent.send.netty.NettyClient.send(org.ninedragon.ezapm.agent.send.netty.NettyClient.GRP_WAS_REQ, \"{"
                        +       "server: '\" + jvmRoute + \"' "
                        +       ", threadId: '\" + threadId + \"' "
                        +       ", sessionId: '\" + sessionId + \"' "
                        +       ", uri: '\" + uri + \"' "
                        +       ", ip: '\" + ip + \"' "
                        +       ", stTime: '\" + System.currentTimeMillis() + \"' "
                        + "}\");"
                );

                method.insertAfter(""
                        + "javax.servlet.http.HttpServletRequest request = ((javax.servlet.http.HttpServletRequest)$1);"
                        + "javax.servlet.http.HttpServletResponse response = ((javax.servlet.http.HttpServletResponse)$2);"
                        //+ "org.apache.catalina.connector.Request request = $1;"
                        //+ "org.apache.catalina.connector.Response response = $2;"
                        + "String uri = request.getRequestURI();"
                        + "int status = response.getStatus();"
                        + "System.out.println(status);"
                        + "String jvmRoute = System.getProperty(\"agent.server.nm\");"
                        + "long threadId = Thread.currentThread().getId();"
                        + "String ip = request.getRemoteAddr();"
                        + ""
                        + "org.ninedragon.ezapm.agent.send.netty.NettyClient.send(org.ninedragon.ezapm.agent.send.netty.NettyClient.GRP_WAS_REQ, \"{"
                        +       "server: '\" + jvmRoute + \"' "
                        +       ", threadId: '\" + threadId + \"' "
                        +       ", uri: '\" + uri + \"' "
                        +       ", ip: '\" + ip + \"' "
                        +       ", edTime: '\" + System.currentTimeMillis() + \"' "
                        +       ", status: '\" + status + \"' "
                        +       "}\");"
                );
            } catch (Exception e) {
                System.err.println("Aa e: " + e);
            }
        }
    }

	/*private byte[] transformClass(Class classToTransform, byte[] b) {
		CtClass cl = null;
		try {
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader(); // or something else

			ClassPool pool = ClassPool.getDefault();
			pool.insertClassPath(new LoaderClassPath(classLoader));
			if (pool != null) {
				cl = pool.makeClass(new java.io.ByteArrayInputStream(b));
				if (cl.isInterface() == false) {
					CtBehavior[] methods = cl.getDeclaredBehaviors();
					for (int i = 0; i < methods.length; i++) {
						System.out.println("methods[" + i + "]: " + methods[i]);
						if (methods[i].isEmpty() == false) {
							doTransform(methods[i]);
						}
					}
				}
				b = cl.toBytecode();
			}
		} catch (Exception e) {
			System.err.println("e111: " + e);
		} finally {
			if (cl != null) {
				cl.detach();
			}
		}
		return b;
	}

	private void doTransform(CtBehavior method) throws NotFoundException, CannotCompileException {
		if (method.getName().equals("invoke")) {
			try {
				method.insertBefore(""
						+ "String uri = $1.getRequestURI();"
						+ "if (uri.indexOf(\"/resources\") == 0 || uri.indexOf(\"/favicon.ico\") == 0) {"
						+ "} else {"
						+ "String jvmRoute = System.getProperty(\"jvmRoute\");"
						+ "String sessionId = $1.getSession().getId();"
						+ "String ip = $1.getRemoteAddr();"
						+ "com.ezwel.monitor.agent.send.VertxClient.send(\"{"
						+ "server: '\" + jvmRoute + \"' "
						+ ", sessionId: '\" + sessionId + \"' "
						+ ", uri: '\" + uri + \"' "
						+ ", ip: '\" + ip + \"' "
						+ ", stTime: '\" + System.currentTimeMillis() + \"' "
						+ "}\");"
						+ "}");

				method.insertAfter(""
						//+ "new Thread(new Runnable() {"
						//+ "		@Override"
						//+ "		public void run() {"
						+ "System.out.println($2.getStatus());"
						//+ "if ($2.getStatus() == 200) {"
						+ "	String uri = $1.getRequestURI();"
						+ "		if (uri.indexOf(\"/resources\") == 0 || uri.indexOf(\"/favicon.ico\") == 0) {"
						+ "		} else {"
						+ "			String jvmRoute = System.getProperty(\"jvmRoute\");"
						+ "			String sessionId = $1.getSession().getId();"
						+ "			String ip = $1.getRemoteAddr();"
						+ "			com.ezwel.monitor.agent.send.VertxClient.send(\"{"
						+ "				server: '\" + jvmRoute + \"' "
						+ "				, sessionId: '\" + sessionId + \"' "
						+ "				, uri: '\" + uri + \"' "
						+ "				, ip: '\" + ip + \"' "
						+ "				, edTime: '\" + System.currentTimeMillis() + \"' "
						+ "				, status: '\" + $2.getStatus() + \"' "
						+ "			}\");"
						+ "}"
						//+ "}"
						//+ "		}"
						//+ "}).start();"
						);
			} catch (Exception e) {
				System.err.println("Aa e: " + e);
			}
		}

	}*/

}