package jetbrains.buildServer.xmlReportPlugin;

import junit.framework.TestCase;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

import static jetbrains.buildServer.xmlReportPlugin.TestUtil.getAbsoluteTestDataPath;
import static jetbrains.buildServer.xmlReportPlugin.TestUtil.readFile;

/**
 * User: vbedrosova
 * Date: 07.09.2010
 * Time: 15:21:04
 */
public abstract class ParserTestCase extends TestCase {
  protected void runTest(final String fileName) throws Exception {
    final String reportName = getAbsoluteTestDataPath(fileName, getType());
    final String resultsFile = reportName + ".tmp";
    final String expectedFile = reportName + ".gold";
    final String baseDir = getAbsoluteTestDataPath(null, getType());

    new File(resultsFile).delete();

    final StringBuilder results = new StringBuilder();

    final XmlReportParser parser = getParser(results);

    final File report = new File(reportName);

    final Map<String, String> params = new HashMap<String, String>();
    prepareParams(params);

    parser.parse(new ReportData(report, getType()));
    parser.logReportTotals(report, true);
    parser.logParsingTotals(params, true);

    final File expected = new File(expectedFile);
    final String actual = results.toString().replace(baseDir, "##BASE_DIR##").replace("/", File.separator).replace("\\", File.separator).trim();
    if (!readFile(expected, true).equals(actual)) {
      final FileWriter resultsWriter = new FileWriter(resultsFile);
      resultsWriter.write(actual);
      resultsWriter.close();

      assertEquals(readFile(expected, true), actual);
    }
  }

  protected abstract String getType();
  protected abstract XmlReportParser getParser(StringBuilder result);
  protected abstract void prepareParams(Map<String, String> paramsMap);
}