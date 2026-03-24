package ru.geraskindenis.transformer;

import org.objectweb.asm.*;
import ru.geraskindenis.settings.models.ClassInfo;

import java.lang.instrument.*;
import java.security.ProtectionDomain;
import java.util.*;
import java.util.logging.Logger;

public class ClassTransformer implements ClassFileTransformer {

    private final Logger log;

    private final Map<String, ClassInfo> classesMap;

    public ClassTransformer(Map<String, ClassInfo> classesMap) {
        this.classesMap = classesMap;
        log = Logger.getLogger(ClassTransformer.class.getName());
    }

    @Override
    public byte[] transform(ClassLoader loader, String className,
                            Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain,
                            byte[] classfileBuffer) {

        ClassInfo classInfo = classesMap.get(className);
        if (Objects.isNull(classInfo)) {
            return null;
        }

        log.info("[Agent] Transforming class: " + className);

        ClassReader cr = new ClassReader(classfileBuffer);
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        ClassVisitor cv = new ClassVisitor(Opcodes.ASM9, cw) {
            private boolean fieldsAdded = false;
            private String classInternalName;

            @Override
            public void visit(int version, int access, String name, String signature,
                              String superName, String[] interfaces) {
                super.visit(version, access, name, signature, superName, interfaces);
                this.classInternalName = name;
            }

            @Override
            public FieldVisitor visitField(int access, String name, String descriptor,
                                           String signature, Object value) {
                if (name.equals("startTime") || name.equals("duration") ||
                        name.equals("methodCounters") || name.equals("needToSave")) {
                    fieldsAdded = true;
                }
                return super.visitField(access, name, descriptor, signature, value);
            }

            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor,
                                             String signature, String[] exceptions) {
                MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
                ClassInfo.MethodInfo methodInfo = classInfo.getMethods().stream()
                        .filter(m -> m.getName().equals(name)).findAny().orElseGet(() -> null);
                if (Objects.nonNull(methodInfo)) {
                    log.info("[Agent] Transforming method: " + name);

                    return new MethodVisitor(Opcodes.ASM9, mv) {
                        @Override
                        public void visitCode() {
                            super.visitCode();
                            Label elseLabel = new Label();
                            Label endLabel = new Label();
                            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
                            mv.visitFieldInsn(Opcodes.GETSTATIC, classInternalName, "startTime", "J");
                            mv.visitInsn(Opcodes.LSUB);
                            mv.visitFieldInsn(Opcodes.GETSTATIC, classInternalName, "duration", "J");
                            mv.visitInsn(Opcodes.LCMP);
                            mv.visitJumpInsn(Opcodes.IFGE, elseLabel); // если >=, переход на else
                            mv.visitMethodInsn(Opcodes.INVOKESTATIC, classInternalName, "getMethodCounters",
                                    "()Ljava/util/concurrent/ConcurrentHashMap;", false);
                            mv.visitLdcInsn(name);
                            mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                                    "ru/geraskindenis/agent/MonitoringHelper",
                                    "increment",
                                    "(Ljava/util/concurrent/ConcurrentHashMap;Ljava/lang/String;)V",
                                    false);
                            mv.visitJumpInsn(Opcodes.GOTO, endLabel);

                            mv.visitLabel(elseLabel);
                            mv.visitMethodInsn(Opcodes.INVOKESTATIC, classInternalName, "saveDataIfNeeded", "()V", false);
                            mv.visitLabel(endLabel);
                        }
                    };
                }
                return mv;
            }

