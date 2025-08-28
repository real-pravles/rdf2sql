;
; Copyright 2025 Pravles Redneckoff
;
; Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
;
; The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
;
; THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
;

(ns read-rdf-data)

(require '[clojure.string :as str]
         '[clojure.java.io :as io]
         '[clojure.pprint :as pprint])


(import 'com.pravles.rdf2sql.ExtractRdfData)
(def nl (System/getProperty "line.separator"))


(defn гав
  [old-ctx]
  (let [cur-idx (get old-ctx "cur-subdir-idx")
        subdirs (get old-ctx "subdirs")
        subdir-name (nth subdirs cur-idx)
        basedir (get old-ctx "dir")
        path (str
               basedir
               "/"
               subdir-name
               "/"
               "pg" subdir-name ".rdf")
        rdf-file (-> path
                     (java.io.File.))
        extractor (ExtractRdfData.)
        data-from-rdf (.apply extractor rdf-file)
        include-book? (-> data-from-rdf
                          (.getSuccess))
        ]
    (.put old-ctx "data-from-rdf" data-from-rdf)
    (.put old-ctx "include-book?" include-book?)
    old-ctx))