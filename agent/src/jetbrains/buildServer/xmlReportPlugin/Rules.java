/*
 * Copyright 2000-2019 JetBrains s.r.o.
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
import java.util.Collection;
import org.jetbrains.annotations.NotNull;

/**
 * User: vbedrosova
 * Date: 16.12.10
 * Time: 13:19
 */
public interface Rules {
  @NotNull Collection<String> getBody();
  @NotNull Collection<File> getPaths();
  @NotNull Collection<File> collectFiles();
}
