package com.ys.second

import jdk.internal.org.objectweb.asm.AnnotationVisitor
import jdk.internal.org.objectweb.asm.ClassVisitor
import jdk.internal.org.objectweb.asm.MethodVisitor
import jdk.internal.org.objectweb.asm.Opcodes

class FirstClassVisitor extends ClassVisitor{

    private String mClassName
    private String mMethodId

    FirstClassVisitor(ClassVisitor cv){
        super(Opcodes.ASM5, cv)
    }

    @Override
    void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.mClassName = name
        super.visit(version, access, name, signature, superName, interfaces)
    }

    @Override
    MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        return new InvokerMethodVisitor(name, super.visitMethod(access, name, desc, signature, exceptions))
    }

    private class InvokerMethodVisitor extends MethodVisitor {

        private String mMethodName
        private boolean mIsInvokerMethod

        InvokerMethodVisitor(String methodName, MethodVisitor mv){
            super(Opcodes.ASM5, mv)
            this.mMethodName = methodName
        }

        @Override
        AnnotationVisitor visitAnnotation(String desc, boolean visible) {
            if (desc == "Lcom/ys/annotation/InvokeBy;") {
                mIsInvokerMethod = true
                return new InvokerAnnotationVisitor(super.visitAnnotation(desc, visible))
            }
            return super.visitAnnotation(desc, visible)
        }

        @Override
        void visitCode() {
            if (mIsInvokerMethod) {
                println("is invoker method, " + mMethodId + " " + mClassName + " " + mMethodName)
                List<GlobalManager.InvokerModel> invokerModelList = GlobalManager.mInvokerModelMap.get(mMethodId)
                if(invokerModelList == null){
                    invokerModelList = new ArrayList<>();
                }
                invokerModelList.add(new GlobalManager.InvokerModel(mClassName, mMethodName))
                GlobalManager.mInvokerModelMap.put(mMethodId, invokerModelList)
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