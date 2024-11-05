(defproject interpolation "0.1.0-SNAPSHOT"
  :description "Interpolation"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [org.clojure/tools.cli "1.0.206"]]
  :main ^:skip-aot interpolation.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})