            @Override
            public void visitEnd() {
                if (!fieldsAdded) {
                    FieldVisitor fv = cw.visitField(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL,
                            "startTime", "J", null, null);
                    fv.visitEnd();
                    fv = cw.visitField(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL,
                            "duration", "J", null, classInfo.getDuration());
                    fv.visitEnd();
                    fv = cw.visitField(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC,
                            "methodCounters", "Ljava/util/concurrent/ConcurrentHashMap;",
                            "Ljava/util/concurrent/ConcurrentHashMap<Ljava/lang/String;Ljava/util/concurrent/atomic/AtomicLong;>;", null);
                    fv.visitEnd();
                    fv = cw.visitField(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC,
                            "needToSave", "Ljava/util/concurrent/atomic/AtomicBoolean;", null, null);
                    fv.visitEnd();
                    MethodVisitor mv = cw.visitMethod(Opcodes.ACC_STATIC, "<clinit>", "()V", null, null);
                    mv.visitCode();
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
                    mv.visitFieldInsn(Opcodes.PUTSTATIC, classInternalName, "startTime", "J");
                    mv.visitInsn(Opcodes.RETURN);
                    mv.visitMaxs(0, 0);
                    mv.visitEnd();
                }

                generateGetMethodCounters(cw, classInternalName);
                generateGetNeedToSave(cw, classInternalName);
                generateSaveDataIfNeeded(cw, classInternalName);
                generateSaveData(cw, classInternalName);

                super.visitEnd();
            }

            private void generateGetMethodCounters(ClassWriter cw, String className) {
                MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC,
                        "getMethodCounters", "()Ljava/util/concurrent/ConcurrentHashMap;",
                        "()Ljava/util/concurrent/ConcurrentHashMap<Ljava/lang/String;Ljava/util/concurrent/atomic/AtomicLong;>;", null);
                mv.visitCode();

                Label start = new Label();
                mv.visitLabel(start);
                mv.visitFieldInsn(Opcodes.GETSTATIC, className, "methodCounters", "Ljava/util/concurrent/ConcurrentHashMap;");
                mv.visitVarInsn(Opcodes.ASTORE, 0);
                mv.visitVarInsn(Opcodes.ALOAD, 0);
                Label nonNull = new Label();
                mv.visitJumpInsn(Opcodes.IFNONNULL, nonNull);

                mv.visitLdcInsn(Type.getObjectType(className));
                mv.visitInsn(Opcodes.DUP);
                mv.visitInsn(Opcodes.MONITORENTER);

                mv.visitFieldInsn(Opcodes.GETSTATIC, className, "methodCounters", "Ljava/util/concurrent/ConcurrentHashMap;");
                mv.visitVarInsn(Opcodes.ASTORE, 0);
                mv.visitVarInsn(Opcodes.ALOAD, 0);
                Label alreadyCreated = new Label();
                mv.visitJumpInsn(Opcodes.IFNONNULL, alreadyCreated);

                mv.visitTypeInsn(Opcodes.NEW, "java/util/concurrent/ConcurrentHashMap");
                mv.visitInsn(Opcodes.DUP);
                mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/util/concurrent/ConcurrentHashMap", "<init>", "()V", false);
                mv.visitVarInsn(Opcodes.ASTORE, 0);
                mv.visitVarInsn(Opcodes.ALOAD, 0);
                mv.visitFieldInsn(Opcodes.PUTSTATIC, className, "methodCounters", "Ljava/util/concurrent/ConcurrentHashMap;");

                mv.visitLabel(alreadyCreated);
                mv.visitInsn(Opcodes.MONITOREXIT);
                mv.visitJumpInsn(Opcodes.GOTO, nonNull);

                Label catchBlock = new Label();
                mv.visitTryCatchBlock(start, alreadyCreated, catchBlock, null);
                mv.visitLabel(catchBlock);
                mv.visitLdcInsn(Type.getObjectType(className));
                mv.visitInsn(Opcodes.MONITOREXIT);
                mv.visitInsn(Opcodes.ATHROW);

                mv.visitLabel(nonNull);
                mv.visitVarInsn(Opcodes.ALOAD, 0);
                mv.visitInsn(Opcodes.ARETURN);

