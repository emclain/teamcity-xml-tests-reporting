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

package jetbrains.buildServer.xmlReportPlugin.checkstyle;

import jetbrains.buildServer.agent.BuildProgressLogger;
import jetbrains.buildServer.agent.inspections.InspectionInstance;
import jetbrains.buildServer.agent.inspections.InspectionReporter;
import jetbrains.buildServer.xmlReportPlugin.InspectionsReportParser;
import jetbrains.buildServer.xmlReportPlugin.ReportFileContext;
import jetbrains.buildServer.xmlReportPlugin.XmlReportPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.io.File;

/**
 * User: vbedrosova
 * Date: 23.12.2009
 * Time: 16:11:49
 */
public class CheckstyleReportParser extends InspectionsReportParser {
  public static final String TYPE = "checkstyle";

  private File myCurrentReport;
  private String myCurrentFile;

  @Nullable private BuildProgressLogger myLogger;

  public CheckstyleReportParser(@NotNull InspectionReporter inspectionReporter,
                                @NotNull String checkoutDirectory) {
    super(inspectionReporter, checkoutDirectory);
    myCData = new StringBuilder();
  }

  @Override
  public void parse(@NotNull ReportFileContext data) {
    myCurrentReport = data.getFile();
    try {
      myLogger = data.getRequestContext().getLogger();
      doSAXParse(data);
    } catch (SAXParseException spe) {
      data.getRequestContext().getLogger().error(myCurrentReport.getAbsolutePath() + " is not parsable by Checkstyle parser");
    } catch (Exception e) {
      data.getRequestContext().getLogger().exception(e);
    } finally {
      myLogger = null;
      myInspectionReporter.flush();
    }
    data.setProcessedEvents(-1);
  }

//  Handler methods

  @Override
  public void startElement(String uri, String name, String qName, Attributes attributes) throws SAXException {
    if ("checkstyle".equals(name)) {
      XmlReportPlugin.LOG.info(specifyMessage("Parsing report of version " + attributes.getValue("version")));
    } else if ("file".equals(name)) {
      myCurrentFile = resolveSourcePath(attributes.getValue("name"));
    } else if ("error".equals(name)) {
      if (myCurrentFile == null) {
        XmlReportPlugin.LOG.error(specifyMessage("Unexpected report structure: error tag comes outside file tag"));
      }
      reportInspectionType(attributes);

      myCurrentBug = new InspectionInstance();
      myCurrentBug.setFilePath(myCurrentFile);
      myCurrentBug.setLine(getNumber(attributes.getValue("line")));
      myCurrentBug.setMessage(attributes.getValue("message"));
      myCurrentBug.setInspectionId(attributes.getValue("source"));
      processPriority(getPriority(attributes.getValue("severity")));

      myInspectionReporter.reportInspection(myCurrentBug);
    }
  }

  @Override
  public void endElement(String uri, String name, String qName) throws SAXException {
    if ("file".equals(name)) {
      myCurrentFile = null;
    } else if ("exception".equals(name)) {
      final BuildProgressLogger logger = myLogger;
      assert logger != null;
      logger.error("Exception in report " + myCurrentReport.getAbsolutePath() + "\n" + myCData.toString().trim());
    }
    clearCData();
  }

  // Auxiliary methods

  private void reportInspectionType(Attributes attributes) {
    final String source = attributes.getValue("source");
    reportInspectionType(source, source, attributes.getValue("severity"), "From " + source);
  }

  private int getPriority(String severity) {
    if ("error".equals(severity)) {
      return 1;
    } else if ("warning".equals(severity)) {
      return 2;
    } else if ("info".equals(severity)) {
      return 3;
    } else {
      XmlReportPlugin.LOG.error(specifyMessage("Came across illegal severity value: " + severity));
      return 3;
    }
  }

  public String specifyMessage(String message) {
    return "<CheckstyleReportParser> " + message;
  }
}
