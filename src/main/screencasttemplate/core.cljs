(ns screencasttemplate.core
  (:require
   ["node-fetch" :default nf :as fetch]
   [clojure.set]
   [promesa.core :as p]
   [promesa.exec :as exec]
   [goog.string :as gstring]
   [commonutils.csvlib :refer
    [write-file
     spit-ns
     aspit-ns
     process-csv
     ;if csv files client has to supply header
     read-csv-file-noheader
     read-csv-file
     scsv
     wcsv]]
   [goog.string.format]
   [clojure.string :as s]
   [cljs-time.core :as t]
   [cljs-time.format :as f]
   [clojure.pprint :refer [pprint]]
   ["fs" :as fs]
   [cljs.reader :as reader]
   ))

(def HOME-DIR "/Users/sridharparasuraman/")
(def LOG-DIR (gstring/format "%s/logs" HOME-DIR))

(defn logfile [line]
  (let
   [filename (gstring/format  "%s/log-data.txt" LOG-DIR)
    ts (f/unparse (f/formatters :date-time) (t/time-now))
    line-to-spit (gstring/format "\n[%s]%s" ts   line)]
    (aspit-ns filename line-to-spit)))

;our export

(defn handler [env]
  (p/let [env-string (-> js/JSON (.stringify env))
          clj-env (js->clj (-> js/JSON (.parse env-string)) :keywordize-keys true)
          arr ((fn [x] [1 2 3]) clj-env)]
    (print arr)
    "1730"))

(comment
  
  (+ 2 3)
  
  )

