package com.ys.second

import jdk.internal.org.objectweb.asm.AnnotationVisitor
import jdk.internal.org.objectweb.asm.ClassVisitor
import jdk.internal.org.objectweb.asm.MethodVisitor
import jdk.internal.org.objectweb.asm.Opcodes
import org.gradle.api.internal.java.usagecontext.ConfigurationVariantMapping

class SecondClassVisitor extends ClassVisitor implements Opcodes{

    private String mMethodId

    SecondClassVisitor(ClassVisitor cv){
        super(Opcodes.ASM5, cv)
    }

    @Override
    MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        return new InvokerMethodVisitor(super.visitMethod(access, name, desc, signature, exceptions))
    }

    private class InvokerMethodVisitor extends MethodVisitor {

        private boolean mIsInvokerMethod

        InvokerMethodVisitor(MethodVisitor mv){
            super(Opcodes.ASM5, mv)
        }

        @Override
        AnnotationVisitor visitAnnotation(String desc, boolean visible) {
            if (desc == "Lcom/ys/annotation/ForInvoker;") {
                mIsInvokerMethod = true
                return new InvokerAnnotationVisitor(super.visitAnnotation(desc, visible))
            }
            return super.visitAnnotation(desc, visible)
        }

        @Override
        void visitCode() {
            if (mIsInvokerMethod) {
                println("is Invoker method, invoke " + mMethodId)
                List<GlobalManager.InvokerModel> invokerModelList = GlobalManager.mInvokerModelMap.get(mMethodId)
                if(invokerModelList != null){
                    for(GlobalManager.InvokerModel model : invokerModelList){
                        visitMethodInsn(Opcodes.INVOKESTATIC, model.className, model.methodName, "()V", false)
                    }
                }
            }
            super.visitCode()
        }
    }

    private class InvokerAnnotationVisitor extends AnnotationVisitor {

        InvokerAnnotationVisitor(AnnotationVisitor av){
            super(Opcodes.ASM5, av)
        }

        @Override
        void visit(String name, Object value) {
            if (name == "methodId"){
                mMethodId = (String) value
            }
            super.visit(name, value)
        }
    }
}