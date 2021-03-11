package com.ys.second

class GlobalManager{

    // methodId -> List<InvokerModel>
    public static Map<String, List<InvokerModel>> mInvokerModelMap = new HashMap<>()

    static class InvokerModel {

        String className
        String methodName

        InvokerModel(String className, String methodName) {
            this.className = className
            this.methodName = methodName
        }
    }
}