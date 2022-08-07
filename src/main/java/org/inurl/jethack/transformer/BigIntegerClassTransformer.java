package org.inurl.jethack.transformer;

import org.inurl.jethack.Checker;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class BigIntegerClassTransformer implements ClassTransformer {
    @Override
    public String getName() {
        return "java/math/BigInteger";
    }

    @Override
    public byte[] transform(String clazz, byte[] bytes) {
        ClassNode node = ClassUtils.parse(bytes);
        for (MethodNode mn : node.methods) {
            if (!"oddModPow".equals(mn.name) || !"(Ljava/math/BigInteger;Ljava/math/BigInteger;)Ljava/math/BigInteger;".equals(mn.desc)) {
                continue;
            }
            InsnList insns = new InsnList();

            /* --- ARG --- */
            insns.add(new VarInsnNode(Opcodes.ALOAD, 1));
            insns.add(new VarInsnNode(Opcodes.ALOAD, 2));
            insns.add(new MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    Checker.NAME,
                    "checkArgs",
                    "(Ljava/math/BigInteger;Ljava/math/BigInteger;)[Ljava/math/BigInteger;",
                    false
            ));
            insns.add(new InsnNode(Opcodes.DUP));

            insns.add(new InsnNode(Opcodes.ICONST_0));
            insns.add(new InsnNode(Opcodes.AALOAD));
            insns.add(new VarInsnNode(Opcodes.ASTORE, 1));

            insns.add(new InsnNode(Opcodes.ICONST_1));
            insns.add(new InsnNode(Opcodes.AALOAD));
            insns.add(new VarInsnNode(Opcodes.ASTORE, 2));
            /* --- ARG --- */

            /* --- RESULT --- */
            insns.add(new VarInsnNode(Opcodes.ALOAD, 0));
            insns.add(new VarInsnNode(Opcodes.ALOAD, 1));
            insns.add(new VarInsnNode(Opcodes.ALOAD, 2));
            insns.add(new MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    Checker.NAME,
                    "checkResult",
                    "(Ljava/math/BigInteger;Ljava/math/BigInteger;Ljava/math/BigInteger;)Ljava/math/BigInteger;",
                    false
            ));

            insns.add(new InsnNode(Opcodes.DUP));
            LabelNode ret = new LabelNode();
            insns.add(new JumpInsnNode(Opcodes.IFNULL, ret));
            insns.add(new InsnNode(Opcodes.ARETURN));
            insns.add(ret);
            insns.add(new InsnNode(Opcodes.POP));
            /* --- RESULT --- */

            mn.instructions.insert(insns);
            logMethodRedefined(mn);
            break;
        }


        return ClassUtils.toBytes(node);
    }
}
