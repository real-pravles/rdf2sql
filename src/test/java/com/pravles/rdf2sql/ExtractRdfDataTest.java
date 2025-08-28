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

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Stream;

import static com.pravles.rdf2sql.DataFromRdf.*;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.of;

class ExtractRdfDataTest {

    static Stream<Arguments> scenarios() {
        return Stream.of(
                of("125",
                        builder()
                                .creator("Stratton-Porter, Gene")
                                .fiction(true)
                                .poetry(false)
                                .languageCode("en")
                                .mediumCoverUrl("https://www.gutenberg.org/cache/epub/125/pg125.cover.medium.jpg")
                                .smallCoverUrl("https://www.gutenberg.org/cache/epub/125/pg125.cover.small.jpg")
                                .subjects(asList("Didactic fiction",
                                        "Indiana -- Fiction", "Domestic fiction", "Bildungsromans", "Mothers and daughters -- Fiction", "Girls -- Fiction", "Conflict of generations -- Fiction", "Swamps -- Fiction", "Moths -- Fiction", "PZ", "PS"))
                                .success(true)
                                .textUrl("https://www.gutenberg.org/ebooks/125.txt.utf-8")
                                .title("A Girl of the Limberlost")
                                .rdfUrl("https://www.gutenberg.org/ebooks/125.rdf")
                                .build()),
                of("1199",
                        DataFromRdf.builder()
                                .languageCode("en")
                                .creator("")
                                .title("An Anthology of Australian Verse")
                                .subjects(asList("Australian poetry", "PR"))
                                .fiction(false)
                                .textUrl("https://www.gutenberg.org/ebooks/1199.txt.utf-8")
                                .smallCoverUrl("https://www.gutenberg.org/cache/epub/1199/pg1199.cover.small.jpg")
                                .mediumCoverUrl("https://www.gutenberg.org/cache/epub/1199/pg1199.cover.medium.jpg")
                                .rdfUrl("https://www.gutenberg.org/ebooks/1199.rdf")
                                .poetry(true)
                                .success(true)
                                .build()),
                of("12988",
                        DataFromRdf.builder()
                                .poetry(false)
                                .fiction(true)
                                .creator("Thomas, Augustus")
                                .title("Representative Plays by American Dramatists: 1856-1911: In Mizzoura")
                                .languageCode("en")
                                .subjects(Arrays.asList("American drama", "PS"))
                                .rdfUrl("https://www.gutenberg.org/ebooks/12988.rdf")
                                .fiction(false)
                                .poetry(false)
                                .success(true)
                                .textUrl("https://www.gutenberg.org/ebooks/12988.txt.utf-8")
                                .rdfUrl("https://www.gutenberg.org/ebooks/12988.rdf")
                                .smallCoverUrl("")
                                .mediumCoverUrl("")
                                .build()),
                Arguments.of("foo.bar",
                        DataFromRdf.builder()
                                .success(false)
                                .build()),
                of("13682",
                        DataFromRdf.builder()
                                .languageCode("fi")
                                .creator("Järnefelt, Arvid")
                                .title("Isänmaa")
                                .subjects(Arrays.asList("Finland -- Fiction", "PH"))
                                .fiction(true)
                                .poetry(false)
                                .success(true)
                                .textUrl("https://www.gutenberg.org/ebooks/13682.txt.utf-8")
                                .rdfUrl("https://www.gutenberg.org/ebooks/13682.rdf")
                                .mediumCoverUrl("")
                                .smallCoverUrl("")
                                .build()),
                of("14982",
                        DataFromRdf.builder()
                                .success(true)
                                .creator("Mabini, Apolinario")
                                .title("Panukala sa Pagkakana nang Repúblika nang Pilipinas")
                                .languageCode("tl")
                                .textUrl("https://www.gutenberg.org/ebooks/14982.txt.utf-8")
                                .rdfUrl("https://www.gutenberg.org/ebooks/14982.rdf")
                                .subjects(asList("Philippines -- History -- Philippine American War, 1899-1902", "DS"))
                                .fiction(false)
                                .poetry(false)
                                .mediumCoverUrl("https://www.gutenberg.org/cache/epub/14982/pg14982.cover.medium.jpg")
                                .smallCoverUrl("https://www.gutenberg.org/cache/epub/14982/pg14982.cover.small.jpg")
                                .build()),
                of("16978",
                        DataFromRdf.builder()
                                .success(true)
                                .creator("Slattery, John T. (John Theodore)")
                                .title("Dante: \"The Central Man of All the " +
                                        "World\"" + "\n" + "A Course of Lectures " +
                                        "Delivered Before the Student Body of the New York State College for Teachers, Albany, 1919, 1920")
                                .languageCode("en")
                                .subjects(Arrays.asList("Dante Alighieri, 1265-1321. Divina commedia", "Dante Alighieri, 1265-1321", "PQ"))
                                .fiction(false)
                                .poetry(false)
                                .textUrl("https://www.gutenberg.org/ebooks/16978.txt.utf-8")
                                .smallCoverUrl("https://www.gutenberg.org/cache/epub/16978/pg16978.cover.small.jpg")
                                .mediumCoverUrl("https://www.gutenberg.org/cache/epub/16978/pg16978.cover.medium.jpg")
                                .rdfUrl("https://www.gutenberg.org/ebooks/16978.rdf")
                                .build())
        );
    }


    @ParameterizedTest
    @MethodSource("scenarios")
    public void givenRdfFile_whenApply_thenReturnCorrectResult(
            final String rdfNr, final DataFromRdf expectedResult) throws IOException {
        // Given
        final String fullPath = String.format("src/test/resources/scenarios/01/input/%s/pg%s.rdf", rdfNr, rdfNr);
        final File rdfFile = new File(fullPath);

        // When
        final DataFromRdf actualResult = new ExtractRdfData().apply(rdfFile);

        // Then
        assertEquals(expectedResult, actualResult);
    }
}