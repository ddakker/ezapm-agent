/*
 * Copyright 2013 The Netty Project
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

package org.ezdevgroup.extlib.io.netty.util.internal;

import java.lang.reflect.Method;

import org.ezdevgroup.extlib.io.netty.util.internal.logging.InternalLogger;
import org.ezdevgroup.extlib.io.netty.util.internal.logging.InternalLoggerFactory;
import org.ezdevgroup.extlib.javassist.ClassClassPath;
import org.ezdevgroup.extlib.javassist.ClassPath;
import org.ezdevgroup.extlib.javassist.ClassPool;
import org.ezdevgroup.extlib.javassist.CtClass;
import org.ezdevgroup.extlib.javassist.Modifier;
import org.ezdevgroup.extlib.javassist.NotFoundException;

public final class JavassistTypeParameterMatcherGenerator {

    private static final InternalLogger logger =
            InternalLoggerFactory.getInstance(JavassistTypeParameterMatcherGenerator.class);

    private static final ClassPool classPool = new ClassPool(true);

    static {
        classPool.appendClassPath(new ClassClassPath(NoOpTypeParameterMatcher.class));
    }

    public static void appendClassPath(ClassPath classpath) {
        classPool.appendClassPath(classpath);
    }

    public static void appendClassPath(String pathname) throws NotFoundException {
        classPool.appendClassPath(pathname);
    }

    public static TypeParameterMatcher generate(Class<?> type) {
        ClassLoader classLoader = PlatformDependent.getContextClassLoader();
        if (classLoader == null) {
            classLoader = PlatformDependent.getSystemClassLoader();
        }
        return generate(type, classLoader);
    }

    public static TypeParameterMatcher generate(Class<?> type, ClassLoader classLoader) {
        final String typeName = typeName(type);
        final String className = "org.ninedragon.extlib.io.netty.util.internal.__matchers__." + typeName + "Matcher";
        try {
            try {
                return (TypeParameterMatcher) Class.forName(className, true, classLoader).newInstance();
            } catch (Exception e) {
                // Not defined in the specified class loader.
            }

            CtClass c = classPool.getAndRename(NoOpTypeParameterMatcher.class.getName(), className);
            c.setModifiers(c.getModifiers() | Modifier.FINAL);
            c.getDeclaredMethod("match").setBody("{ return $1 instanceof " + typeName + "; }");
            byte[] byteCode = c.toBytecode();
            c.detach();
            Method method = ClassLoader.class.getDeclaredMethod(
                    "defineClass", String.class, byte[].class, int.class, int.class);
            method.setAccessible(true);

            Class<?> generated = (Class<?>) method.invoke(classLoader, className, byteCode, 0, byteCode.length);
            if (type != Object.class) {
                logger.debug("Generated: {}", generated.getName());
            } else {
                // Object.class is only used when checking if Javassist is available.
            }
            return (TypeParameterMatcher) generated.newInstance();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String typeName(Class<?> type) {
        if (type.isArray()) {
            return typeName(type.getComponentType()) + "[]";
        }

        return type.getName();
    }

    private JavassistTypeParameterMatcherGenerator() { }
}
