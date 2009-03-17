/*
 * Copyright 2008 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jetbrains.buildServer.xmlReportPlugin;

import jetbrains.buildServer.agent.inspections.InspectionInstance;
import jetbrains.buildServer.agent.inspections.InspectionReporter;
import jetbrains.buildServer.agent.inspections.InspectionTypeInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;


public final class TestUtil {
  static public String readFile(@NotNull final File file) throws IOException {
    final FileInputStream inputStream = new FileInputStream(file);
    try {
      final BufferedInputStream bis = new BufferedInputStream(inputStream);
      final byte[] bytes = new byte[(int) file.length()];
      bis.read(bytes);
      bis.close();

      return new String(bytes);
    }
    finally {
      inputStream.close();
    }
  }

  public static String getTestDataPath(final String fileName, final String folderName) throws FileNotFoundException {
    return getTestDataFile(fileName, folderName).getPath();
  }

  public static String getAbsoluteTestDataPath(@Nullable final String fileName, @NotNull final String folderName) throws FileNotFoundException {
    return getTestDataFile(fileName, folderName).getAbsolutePath();
  }

  public static File getTestDataFile(final String fileName, final String folderName) throws FileNotFoundException {
    final String relativeFileName = "tests/testData" + (folderName != null ? "/" + folderName : "") + (fileName != null ? "/" + fileName : "");

    File file1 = new File(relativeFileName);

    if (file1.exists()) {
      return file1;
    }

    File file2 = new File("svnrepo/xml-tests-reporting/" + relativeFileName);
    if (file2.exists()) {
      return file2;
    }

    throw new FileNotFoundException("Either " + file1.getAbsolutePath() + " or file " + file2.getAbsolutePath() + " should exist.");
  }

  public static String getRelativePath(final File f, final File base) {
    if (f.getAbsolutePath().startsWith(base.getAbsolutePath())) {
      return f.getAbsolutePath().substring(base.getAbsolutePath().length() + 1);  //+1 for truncating trasiling "/"
    }
    return f.getAbsolutePath();
  }

  public static InspectionReporter createFakeReporter(final StringBuilder results) {
    return new InspectionReporter() {
      public void reportInspection(@NotNull final InspectionInstance inspection) {
        results.append(inspection.toString()).append("\n");
      }

      public void reportInspectionType(@NotNull final InspectionTypeInfo inspectionType) {
        results.append(inspectionType.toString()).append("\n");
      }

      public void markBuildAsInspectionsBuild() {
      }

      public void flush() {
      }
    };
  }
}