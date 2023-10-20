(ns commonutils.csvlib
  (:require
   [goog.string :as gstring]
   [clojure.string :as s]
   ["fs" :as fs]
   [testdouble.cljs.csv :as csv])) (defn write-file [filename data]
                                     (-> fs
                                         (.writeFile  filename (str data) (fn [err] (when err (print "error" err))))))

(defn spit-ns [filename sdata]
  (-> fs
      (.writeFileSync  filename sdata)))

(defn aspit-ns [filename sdata]
  (-> fs
      (.appendFileSync  filename sdata)))

(defn process-csv [data1]
  (let
   [;\r get rid of them
     ; and trim
    data (->> data1
              (map (fn [x] (map #(if (= % "\r") "" %) x)))
              (map (fn [x] (map #(s/trim %)  x))))
    [collist & rs] data
      ;column headers that are blank get default names
      ;
    cl
    (->> collist
         (map #(s/replace % #" " "-"))
         (map-indexed vector)
         (map (fn [[idx x]] (if (= x "") (gstring/format "col%d" idx) x)))
         (map s/lower-case))]
    (->> rs
         (filter #(> (count %) 0))
         (map (partial zipmap (map keyword cl))))))

(defn read-csv-file [filename]
  (process-csv (csv/read-csv
                (-> fs
                    (.readFileSync filename "utf-8")
                    (.toString)))))

(defn process-csv-noheader [data1 collist]
  (let
   [;\r get rid of them
     ; and trim
    data (->> data1
              (map (fn [x] (map #(if (= % "\r") "" %) x)))
              (map (fn [x] (map #(s/trim %)  x))))
    rs data
      ;column headers that are blank get default names
      ;
    cl
    (->> collist
         (map #(s/replace % #" " "-"))
         (map-indexed vector)
         (map (fn [[idx x]] (if (= x "") (gstring/format "col%d" idx) x)))
         (map s/lower-case))]
    (->> rs
         (filter #(> (count %) 0))
         (map (partial zipmap (map keyword cl))))))

(defn read-csv-file-noheader [filename collist]
  (process-csv-noheader (csv/read-csv
                         (-> fs
                             (.readFileSync filename "utf-8")
                             (.toString))) collist))

(defn scsv [specdata s]
  (let
   [fcols #(if (map? %) (:h %) (->> (str %)
                                    (drop 1)
                                    (apply str)))
    ffns #(if (map? %) (:f %) %)
    result [(mapv fcols specdata)]]
    (->> s
         (map (apply juxt (map ffns specdata)))
         (into result))))

(defn wcsv [filename data]
  (write-file filename (csv/write-csv data :quote? true)))

(comment
;client code
  ;simple scsv
  ;it removes \rs
  ;then it trims all data first. it doesnt touch commas
  ;then it makes keywords by hyphenating spaces and lowe casing
  (->>
   (read-csv-file "/Users/sridharparasuraman/node-csv/src/main/node_csv/s1.csv")
   (scsv [:symbol-only-crypto :price]))

  ;writing to a file
  ;we rename a column and further embellish it through map element in the spec
  (->>
   (read-csv-file "/Users/sridharparasuraman/node-csv/src/main/node_csv/s1.csv")
   (scsv [:symbol-only-crypto {:h "renamed-price" :f (comp (partial gstring/format "C %s")  :price)}])
   (wcsv "/Users/sridharparasuraman/node-csv/src/main/node_csv/s2.csv")))
