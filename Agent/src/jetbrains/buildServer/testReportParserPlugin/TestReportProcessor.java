/*
 * Copyright 2000-2007 JetBrains s.r.o.
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

package jetbrains.buildServer.testReportParserPlugin;

import static jetbrains.buildServer.testReportParserPlugin.TestReportParserPlugin.createBuildLogMessage;
import jetbrains.buildServer.testReportParserPlugin.antJUnit.AntJUnitReportParser;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class TestReportProcessor implements Runnable {
    private static final long FILE_WAIT_TIMEOUT = 500;

    private final TestReportParserPlugin myPlugin;

    private final LinkedBlockingQueue<File> myReportQueue;
    private final TestReportDirectoryWatcher myWatcher;
    private final AntJUnitReportParser myParser;

    private ReportData myCurrentReport;

    private volatile boolean myFinished;


    private static final class ReportData {
        private final File myFile;
        private long myProcessedTests;

        public ReportData(@NotNull final File file) {
            myFile = file;
            myProcessedTests = 0;
        }

        public File getFile() {
            return myFile;
        }

        public long getProcessedTests() {
            return myProcessedTests;
        }

        public void setProcessedTests(long tests) {
            myProcessedTests = tests;
        }
    }

    public TestReportProcessor(@NotNull final TestReportParserPlugin plugin,
                               @NotNull final LinkedBlockingQueue<File> queue,
                               @NotNull final TestReportDirectoryWatcher watcher) {
        myPlugin = plugin;
        myReportQueue = queue;
        myWatcher = watcher;
        myParser = new AntJUnitReportParser(myPlugin.getLogger());
    }

    public void run() {
        myFinished = false;
        myCurrentReport = null;

        while (!myPlugin.isStopped()) {
            processReport(takeNextReport(FILE_WAIT_TIMEOUT));
        }
        synchronized (myWatcher) {
            while (!myWatcher.isStopped()) {
                try {
                    myWatcher.wait();
                } catch (InterruptedException e) {
                    myPlugin.getLogger().warning(createBuildLogMessage("report processor thread interrupted"));
                }
            }
        }
        while (!myReportQueue.isEmpty()) {
            processReport(takeNextReport(1));
        }
        synchronized (this) {
            myFinished = true;
            this.notify();
        }
    }

    private void processReport(ReportData report) {
        if (report != null) {
            long processedTests = myParser.parse(report.getFile(), report.getProcessedTests());
            if (processedTests != -1) {
                myCurrentReport.setProcessedTests(processedTests);
            } else {
                myPlugin.getLogger().message(createBuildLogMessage(report.getFile().getPath() + " report processed."));
                myCurrentReport = null;
            }
        }
    }

    private ReportData takeNextReport(long timeout) {
        if (myCurrentReport != null) {
            return myCurrentReport;
        }
        try {
            File file = myReportQueue.poll(timeout, TimeUnit.MILLISECONDS);
            if (file != null) {
                myCurrentReport = new ReportData(file);
                return myCurrentReport;
            }
        } catch (InterruptedException e) {
            myPlugin.getLogger().warning(createBuildLogMessage("report processor thread interrupted"));
        }
        return null;
    }

    public boolean isProcessingFinished() {
        return myFinished;
    }
}