(ns chengfen.core
  (:gen-class))

(import '[java.nio.file Files Paths LinkOption])
(import 'java.net.URI)

(defn uri [path]
  (URI. (str "file://" path)))
(def linkopt (into-array LinkOption []))
(def slash java.io.File/separator)

(defn find-root []
  (letfn [(find-root' [path]
            (let [git (Paths/get (uri (str path slash ".git")))]
              (if (and (Files/exists git linkopt) (Files/isDirectory git linkopt))
                path
                (recur (find-root' (.toString (.getParent (Paths/get (uri path)))))))))]
    (find-root' (System/getProperty "user.dir"))))

(defn -main
  []
  (println (find-root)))
