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

package jetbrains.buildServer.xmlReportPlugin.pmd;

import jetbrains.buildServer.agent.BuildProgressLogger;
import jetbrains.buildServer.agent.inspections.InspectionInstance;
import jetbrains.buildServer.agent.inspections.InspectionReporter;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.xmlReportPlugin.InspectionsReportParser;
import jetbrains.buildServer.xmlReportPlugin.ReportData;
import org.jetbrains.annotations.NotNull;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.io.File;


public class PmdReportParser extends InspectionsReportParser {
  public static final String TYPE = "pmd";
  private static final String DEFAULT_MESSAGE = "No message";
  private static final String TRAILING_TAG = "</pmd>";

  private String myCurrentFile;

  public PmdReportParser(@NotNull final BuildProgressLogger logger,
                         @NotNull InspectionReporter inspectionReporter,
                         @NotNull String checkoutDirectory) {
    super(logger, inspectionReporter, checkoutDirectory);
    myCData = new StringBuilder();
  }

  @Override
  public void parse(@NotNull final ReportData data) {
    final File report = data.getFile();
    if (!isReportComplete(report, TRAILING_TAG)) {
      Loggers.AGENT.debug("The report doesn't finish with " + TRAILING_TAG);
      data.setProcessedEvents(0);
      return;
    }
    try {
      parse(report);
    } catch (SAXParseException spe) {
      myLogger.error(report.getAbsolutePath() + " is not parsable by PMD parser");
    } catch (Exception e) {
      myLogger.exception(e);
    } finally {
      myInspectionReporter.flush();
    }
    data.setProcessedEvents(-1);
  }

  //  Handler methods

  @Override
  public void startElement(String uri, String localName,
                           String qName, Attributes attributes) throws SAXException {
    if ("file".equals(localName)) {
      myCurrentFile = resolveSourcePath(attributes.getValue("name"));
    } else if ("violation".equals(localName)) {
      myCurrentBug = new InspectionInstance();
      myCurrentBug.setLine(getNumber(attributes.getValue("beginline")));
      myCurrentBug.setInspectionId(attributes.getValue("rule"));
      myCurrentBug.setMessage(DEFAULT_MESSAGE);
      myCurrentBug.setFilePath(myCurrentFile);
      reportInspectionType(attributes);
      processPriority(getNumber(attributes.getValue("priority")));
    }
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    if ("violation".equals(localName)) {
      myCurrentBug.setMessage(formatText(myCData));
      myInspectionReporter.reportInspection(myCurrentBug);
    }
    clearCData();
  }

  // Auxiliary methods

  private void reportInspectionType(Attributes attributes) {
    final String id = attributes.getValue("rule");
    final String category = attributes.getValue("ruleset");
    reportInspectionType(id, id, category, category);
  }
}