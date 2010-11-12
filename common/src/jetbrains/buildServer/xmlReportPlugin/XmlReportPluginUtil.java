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

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class XmlReportPluginUtil {
  public static final Map<String, String> SUPPORTED_REPORT_TYPES = new HashMap<String, String>();
  public static final List<String> INSPECTIONS_TYPES = new LinkedList<String>();
  public static final List<String> DUPLICATES_TYPES = new LinkedList<String>();

  static {
    SUPPORTED_REPORT_TYPES.put("junit", "Ant JUnit");
    SUPPORTED_REPORT_TYPES.put("nunit", "NUnit");
    SUPPORTED_REPORT_TYPES.put("surefire", "Surefire");
    SUPPORTED_REPORT_TYPES.put("findBugs", "FindBugs");
    SUPPORTED_REPORT_TYPES.put("pmd", "PMD");
    SUPPORTED_REPORT_TYPES.put("checkstyle", "Checkstyle");
    SUPPORTED_REPORT_TYPES.put("pmdCpd", "PMD CPD");

    INSPECTIONS_TYPES.add("findBugs");
    INSPECTIONS_TYPES.add("pmd");
    INSPECTIONS_TYPES.add("checkstyle");

    DUPLICATES_TYPES.add("pmdCpd");
  }

  public static final String REPORT_TYPE = "xmlReportParsing.reportType";
  public static final String REPORT_DIRS = "xmlReportParsing.reportDirs";
  public static final String VERBOSE_OUTPUT = "xmlReportParsing.verboseOutput";
  public static final String PARSE_OUT_OF_DATE = "xmlReportParsing.parse.outofdate";
  public static final String BUILD_START = "xmlReportParsing.buildStart";
  public static final String TMP_DIR = "xmlReportParsing.tmpDir";
  public static final String MAX_ERRORS = "xmlReportParsing.max.errors";
  public static final String MAX_WARNINGS = "xmlReportParsing.max.warnings";
  public static final String FINDBUGS_HOME = "xmlReportParsing.findBugs.home";
  public static final String WHEN_NO_DATA_PUBLISHED = "xmlReportParsing.whenNoDataPublished";
  public static final String LOG_AS_INTERNAL = "xmlReportParsing.logAsInternal";

  public static boolean isParsingEnabled(@NotNull final Map<String, String> runParams) {
    return runParams.containsKey(REPORT_TYPE) && !"".equals(runParams.get(REPORT_TYPE));
  }

  public static boolean isOutputVerbose(@NotNull final Map<String, String> runParams) {
    return runParams.containsKey(VERBOSE_OUTPUT);
  }

  public static boolean shouldParseOutOfDateReports(@NotNull final Map<String, String> systemProperties) {
    return systemProperties.containsKey(PARSE_OUT_OF_DATE);
  }

  public static void enableXmlReportParsing(@NotNull final Map<String, String> runParams, String reportType) {
    if (reportType.equals("")) {
      runParams.remove(REPORT_TYPE);
      runParams.remove(REPORT_DIRS);
      runParams.remove(VERBOSE_OUTPUT);
      runParams.remove(PARSE_OUT_OF_DATE);
    } else {
      runParams.put(REPORT_TYPE, reportType);
    }
  }

  public static void setVerboseOutput(@NotNull final Map<String, String> runParams, boolean verboseOutput) {
    if (isParsingEnabled(runParams)) {
      if (verboseOutput) {
        runParams.put(VERBOSE_OUTPUT, "true");
      } else {
        runParams.remove(VERBOSE_OUTPUT);
      }
    }
  }

  public static void setParseOutOfDateReports(@NotNull final Map<String, String> systemProperties, boolean shouldParse) {
    if (shouldParse) {
      systemProperties.put(PARSE_OUT_OF_DATE, "true");
    } else {
      systemProperties.remove(PARSE_OUT_OF_DATE);
    }
  }

  public static void setWhenNoDataPublished(@NotNull final Map<String, String> systemProperties, String value) {
    systemProperties.put(WHEN_NO_DATA_PUBLISHED, value);
  }

  public static void setXmlReportPaths(@NotNull final Map<String, String> runParams, String reportDirs) {
    if (isParsingEnabled(runParams)) {
      runParams.put(REPORT_DIRS, reportDirs);
    }
  }

  public static String getXmlReportPaths(@NotNull final Map<String, String> runParams) {
    return runParams.get(REPORT_DIRS);
  }

  public static String getReportType(@NotNull final Map<String, String> runParams) {
    return runParams.get(REPORT_TYPE);
  }

  public static void setReportType(@NotNull final Map<String, String> runParams, String type) {
    if (isParsingEnabled(runParams)) {
      runParams.put(REPORT_TYPE, type);
    }
  }

  public static int getMaxErrors(@NotNull final Map<String, String> runParams) {
    return getMaxErrorsOrWarnings(runParams, MAX_ERRORS);
  }

  public static void setMaxErrors(@NotNull final Map<String, String> runParams, int maxErrors) {
    if (isParsingEnabled(runParams)) {
      runParams.put(MAX_ERRORS, "" + maxErrors);
    }
  }

  public static int getMaxWarnings(@NotNull final Map<String, String> runParams) {
    return getMaxErrorsOrWarnings(runParams, MAX_WARNINGS);
  }

  public static void setMaxWarnings(@NotNull final Map<String, String> runParams, int maxWarnings) {
    if (isParsingEnabled(runParams)) {
      runParams.put(MAX_WARNINGS, "" + maxWarnings);
    }
  }

  private static int getMaxErrorsOrWarnings(@NotNull final Map<String, String> runParams, String what) {
    if (runParams.containsKey(what)) {
      try {
        return Integer.parseInt(runParams.get(what));
      } catch (NumberFormatException e) {
        return -1;
      }
    }
    return -1;
  }

  public static boolean isInspection(String type) {
    return INSPECTIONS_TYPES.contains(type);
  }

  public static String getFindBugsHomePath(@NotNull final Map<String, String> runParams) {
    return runParams.get(FINDBUGS_HOME);
  }

  public static boolean isDuplication(String type) {
    return DUPLICATES_TYPES.contains(type);
  }
}