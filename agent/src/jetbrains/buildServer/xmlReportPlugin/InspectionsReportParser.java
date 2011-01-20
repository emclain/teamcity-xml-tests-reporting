/*
 * Copyright 2000-2011 JetBrains s.r.o.
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

import jetbrains.buildServer.agent.inspections.*;
import jetbrains.buildServer.messages.Status;
import jetbrains.buildServer.messages.serviceMessages.BuildStatus;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import static jetbrains.buildServer.xmlReportPlugin.XmlReportPlugin.LOG;


public abstract class InspectionsReportParser extends XmlReportParser {
  protected final InspectionReporter myInspectionReporter;
  protected final String myCheckoutDirectory;
  private final Set<String> myReportedInstanceTypes;

  private int myErrors;
  private int myWarnings;
  private int myInfos;

  private int myTotalErrors;
  private int myTotalWarnings;
  private int myTotalInfos;

  protected InspectionInstance myCurrentBug;

  protected InspectionsReportParser(@NotNull InspectionReporter inspectionReporter,
                                    @NotNull String checkoutDirectory) {
    super();
    myInspectionReporter = inspectionReporter;
    myCheckoutDirectory = checkoutDirectory.replace("\\", File.separator).replace("/", File.separator);
    myReportedInstanceTypes = new HashSet<String>();
  }

  private static String generateBuildStatus(int errors, int warnings, int infos) {
    return "Errors: " + errors + ", warnings: " + warnings + ", information: " + infos;
  }

  @Override
  public void logReportTotals(@NotNull ReportContext context, boolean verbose) {
    String message = context.getFile().getPath() + " report processed";
    if (myErrors > 0) {
      message = message.concat(": " + myErrors + " error(s)");
    }
    if (myWarnings > 0) {
      message = message.concat(": " + myWarnings + " warning(s)");
    }
    if (myInfos > 0) {
      message = message.concat(": " + myInfos + " info message(s)");
    }
    if (verbose) {
      context.getLogger().message(message);
    }
    LOG.debug(message);
    myTotalErrors += myErrors;
    myTotalWarnings += myWarnings;
    myTotalInfos += myInfos;
    myErrors = 0;
    myWarnings = 0;
    myInfos = 0;
  }

  @Override
  protected void logParsingTotals(@NotNull final XmlReportPluginParameters parameters) {
    boolean limitReached = false;

    final int errorLimit = XmlReportPluginUtil.getMaxErrors(parameters.getRunnerParameters());
    if ((errorLimit != -1) && (myTotalErrors > errorLimit)) {
      parameters.getThreadLogger().error("Errors limit reached: found " + myTotalErrors + " errors, limit " + errorLimit);
      limitReached = true;
    }

    final int warningLimit = XmlReportPluginUtil.getMaxWarnings(parameters.getRunnerParameters());
    if ((warningLimit != -1) && (myTotalWarnings > warningLimit)) {
      parameters.getThreadLogger().error("Warnings limit reached: found " + myTotalWarnings + " warnings, limit " + warningLimit);
      limitReached = true;
    }

    if (limitReached) {
      parameters.getThreadLogger().message(new BuildStatus(generateBuildStatus(myTotalErrors, myTotalWarnings, myTotalInfos), Status.FAILURE).asString());
    }
  }

  protected void processPriority(int priority) {
    InspectionSeverityValues level;
    switch (priority) {
      case 1:
        ++myErrors;
        level = InspectionSeverityValues.ERROR;
        break;
      case 2:
        ++myWarnings;
        level = InspectionSeverityValues.WARNING;
        break;
      default:
        ++myInfos;
        level = InspectionSeverityValues.INFO;
    }
    final Collection<String> attrValue = new Vector<String>();
    attrValue.add(level.toString());
    myCurrentBug.addAttribute(InspectionAttributesId.SEVERITY.toString(), attrValue);
  }

  protected void reportInspectionType(String id, String name, String category, String descr) {
    if (myReportedInstanceTypes.contains(id)) {
      return;
    }
    final InspectionTypeInfo type = new InspectionTypeInfo();
    type.setId(id);
    type.setName(name);
    type.setCategory(category);
    type.setDescription(descr);
    myInspectionReporter.reportInspectionType(type);
    myReportedInstanceTypes.add(id);
  }

  protected String resolveSourcePath(String path) {
    path = path.replace("\\", File.separator).replace("/", File.separator);
    if (path.startsWith(myCheckoutDirectory)) {
      path = path.substring(myCheckoutDirectory.length());
    }
    if (path.startsWith(File.separator)) {
      path = path.substring(1);
    }
    return path.replace(File.separator, "/");
  }

  @Override
  public abstract void parse(@NotNull ReportContext context) throws Exception;
}
