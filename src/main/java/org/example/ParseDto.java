package org.example;
import java.util.List;

public class ParseDto {
    private String progName;
    private String progNo;
    private String packageName;
    private String className;
    private String classDescription;

    public String getPackageName() {
        if(packageName == null) { packageName = ""; }
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public List<Method> methods;

    public class Method {

        private String methodName;
        private String methodVisible;
        private String methodDesc;

        public String getMethodName() {
            if(methodName == null) { methodName = ""; }
            return methodName;
        }

        public void setMethodName(String methodName) {
            this.methodName = methodName;
        }

        public String getMethodVisible() {
            if(methodVisible == null) { methodVisible = ""; }
            return methodVisible;
        }

        public void setMethodVisible(String methodVisible) {
            this.methodVisible = methodVisible;
        }

        public String getMethodDesc() {
            if(methodDesc == null) { methodDesc = ""; }
            return methodDesc;
        }

        public void setMethodDesc(String methodDesc) {
            this.methodDesc = methodDesc;
        }
    }
    public String getProgName() {
        if(progName == null) { progName = ""; }
        return progName;
    }

    public void setProgName(String progName) {
        this.progName = progName;
    }

    public String getProgNo() {
        if(progNo == null) { progNo = ""; }
        return progNo;
    }

    public void setProgNo(String progNo) {
        this.progNo = progNo;
    }

    public String getClassName() {
        if(className == null) { className = ""; }
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassDescription() {
        if(classDescription == null) { classDescription = ""; }
        return classDescription;
    }

    public void setClassDescription(String classDescription) {
        this.classDescription = classDescription;
    }

    public List<Method> getMethods() {
        return methods;
    }

    public void setMethods(List<Method> methods) {
        this.methods = methods;
    }

    @Override
    public String toString() {



        return "ParseDto{" +
                "progName='" + progName + '\'' +
                ", progNo='" + progNo + '\'' +
                ", className='" + className + '\'' +
                ", classDescription='" + classDescription + '\'' +
                '}';
    }
}
