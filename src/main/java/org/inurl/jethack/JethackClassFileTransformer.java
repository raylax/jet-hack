package org.inurl.jethack;

import org.inurl.jethack.transformer.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;

public class JethackClassFileTransformer implements ClassFileTransformer {

    private Map<String, ClassTransformer> classTransformerMap = new HashMap<>();

    public JethackClassFileTransformer() {
        addClassTransformer(new HttpClientClassTransformer());
        addClassTransformer(new InetAddressClassTransformer());
        addClassTransformer(new BigIntegerClassTransformer());
        addClassTransformer(new VMManagementImplClassTransformer());
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        ClassTransformer classTransformer = classTransformerMap.get(className);
        if (classTransformer == null) {
            return classfileBuffer;
        }
        try {
            return classTransformer.transform(className, classfileBuffer);
        } catch (Exception e) {
            StringWriter w = new StringWriter();
            e.printStackTrace(new PrintWriter(w));
            Logger.info("%n===%n%s%n===%n", w.toString());
            throw e;
        }
    }

    private void addClassTransformer(ClassTransformer classTransformer) {
        classTransformerMap.put(classTransformer.getName(), classTransformer);
    }

}
