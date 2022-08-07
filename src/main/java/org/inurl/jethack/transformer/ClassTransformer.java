package org.inurl.jethack.transformer;

import org.inurl.jethack.Logger;
import org.objectweb.asm.tree.MethodNode;

public interface ClassTransformer {

    String getName();

    byte[] transform(String clazz, byte[] bytes);

    default void logMethodRedefined(MethodNode mn) {
        Logger.info("Redefined %s#%s%s", getName(), mn.name, mn.desc);
    }

}
