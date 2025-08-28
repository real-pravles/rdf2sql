;
; Copyright 2025 Pravles Redneckoff
;
; Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
;
; The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
;
; THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
;

(ns append-ddl-to-sql)

(require '[clojure.string :as str]
         '[clojure.java.io :as io]
         '[clojure.pprint :as pprint])
(import 'org.apache.commons.lang3.StringUtils)
(import 'us.bpsm.edn.Keyword)

(def nl (System/getProperty "line.separator"))


(defn гав
  [old-ctx]
  (let [
        cur-idx (get old-ctx "cur-subdir-idx")
        subdirs (get old-ctx "subdirs")
        subdir-name (nth subdirs cur-idx)
        data-from-rdf (get old-ctx "data-from-rdf")
        lang-code (.getLanguageCode data-from-rdf)
        is-fiction? (.getFiction data-from-rdf)
        is-poetry? (.getPoetry data-from-rdf)
        is-success? (.getSuccess data-from-rdf)
        creator (-> data-from-rdf
                    (.getCreator))
        title (-> data-from-rdf
                    (.getTitle))
        rdf-url (-> data-from-rdf
                    (.getRdfUrl))
        text-url (-> data-from-rdf
                     (.getTextUrl))
        uuid (get old-ctx "cur-uuid")
        insert-row-ddl (str ""
                            "INSERT INTO books (" nl
                            "    id," nl
                            "    isFromProjectGutenberg," nl
                            "    langCode," nl
                            "    isFiction," nl
                            "    isPoetry," nl
                            "    projectGutenbergId," nl
                            "    title," nl
                            "    creators," nl
                            "    productUrl," nl
                            "    textUrl" nl
                            ") VALUES (" nl
                            "    '" uuid "'," nl
                            "    1," nl
                            "    '" lang-code "'," nl
                            "    " is-fiction? "," nl
                            "    " is-poetry? "," nl
                            "    '" subdir-name "'," nl
                            "    '" title "'," nl
                            "    '" creator "'," nl
                            "    'https://www.gutenberg.org/ebooks/" subdir-name "'," nl
                            "    '" text-url "'" nl
                            ");"
                            nl)
        sql-file-path (get old-ctx "sql-file-path")
        ]
    (spit sql-file-path insert-row-ddl :append true)
    old-ctx))