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
package jetbrains.buildServer.testReportParserPlugin.antJUnit;


public class TestData {
  private final String myClassName;
  private final String myTestName;

  private final long myStartTime;
  private final long myDuration;

  private String myFailureType;
  private String myFailureMessage;

  public TestData(final String className,
                  final String testName,
                  final long startTime,
                  final long duration) {
    myClassName = className;
    myTestName = testName;
    myStartTime = startTime;
    myDuration = duration;
  }

  public String getClassName() {
    return myClassName;
  }

  public String getTestName() {
    return myTestName;
  }

  public long getStartTime() {
    return myStartTime;
  }

  public long getDuration() {
    return myDuration;
  }

  public String getFailureMessage() {
    return myFailureMessage;
  }

  public void setFailureMessage(String message) {
    myFailureMessage = message;
  }

  public String getFailureType() {
    return myFailureType;
  }

  public void setFailureType(String type) {
    myFailureType = type;
  }

  public boolean isFailure() {
    return (myFailureType != null);
  }
}