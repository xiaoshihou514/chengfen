(ns chengfen.core (:gen-class))

(import '[java.nio.file Files Paths LinkOption])
(import 'java.net.URI)
(require '[clojure.java.shell :refer [sh]])
(require '[clojure.string :refer [split]])
(require '[clojure.core.async :refer [chan >! go <!!]])
(require '[clojure.java.io :refer [reader]])
(require '[chengfen.ft :refer [fts ft-color]])

(def slash java.io.File/separator)
(defn uri [path]
  (URI. (str "file://" path)))
(defn pwd [] (.getAbsolutePath (java.io.File. ".")))
(def total-cells 50)

(defn find-root []
  (letfn [(find-root' [path]
            (let [linkopt (into-array LinkOption [])
                  git (Paths/get (uri (str path slash ".git")))]
              (if (and (Files/exists git linkopt) (Files/isDirectory git linkopt))
                path
                (recur (find-root' (.toString (.getParent (Paths/get (uri path)))))))))]
    (find-root' (pwd))))

(defn count-lines [path]
  (with-open [rdr (reader path)]
    (count (line-seq rdr))))

(defn ft? [fname]
  (when-some [ext (last (split fname #"\."))]
    (fts ext)))

(defn kv-concat [xs]
  (persistent!  ; toMap
   (reduce
    (fn [acc [k? v]]
      (or (when-some [k k?]
            (assoc! acc k (+ (get acc k 0) v)))  ; mutable update
          acc))
    (transient {})  ; mutable.Map
    xs)))

(defn pretty [x]
  (cond
    (> x  1000000) (format "%.2fm" (/ (float x) 1000000.0))
    (> x 1000) (format "%.2fk" (/ (float x) 1000.0))
    :else x))

(defn print-single [sum [ft x]]
  (let [iperc (* 100 (/ (float x) (float sum)))]
    (print ft)
    (print ":" (pretty x) "lines" (format "%.2f" iperc))
    (println "%")))

(defn rgb-text
  [text [r g b]]
  (format "\u001b[38;2;%d;%d;%dm%s\u001b[0m" r g b text))

(defn print-perc [sum [ft x]]
  (let [ncells (int (* (/ (float x) (float sum)) total-cells))]
    (loop [x 0]
      (when (< x ncells)
        (print (rgb-text "█" (ft-color ft)))
        (recur (+ x 1))))))

(defn -main []
  (let [files (mapv #(str (pwd) slash %)
                    (split (str
                            (:out (sh "git" "ls-files" :dir (find-root)))
                            (:out (sh "git" "ls-files" "--others" "--exclude-standard" :dir (find-root))))
                           #"\n"))
        chans (repeatedly (count files) chan)]
    (shutdown-agents)
    ;; launch a counting thread for each file
    (mapv #(go (>! %2 [(ft? %1) (count-lines %1)])) files chans)
    ;; sort in reverse
    (let [stats (sort-by (comp - second) (kv-concat (map <!! chans)))
          total (reduce #(+ %1 (second %2)) 0 stats)]
      (mapv (partial print-perc total) stats)
      (println \newline)
      (mapv (partial print-single total) stats))))
