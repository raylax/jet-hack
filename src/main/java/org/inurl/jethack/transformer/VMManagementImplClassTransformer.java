package org.inurl.jethack.transformer;

import org.inurl.jethack.Checker;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

public class VMManagementImplClassTransformer implements ClassTransformer {
    @Override
    public String getName() {
        return "sun/management/VMManagementImpl";
    }

    @Override
    public byte[] transform(String clazz, byte[] bytes) {
        ClassNode node = ClassUtils.parse(bytes);
        for (MethodNode mn : node.methods) {
            if (!"getVmArguments".equals(mn.name) || !"()Ljava/util/List;".equals(mn.desc)) {
                continue;
            }
            InsnList list = new InsnList();

            list.add(new VarInsnNode(Opcodes.ALOAD, 0));
            list.add(new FieldInsnNode(Opcodes.GETFIELD, "sun/management/VMManagementImpl", "vmArgs", "Ljava/util/List;"));

            list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Checker.NAME, "checkVmArgs", "(Ljava/util/List;)Ljava/util/List;", false));

            list.add(new VarInsnNode(Opcodes.ALOAD, 0));
            list.add(new FieldInsnNode(Opcodes.PUTFIELD, "sun/management/VMManagementImpl", "vmArgs", "Ljava/util/List;"));

            Iterator<AbstractInsnNode> it = mn.instructions.iterator();
            while (it.hasNext()) {
                AbstractInsnNode in = it.next();
                if (AbstractInsnNode.INSN == in.getType() && Opcodes.ARETURN == in.getOpcode()) {
                    mn.instructions.insert(in.getPrevious().getPrevious(), list);
                    break;
                }
            }
            logMethodRedefined(mn);
        }

        return ClassUtils.toBytes(node);
    }
}
