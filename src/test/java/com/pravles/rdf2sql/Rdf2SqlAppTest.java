/*
 * Copyright 2025 Pravles Redneckoff
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.pravles.rdf2sql;

import com.pravles.processengine.util.LaunchInfoFactory;
import com.pravles.processengine.util.ProcessEngineLauncher;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Stream;

import static com.pravles.TestUtils.assertFilesEqual;
import static java.lang.String.format;
import static org.apache.commons.io.FileUtils.cleanDirectory;
import static org.apache.commons.io.FileUtils.copyDirectory;
import static org.junit.jupiter.api.Assertions.assertEquals;

class Rdf2SqlAppTest {
    static Stream<Arguments> scenarios() {
        return Stream.of(
                Arguments.of("01"),
                Arguments.of("02"),
                Arguments.of("03")
        );
    }

    @ParameterizedTest
    @MethodSource("scenarios")
    public void givenCall_whenRun_thenProduceCorrectResult(
            final String scenario) throws IOException {
        // Given
        final String baseDir = String.format("src/test/resources/scenarios/%s", scenario);
        final String inputDir = format("%s/input", baseDir);
        final String actualOutputDir = format("%s/actual-result", baseDir);
        final String expectedOutputDir = format( "%s/expected-result",
                baseDir);

        new File(actualOutputDir).mkdir();
        cleanDirectory(new File(actualOutputDir));
        copyDirectory(new File(inputDir),
                new File(actualOutputDir));

        final LaunchInfoFactory lif = new TestLaunchInfoFactory(actualOutputDir);

        // When
        final Map<String, Object> actualCtx =
                new ProcessEngineLauncher().run(lif);

        // Then
        final Collection<File> expectedFiles =
                FileUtils.listFiles(new File(expectedOutputDir), null,
                        true);
        final Collection<File> actualFiles =
                FileUtils.listFiles(new File(actualOutputDir), null,
                        true);

        assertEquals(expectedFiles.size(), actualFiles.size());

        expectedFiles
                .stream()
                .forEach(expectedFile -> {
                    final String expectedFilePath = expectedFile.getPath();
                    final String actualFilePath =
                            expectedFilePath.replace(expectedOutputDir,
                                    actualOutputDir);
                    try {
                        assertFilesEqual(new File(expectedFilePath),
                                new File(actualFilePath));
                    } catch (IOException e) {
                        e.printStackTrace();
                        Assertions.fail(e.getMessage());
                    }
                });

    }
}