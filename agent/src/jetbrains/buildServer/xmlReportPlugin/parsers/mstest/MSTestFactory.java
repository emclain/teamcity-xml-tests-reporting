package jetbrains.buildServer.xmlReportPlugin.parsers.mstest;

import jetbrains.buildServer.xmlReportPlugin.ParserFactory;
import org.jetbrains.annotations.NotNull;

/**
 * User: vbedrosova
 * Date: 09.02.11
 * Time: 13:36
 */
public class MSTestFactory extends TRXFactory implements ParserFactory {

  @NotNull
  @Override
  protected final String getDefaultSuiteName() {
    return "MSTest";
  }
}
