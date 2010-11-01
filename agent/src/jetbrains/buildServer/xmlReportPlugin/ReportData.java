/*
 * Copyright 2000-2010 JetBrains s.r.o.
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

import java.io.File;
import org.jetbrains.annotations.NotNull;


public final class ReportData {
  private final File myFile;
  private int myProcessedEvents;
  private long myFileLength;
  private final String myType;
  private final File myImportRequestPath;

  public ReportData(@NotNull final File file, @NotNull String type, @NotNull final File importRequestPath) {
    myFile = file;
    myFileLength = 0L;
    myProcessedEvents = 0;
    myType = type;
    myImportRequestPath = importRequestPath;
  }

  @NotNull
  public File getFile() {
    return myFile;
  }

  public int getProcessedEvents() {
    return myProcessedEvents;
  }

  public void setProcessedEvents(int tests) {
    myProcessedEvents = tests;
  }

  public long getFileLength() {
    return myFileLength;
  }

  public void setFileLength(long fileLength) {
    myFileLength = fileLength;
  }

  @NotNull
  public String getType() {
    return myType;
  }

  @NotNull
  public File getImportRequestPath() {
    return myImportRequestPath;
  }
}
