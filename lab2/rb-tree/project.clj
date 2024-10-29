(defproject rb-tree "0.1.0-SNAPSHOT"
  :description "Red-Black Tree - bag (multiset)"
  :url "http://example.com/rb-tree"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [org.clojure/test.check "1.1.0"]]
  :main ^:skip-aot rb-tree.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}}
  :java-source-paths ["src/java"])
