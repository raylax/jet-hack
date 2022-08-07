package org.inurl.jethack.transformer;

import org.inurl.jethack.Checker;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class InetAddressClassTransformer implements ClassTransformer {
    @Override
    public String getName() {
        return "java/net/InetAddress";
    }

    @Override
    public byte[] transform(String clazz, byte[] bytes) {
        ClassNode node = ClassUtils.parse(bytes);

        for (MethodNode mn : node.methods) {

            if ("getAllByName".equals(mn.name) && "(Ljava/lang/String;Ljava/net/InetAddress;)[Ljava/net/InetAddress;".equals(mn.desc)) {
                InsnList insns = new InsnList();
                insns.add(new VarInsnNode(Opcodes.ALOAD, 0));
                insns.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Checker.NAME, "checkDnsQuery", "(Ljava/lang/String;)V", false));

                mn.instructions.insert(insns);
                logMethodRedefined(mn);
            }

            if ("isReachable".equals(mn.name) && "(Ljava/net/NetworkInterface;II)Z".equals(mn.desc)) {
                InsnList insns = new InsnList();
                insns.add(new VarInsnNode(Opcodes.ALOAD, 0));
                insns.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Checker.NAME, "checkDnsQuery", "(Ljava/net/InetAddress;)Z", false));

                insns.add(new InsnNode(Opcodes.ICONST_1));
                LabelNode ret = new LabelNode();
                insns.add(new JumpInsnNode(Opcodes.IFEQ, ret));
                insns.add(new InsnNode(Opcodes.ICONST_0));
                insns.add(new InsnNode(Opcodes.IRETURN));
                insns.add(ret);

                mn.instructions.insert(insns);
                logMethodRedefined(mn);
            }
        }

        return ClassUtils.toBytes(node);
    }
}
