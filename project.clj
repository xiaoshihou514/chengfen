(defproject chengfen "0.1.0"
  :plugins [[io.taylorwood/lein-native-image "0.3.1"]]
  :description "Toy tool that visualizes language distribution in git repository, an execuse to try out lisp"
  :url "https://github.com/xiaoshihou514/chengfen"
  :license {:name "GPL-3.0"
            :url "https://www.gnu.org/licenses/gpl-3.0.html"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [com.github.clj-easy/graal-build-time "1.0.5"]]
  :main ^:skip-aot chengfen.core
  :target-path "target/%s"
  :native-image {:name "chengfen" :opts ["--features=clj_easy.graal_build_time.InitClojureClasses"]}
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
