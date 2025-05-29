(defproject chengfen "0.1.0"
  :description "Toy tool that visualizes language distribution in git repository, an execuse to try out lisp"
  :url "https://github.com/xiaoshihou514/chengfen"
  :license {:name "GPL-3.0"
            :url "https://www.gnu.org/licenses/gpl-3.0.html"}
  :dependencies [[org.clojure/clojure "1.11.1"]]
  :main ^:skip-aot chengfen.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
