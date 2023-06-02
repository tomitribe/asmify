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

import org.tomitribe.crest.api.Command;
import org.tomitribe.util.Files;
import org.tomitribe.util.IO;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class AsmifierCommand {

    /**
     * Read the specified File as a class and output the ASM code required to create that class via ASM.
     *
     * The output of the command is Java code that uses the ASM APIs to build a class definition that would
     * be identical to the class file specified.  This allows for a text-searchable representation of the bytecode,
     * which can be very useful for analyzing the impact of bytecode transformation tools.
     *
     * All output is written to the console
     *
     * IMPORTANT
     *
     * This command is not a decompiler.
     *
     * It should be noted that not all bytecode can be translated to Java syntax.  This includes bytecode
     * created by the Groovy, Scala, Kotlin and similar compilers.  Similarly, one Java source file can result in
     * several class files.  It is not the goal of this tool to attempt to erase or hide the class files.
     *
     * EXAMPLE
     *
     * To view the asmified output of a class file in the target directory of a Maven build
     *
     *     asmify class target/classes/org/example/colors/Orange.class
     *
     * RELATED
     *
     * To process an entire zip file
     *
     *     asmify zip target/myapp.war /tmp/foo
     *
     * @param file path to a class file to parse and output as ASM
     */
    @Command("class")
    public String asmifyClass(final File file) throws IOException {
        return Asmifier.asmify(IO.readBytes(file));
    }

    /**
     * Read the specified zip archive and output all contents to the specified directory.
     *
     * The contents of the zip will be extracted into a new folder in the output directory.
     * The new folder will be named after the zip file, which means it is possible to asmify
     * several zip files into the same directory.  It also means there is no need to manually
     * create a directory named after the zip -- this tool will create that for you.
     *
     * As the tool iterates over the zip file, each zip entry will be handled as follows:
     *
     *  - class entries: contents of the class file will be parsed into asmified format and written
     *    to the output directory with a ".asm" extension.  It is safe to view these files in an IDE
     *    as Java files and associate the ".asm" extension in your IDE accordingly.
     *  - file entries: entries that are unknown will be extracted as-is into the output directory.
     *  - nested zip entries: entries with a well-known zip extension (zip, jar, war, ear, rar) will
     *    be recursively extracted into the output directory using the above logic.
     *
     * PARAMETERS
     *
     *  - File: The path to a zip, jar, war, ear or similar file to parse and output as ASM.
     *  - Directory: The directory where all files will be extracted and converted to ASM.
     *
     * IMPORTANT
     *
     * This command is not a decompiler.
     *
     * It should be noted that not all bytecode can be translated to Java syntax.  This includes bytecode
     * created by the Groovy, Scala, Kotlin and similar compilers.  Similarly, one Java source file can result in
     * several class files.  It is not the goal of this tool to attempt to erase or hide the class files.
     *
     * EXAMPLE
     *
     * To view the asmified output of a zip file and output that into a directory in /tmp/
     *
     *     mkdir /tmp/foo
     *     asmify zip target/myapp.war /tmp/foo
     *
     * The above would result in a new directory called `/tmp/foo/myapp.war` containing the extracted
     * and asmified contents of `target/myapp.war`
     *
     * RELATED
     *
     *  To output an individual class file
     *
     *     asmify class target/classes/org/example/colors/Orange.class
     *
     * @param zip path to a zip, jar, war, ear or similar file to parse and output as ASM
     * @param directory the directory where all files will be extracted and converted to ASM
     */
    @Command("zip")
    public void zip(final File zip, final Directory directory) throws Exception {
        final File output = directory.get();

        try {
            Files.exists(zip);
            Files.file(zip);
            Files.readable(zip);

            Files.exists(output);
            Files.dir(output);
            Files.readable(output);
            Files.writable(output);
        } catch (Exception e) {
            throw new ExitWithMessageException(e);
        }

        final File dest = new File(output, zip.getName());

        final InputStream in = IO.read(zip);

        processZip(in, dest);
    }

    private static void processZip(final InputStream in, final File dest) throws IOException {
        final ZipInputStream zipInputStream = new ZipInputStream(in);

        ZipEntry entry;
        while ((entry = zipInputStream.getNextEntry()) != null) {
            final String path = entry.getName();

            if (path.endsWith(".class")) {

                final String asmified = Asmifier.asmify(IO.readBytes(zipInputStream));

                final File file = new File(dest, path + ".asm");
                Files.mkdirs(file.getParentFile());
                IO.copy(IO.read(asmified), file);

            } else if (isZip(path)) {

                processZip(zipInputStream, new File(dest, path));

            } else {
                // is a resource file

                final byte[] bytes = IO.readBytes(zipInputStream);
                if (bytes.length > 0) {
                    final File file = new File(dest, path);
                    Files.mkdirs(file.getParentFile());
                    IO.copy(IO.read(bytes), file);
                }
            }
        }
    }

    private static boolean isZip(final String path) {
        return Is.Zip.accept(path);
    }

    public static class Zip {
        private final File file;

        public Zip(final File file) {
            this.file = file;
        }

        public File get() {
            return file;
        }

        public static Directory from(final String fileName) {
            return new Directory(new File(fileName));
        }
    }

    public static class Directory {
        private final File file;

        public Directory(final File file) {
            this.file = file;
        }

        public File get() {
            return file;
        }

        public static Directory from(final String fileName) {
            return new Directory(new File(fileName));
        }
    }
}
