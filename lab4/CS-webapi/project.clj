(defproject cs-webapi "0.1.0-SNAPSHOT"
  :description "CS web API"
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [metosin/compojure-api "2.0.0-alpha28"]
                 [ring/ring-core "1.9.6"]
                 [ring/ring-defaults "0.3.3"]
                 [markdown-clj "1.11.4"]
                 [mvxcvi/clj-pgp "0.9.0"]
                 [org.clojure/tools.logging "1.1.0"]
                 [org.clojure/math.num-tower "0.0.4"]
                 [com.tiemens/secretshare "1.4.4"]
                 [jstrutz/hashids "1.0.1"]
                 [me.lemire.integercompression/JavaFastPFOR "0.1.12"]]
  :ring {:handler cs-webapi.handler/app
         :open-browser? false}
  :uberjar-name "server.jar"
  :profiles {:dev {:dependencies [[javax.servlet/javax.servlet-api "4.0.1"]
                                  [cheshire "5.11.0"]
                                  [ring/ring-mock "0.4.0"]
                                  [midje "1.10.3"]]
                   :plugins [[lein-ring "0.12.5"]
                             [lein-midje "3.2.1"]]}})
