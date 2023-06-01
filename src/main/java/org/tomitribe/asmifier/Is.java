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

import java.io.File;
import java.io.FileFilter;

public interface Is {
    public static class Zip implements FileFilter {
        public Zip() {
        }

        public boolean accept(File pathname) {
            return pathname.isFile() && accept(pathname.getName());
        }

        public static boolean accept(String path) {
            if (path.endsWith(".zip")) {
                return true;
            } else if (!path.endsWith("ar")) {
                return false;
            } else {
                return path.endsWith(".jar") || path.endsWith(".ear") || path.endsWith(".war") || path.endsWith(".rar");
            }
        }
    }

    public static class Clazz implements FileFilter {
        public Clazz() {
        }

        public boolean accept(File pathname) {
            return pathname.isFile() && accept(pathname.getName());
        }

        public static boolean accept(String path) {
            return path.endsWith(".class");
        }
    }

    public static class Scannable implements FileFilter {
        private Clazz clazz = new Clazz();
        private Zip zip = new Zip();

        public Scannable() {
        }

        public boolean accept(File pathname) {
            return this.zip.accept(pathname) || this.clazz.accept(pathname);
        }
    }
}
