package org.ninedragon.ezapm.agent.transformer;

import org.ninedragon.extlib.javassist.*;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

/**
 * Created by ddakker on 2016-02-19.
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
        if (method.getLongName().equals("org.apache.catalina.core.StandardEngineValve.invoke(org.apache.catalina.connector.Request,org.apache.catalina.connector.Response)")) {
            try {
                method.insertBefore(""
                        + "javax.servlet.http.HttpServletRequest request = ((javax.servlet.http.HttpServletRequest)$1);"
                        + "String uri = request.getRequestURI();"
                        + "String jvmRoute = System.getProperty(\"jvmRoute\");"
                        + "String sessionId = request.getSession().getId();"
                        + "long threadId = Thread.currentThread().getId();"
                        + "String ip = request.getRemoteAddr();"
                        + "System.out.println(ip);"
                        + "org.ninedragon.ezapm.send.netty.NettyClient.send(\"{"
                        +       "server: '\" + jvmRoute + \"' "
                        +       ", threadId: '\" + threadId + \"' "
                        +       ", sessionId: '\" + sessionId + \"' "
                        +       ", uri: '\" + uri + \"' "
                        +       ", ip: '\" + ip + \"' "
                        +       ", stTime: '\" + System.currentTimeMillis() + \"' "
                        + "}\");");

                method.insertAfter(""
                        + "javax.servlet.http.HttpServletRequest request = ((javax.servlet.http.HttpServletRequest)$1);"
                        + "javax.servlet.http.HttpServletResponse response = ((javax.servlet.http.HttpServletResponse)$2);"
                        + "String uri = request.getRequestURI();"
                        + "int status = response.getStatus();"
                        + "String jvmRoute = System.getProperty(\"jvmRoute\");"
                        + "long threadId = Thread.currentThread().getId();"
                        + "String ip = request.getRemoteAddr();"
                        + ""
                        + "org.ninedragon.ezapm.send.netty.NettyClient.send(\"{"
                        +       "server: '\" + jvmRoute + \"' "
                        +       ", threadId: '\" + threadId + \"' "
                        +       ", uri: '\" + uri + \"' "
                        +       ", ip: '\" + ip + \"' "
                        +       ", edTime: '\" + System.currentTimeMillis() + \"' "
                        +       ", status: '\" + status + \"' "
                        + "}\");" );
            } catch (Exception e) {
                System.err.println("e: " + e);
            }
        }
    }
}
