/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tomitribe.asmifier;

import org.junit.Test;
import org.tomitribe.crest.api.Command;
import org.tomitribe.crest.api.Editor;
import org.tomitribe.util.Archive;
import org.tomitribe.util.Files;
import org.tomitribe.util.IO;
import org.tomitribe.util.Join;

import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class AsmifierCommandTest {

    @Test
    public void testZip() throws Exception {
        final Archive one = Archive.archive()
                .add(URI.class)
                .add(IO.class)
                .add(Command.class)
                .add("META-INF/orange.txt", "Orange is rare and beautiful")
                .add("META-INF/green.txt", "Green is abundant and still beautiful");

        final Archive two = Archive.archive()
                .add("one.jar", one)
                .add(Editor.class)
                .add(Objects.class)
                .add("some/long/path/text.file.txt", "Some pig!")
                .add("another path + /spaces.txt", "Abcdefghijklmnopqrstuvwxyz");

        final File src = Files.tmpdir();
        final File dest = Files.tmpdir();

        final File jar = two.toJar(new File(src, "widget.zip"));

        new AsmifierCommand().zip(jar, new AsmifierCommand.Directory(dest));

        final List<File> files = Files.collect(dest).stream()
                .filter(File::isFile)
                .collect(Collectors.toList());

        final Join.NameCallback<File> getName = file -> file.getAbsolutePath().substring(dest.getAbsolutePath().length());
        assertEquals("/widget.zip/org/tomitribe/crest/api/Editor.class.asm\n" +
                "/widget.zip/java/util/Objects.class.asm\n" +
                "/widget.zip/one.jar/org/tomitribe/util/IO.class.asm\n" +
                "/widget.zip/one.jar/org/tomitribe/crest/api/Command.class.asm\n" +
                "/widget.zip/one.jar/java/net/URI.class.asm\n" +
                "/widget.zip/one.jar/META-INF/orange.txt\n" +
                "/widget.zip/one.jar/META-INF/green.txt\n" +
                "/widget.zip/another path + /spaces.txt\n" +
                "/widget.zip/some/long/path/text.file.txt", Join.join("\n", getName, files));

        assertEquals("Abcdefghijklmnopqrstuvwxyz", IO.slurp(new File(dest, "/widget.zip/another path + /spaces.txt")));
        assertEquals("package asm.org.tomitribe.crest.api;\n" +
                "import org.objectweb.asm.AnnotationVisitor;\n" +
                "import org.objectweb.asm.Attribute;\n" +
                "import org.objectweb.asm.ClassReader;\n" +
                "import org.objectweb.asm.ClassWriter;\n" +
                "import org.objectweb.asm.ConstantDynamic;\n" +
                "import org.objectweb.asm.FieldVisitor;\n" +
                "import org.objectweb.asm.Handle;\n" +
                "import org.objectweb.asm.Label;\n" +
                "import org.objectweb.asm.MethodVisitor;\n" +
                "import org.objectweb.asm.Opcodes;\n" +
                "import org.objectweb.asm.RecordComponentVisitor;\n" +
                "import org.objectweb.asm.Type;\n" +
                "import org.objectweb.asm.TypePath;\n" +
                "public class CommandDump implements Opcodes {\n" +
                "\n" +
                "public static byte[] dump () throws Exception {\n" +
                "\n" +
                "ClassWriter classWriter = new ClassWriter(0);\n" +
                "FieldVisitor fieldVisitor;\n" +
                "RecordComponentVisitor recordComponentVisitor;\n" +
                "MethodVisitor methodVisitor;\n" +
                "AnnotationVisitor annotationVisitor0;\n" +
                "\n" +
                "classWriter.visit(V1_8, ACC_PUBLIC | ACC_ANNOTATION | ACC_ABSTRACT | ACC_INTERFACE, \"org/tomitribe/crest/api/Command\", null, \"java/lang/Object\", new String[] { \"java/lang/annotation/Annotation\" });\n" +
                "\n" +
                "{\n" +
                "annotationVisitor0 = classWriter.visitAnnotation(\"Ljava/lang/annotation/Retention;\", true);\n" +
                "annotationVisitor0.visitEnum(\"value\", \"Ljava/lang/annotation/RetentionPolicy;\", \"RUNTIME\");\n" +
                "annotationVisitor0.visitEnd();\n" +
                "}\n" +
                "{\n" +
                "annotationVisitor0 = classWriter.visitAnnotation(\"Ljava/lang/annotation/Target;\", true);\n" +
                "{\n" +
                "AnnotationVisitor annotationVisitor1 = annotationVisitor0.visitArray(\"value\");\n" +
                "annotationVisitor1.visitEnum(null, \"Ljava/lang/annotation/ElementType;\", \"METHOD\");\n" +
                "annotationVisitor1.visitEnum(null, \"Ljava/lang/annotation/ElementType;\", \"TYPE\");\n" +
                "annotationVisitor1.visitEnd();\n" +
                "}\n" +
                "annotationVisitor0.visitEnd();\n" +
                "}\n" +
                "{\n" +
                "methodVisitor = classWriter.visitMethod(ACC_PUBLIC | ACC_ABSTRACT, \"value\", \"()Ljava/lang/String;\", null, null);\n" +
                "{\n" +
                "annotationVisitor0 = methodVisitor.visitAnnotationDefault();\n" +
                "annotationVisitor0.visit(null, \"\");\n" +
                "annotationVisitor0.visitEnd();\n" +
                "}\n" +
                "methodVisitor.visitEnd();\n" +
                "}\n" +
                "{\n" +
                "methodVisitor = classWriter.visitMethod(ACC_PUBLIC | ACC_ABSTRACT, \"usage\", \"()Ljava/lang/String;\", null, null);\n" +
                "{\n" +
                "annotationVisitor0 = methodVisitor.visitAnnotationDefault();\n" +
                "annotationVisitor0.visit(null, \"\");\n" +
                "annotationVisitor0.visitEnd();\n" +
                "}\n" +
                "methodVisitor.visitEnd();\n" +
                "}\n" +
                "{\n" +
                "methodVisitor = classWriter.visitMethod(ACC_PUBLIC | ACC_ABSTRACT, \"interceptedBy\", \"()[Ljava/lang/Class;\", \"()[Ljava/lang/Class<*>;\", null);\n" +
                "{\n" +
                "annotationVisitor0 = methodVisitor.visitAnnotationDefault();\n" +
                "{\n" +
                "AnnotationVisitor annotationVisitor1 = annotationVisitor0.visitArray(null);\n" +
                "annotationVisitor1.visitEnd();\n" +
                "}\n" +
                "annotationVisitor0.visitEnd();\n" +
                "}\n" +
                "methodVisitor.visitEnd();\n" +
                "}\n" +
                "classWriter.visitEnd();\n" +
                "\n" +
                "return classWriter.toByteArray();\n" +
                "}\n" +
                "}\n", IO.slurp(new File(dest, "/widget.zip/one.jar/org/tomitribe/crest/api/Command.class.asm")));
    }

    @Test
    public void testClass() throws Exception {
        final Archive archive = Archive.archive()
                .add(Command.class);

        final File tmpdir = Files.tmpdir();
        archive.toDir(tmpdir);

        final File classFile = new File(tmpdir, Command.class.getName().replace('.', '/') + ".class");

        final String output = new AsmifierCommand().asmifyClass(classFile);

        assertEquals("package asm.org.tomitribe.crest.api;\n" +
                "import org.objectweb.asm.AnnotationVisitor;\n" +
                "import org.objectweb.asm.Attribute;\n" +
                "import org.objectweb.asm.ClassReader;\n" +
                "import org.objectweb.asm.ClassWriter;\n" +
                "import org.objectweb.asm.ConstantDynamic;\n" +
                "import org.objectweb.asm.FieldVisitor;\n" +
                "import org.objectweb.asm.Handle;\n" +
                "import org.objectweb.asm.Label;\n" +
                "import org.objectweb.asm.MethodVisitor;\n" +
                "import org.objectweb.asm.Opcodes;\n" +
                "import org.objectweb.asm.RecordComponentVisitor;\n" +
                "import org.objectweb.asm.Type;\n" +
                "import org.objectweb.asm.TypePath;\n" +
                "public class CommandDump implements Opcodes {\n" +
                "\n" +
                "public static byte[] dump () throws Exception {\n" +
                "\n" +
                "ClassWriter classWriter = new ClassWriter(0);\n" +
                "FieldVisitor fieldVisitor;\n" +
                "RecordComponentVisitor recordComponentVisitor;\n" +
                "MethodVisitor methodVisitor;\n" +
                "AnnotationVisitor annotationVisitor0;\n" +
                "\n" +
                "classWriter.visit(V1_8, ACC_PUBLIC | ACC_ANNOTATION | ACC_ABSTRACT | ACC_INTERFACE, \"org/tomitribe/crest/api/Command\", null, \"java/lang/Object\", new String[] { \"java/lang/annotation/Annotation\" });\n" +
                "\n" +
                "{\n" +
                "annotationVisitor0 = classWriter.visitAnnotation(\"Ljava/lang/annotation/Retention;\", true);\n" +
                "annotationVisitor0.visitEnum(\"value\", \"Ljava/lang/annotation/RetentionPolicy;\", \"RUNTIME\");\n" +
                "annotationVisitor0.visitEnd();\n" +
                "}\n" +
                "{\n" +
                "annotationVisitor0 = classWriter.visitAnnotation(\"Ljava/lang/annotation/Target;\", true);\n" +
                "{\n" +
                "AnnotationVisitor annotationVisitor1 = annotationVisitor0.visitArray(\"value\");\n" +
                "annotationVisitor1.visitEnum(null, \"Ljava/lang/annotation/ElementType;\", \"METHOD\");\n" +
                "annotationVisitor1.visitEnum(null, \"Ljava/lang/annotation/ElementType;\", \"TYPE\");\n" +
                "annotationVisitor1.visitEnd();\n" +
                "}\n" +
                "annotationVisitor0.visitEnd();\n" +
                "}\n" +
                "{\n" +
                "methodVisitor = classWriter.visitMethod(ACC_PUBLIC | ACC_ABSTRACT, \"value\", \"()Ljava/lang/String;\", null, null);\n" +
                "{\n" +
                "annotationVisitor0 = methodVisitor.visitAnnotationDefault();\n" +
                "annotationVisitor0.visit(null, \"\");\n" +
                "annotationVisitor0.visitEnd();\n" +
                "}\n" +
                "methodVisitor.visitEnd();\n" +
                "}\n" +
                "{\n" +
                "methodVisitor = classWriter.visitMethod(ACC_PUBLIC | ACC_ABSTRACT, \"usage\", \"()Ljava/lang/String;\", null, null);\n" +
                "{\n" +
                "annotationVisitor0 = methodVisitor.visitAnnotationDefault();\n" +
                "annotationVisitor0.visit(null, \"\");\n" +
                "annotationVisitor0.visitEnd();\n" +
                "}\n" +
                "methodVisitor.visitEnd();\n" +
                "}\n" +
                "{\n" +
                "methodVisitor = classWriter.visitMethod(ACC_PUBLIC | ACC_ABSTRACT, \"interceptedBy\", \"()[Ljava/lang/Class;\", \"()[Ljava/lang/Class<*>;\", null);\n" +
                "{\n" +
                "annotationVisitor0 = methodVisitor.visitAnnotationDefault();\n" +
                "{\n" +
                "AnnotationVisitor annotationVisitor1 = annotationVisitor0.visitArray(null);\n" +
                "annotationVisitor1.visitEnd();\n" +
                "}\n" +
                "annotationVisitor0.visitEnd();\n" +
                "}\n" +
                "methodVisitor.visitEnd();\n" +
                "}\n" +
                "classWriter.visitEnd();\n" +
                "\n" +
                "return classWriter.toByteArray();\n" +
                "}\n" +
                "}\n", output);
    }


}