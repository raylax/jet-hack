package org.inurl.jethack.transformer;

import org.inurl.jethack.Checker;
import org.inurl.jethack.Logger;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class HttpClientClassTransformer implements ClassTransformer {

    @Override
    public String getName() {
        return "sun/net/www/http/HttpClient";
    }

    @Override
    public byte[] transform(String clazz, byte[] bytes) {
        ClassNode node = ClassUtils.parse(bytes);
        for (MethodNode mn : node.methods) {
            if (!"openServer".equals(mn.name) || !"()V".equals(mn.desc)) {
                continue;
            }
            Logger.info("Redefining %s#%s%s", getName(), mn.name, mn.desc);
            InsnList insns = new InsnList();
            insns.add(new VarInsnNode(Opcodes.ALOAD, 0));
            insns.add(new FieldInsnNode(Opcodes.GETFIELD, getName(), "url", "Ljava/net/URL;"));
            insns.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Checker.NAME, "checkURL", "(Ljava/net/URL;)V", false));
            mn.instructions.insert(insns);
            break;
        }

        return ClassUtils.toBytes(node);
    }

}
