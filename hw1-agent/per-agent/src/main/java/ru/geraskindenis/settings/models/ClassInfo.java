package ru.geraskindenis.settings.models;

import java.util.List;
import java.util.Objects;

public class ClassInfo {

    private String name;

    private String packageInfo;

    private long duration;

    private List<MethodInfo> methods;

    public ClassInfo() {
    }

    public ClassInfo(String name, String packageInfo, long duration) {
        this.name = name;
        this.packageInfo = packageInfo;
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageInfo() {
        return packageInfo;
    }

    public void setPackageInfo(String packageInfo) {
        this.packageInfo = packageInfo;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public List<MethodInfo> getMethods() {
        return methods;
    }

    public void setMethods(List<MethodInfo> methods) {
        this.methods = methods;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ClassInfo classInfo = (ClassInfo) o;
        return duration == classInfo.duration && Objects.equals(name, classInfo.name)
                && Objects.equals(packageInfo, classInfo.packageInfo)
                && Objects.equals(methods, classInfo.methods);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, packageInfo, duration, methods);
    }

    public static class MethodInfo {

        private String name;

        public MethodInfo() {
        }

        public MethodInfo(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            MethodInfo that = (MethodInfo) o;
            return Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(name);
        }
    }
}