                mv.visitMaxs(0, 0);
                mv.visitEnd();
            }

            private void generateGetNeedToSave(ClassWriter cw, String className) {
                MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC,
                        "getNeedToSave", "()Ljava/util/concurrent/atomic/AtomicBoolean;", null, null);
                mv.visitCode();

                Label start = new Label();
                mv.visitLabel(start);
                mv.visitFieldInsn(Opcodes.GETSTATIC, className, "needToSave", "Ljava/util/concurrent/atomic/AtomicBoolean;");
                mv.visitVarInsn(Opcodes.ASTORE, 0);
                mv.visitVarInsn(Opcodes.ALOAD, 0);
                Label nonNull = new Label();
                mv.visitJumpInsn(Opcodes.IFNONNULL, nonNull);

                mv.visitLdcInsn(Type.getObjectType(className));
                mv.visitInsn(Opcodes.DUP);
                mv.visitInsn(Opcodes.MONITORENTER);

                mv.visitFieldInsn(Opcodes.GETSTATIC, className, "needToSave", "Ljava/util/concurrent/atomic/AtomicBoolean;");
                mv.visitVarInsn(Opcodes.ASTORE, 0);
                mv.visitVarInsn(Opcodes.ALOAD, 0);
                Label alreadyCreated = new Label();
                mv.visitJumpInsn(Opcodes.IFNONNULL, alreadyCreated);

                mv.visitTypeInsn(Opcodes.NEW, "java/util/concurrent/atomic/AtomicBoolean");
                mv.visitInsn(Opcodes.DUP);
                mv.visitInsn(Opcodes.ICONST_1);
                mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/util/concurrent/atomic/AtomicBoolean", "<init>", "(Z)V", false);
                mv.visitVarInsn(Opcodes.ASTORE, 0);
                mv.visitVarInsn(Opcodes.ALOAD, 0);
                mv.visitFieldInsn(Opcodes.PUTSTATIC, className, "needToSave", "Ljava/util/concurrent/atomic/AtomicBoolean;");

                mv.visitLabel(alreadyCreated);
                mv.visitInsn(Opcodes.MONITOREXIT);
                mv.visitJumpInsn(Opcodes.GOTO, nonNull);

                Label catchBlock = new Label();
                mv.visitTryCatchBlock(start, alreadyCreated, catchBlock, null);
                mv.visitLabel(catchBlock);
                mv.visitLdcInsn(Type.getObjectType(className));
                mv.visitInsn(Opcodes.MONITOREXIT);
                mv.visitInsn(Opcodes.ATHROW);

                mv.visitLabel(nonNull);
                mv.visitVarInsn(Opcodes.ALOAD, 0);
                mv.visitInsn(Opcodes.ARETURN);

                mv.visitMaxs(0, 0);
                mv.visitEnd();
            }

            private void generateSaveDataIfNeeded(ClassWriter cw, String className) {
                MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC,
                        "saveDataIfNeeded", "()V", null, null);
                mv.visitCode();
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, className, "getNeedToSave", "()Ljava/util/concurrent/atomic/AtomicBoolean;", false);
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/concurrent/atomic/AtomicBoolean", "get", "()Z", false);
                Label elseLabel = new Label();
                mv.visitJumpInsn(Opcodes.IFEQ, elseLabel);
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, className, "getNeedToSave", "()Ljava/util/concurrent/atomic/AtomicBoolean;", false);
                mv.visitInsn(Opcodes.ICONST_1);
                mv.visitInsn(Opcodes.ICONST_0);
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/concurrent/atomic/AtomicBoolean", "compareAndSet", "(ZZ)Z", false);
                Label afterIf = new Label();
                mv.visitJumpInsn(Opcodes.IFEQ, afterIf);
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, className, "saveData", "()V", false);
                mv.visitLabel(afterIf);
                mv.visitLabel(elseLabel);
                mv.visitInsn(Opcodes.RETURN);
                mv.visitMaxs(0, 0);
                mv.visitEnd();
            }

            private void generateSaveData(ClassWriter cw, String className) {
                MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC,
                        "saveData", "()V", null, null);
                mv.visitCode();
                mv.visitLdcInsn(className.replace('/', '.'));
                mv.visitFieldInsn(Opcodes.GETSTATIC, className, "startTime", "J");
                mv.visitFieldInsn(Opcodes.GETSTATIC, className, "duration", "J");
                mv.visitFieldInsn(Opcodes.GETSTATIC, className, "methodCounters", "Ljava/util/concurrent/ConcurrentHashMap;");
                mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                        "ru/geraskindenis/agent/MonitoringHelper",
                        "saveData",
                        "(Ljava/lang/String;JJLjava/util/concurrent/ConcurrentHashMap;)V",
                        false);
                mv.visitInsn(Opcodes.RETURN);
                mv.visitMaxs(0, 0);
                mv.visitEnd();
            }
        };
        cr.accept(cv, 0);
        return cw.toByteArray();
    }
}