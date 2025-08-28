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

import com.pravles.processengine.api.ActivityFunction;
import com.pravles.processengine.api.ConditionFunction;
import com.pravles.processengine.util.AbstractLaunchInfoFactory;
import com.pravles.processengine.util.GenerateUuid;
import com.pravles.processengine.util.PpmnDiagramInfo;
import com.pravles.util.ClojureActivityFunction;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.pravles.processengine.util.PpmnDiagramInfo.ROOT;
import static java.util.Arrays.asList;

@RequiredArgsConstructor
public class ProdLaunchInfoFactory extends AbstractLaunchInfoFactory {
    private final String dir;

    @Override
    protected void initFnBindings(final Map<String, ActivityFunction> fnBindings) {
        Arrays.asList("init-loop",
                "det-subdir-names",
                "inc-counter",
                "read-rdf-data",
                "add-table-creation-ddl",
                "append-ddl-to-sql")
            .stream()
            .forEach(f -> {
                fnBindings.put(f,
                        new ClojureActivityFunction(f));
            });
        fnBindings.put("gen-uuid", new GenerateUuid());
    }

    @Override
    protected void initCnBindings(final Map<String, ConditionFunction> cnBindings) {
        cnBindings.put("all-files-processed", new AllFilesProcessed());
    }

    @Override
    protected Map<String, Object> composeInitialContext() {
        final HashMap<String, Object> ctx = new HashMap<>();
        ctx.put("dir", dir);
        ctx.put("all-files-processed", true);
        return ctx;
    }

    @Override
    protected List<PpmnDiagramInfo> composeDiagramInfos() {
        return asList(PpmnDiagramInfo
                        .builder()
                        .diagramId(ROOT)
                        .inputStream(istream("/main.ppmn.fodg"))
                        .build(),
                PpmnDiagramInfo.builder()
                        .diagramId("process-file")
                        .inputStream(istream("/process-file.ppmn.fodg"))
                        .build());
    }
}
