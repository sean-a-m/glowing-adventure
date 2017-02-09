(defproject emp-rest "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [compojure "1.5.1"]
                 [ring/ring-defaults "0.2.1"]
                 [ring-server "0.4.0"]
                 [ring/ring-json "0.4.0"]
                 [danlentz/clj-uuid "0.1.6"]
                 [clj-time "0.13.0"]
                 [org.clojure/data.json "0.2.6"]
                 [org.clojure/algo.generic "0.1.2"]
                 [org.jsoup/jsoup "1.10.2"]
                 [prismatic/schema "1.1.3"]]
  :plugins [[lein-ring "0.9.7"]]
  :aot [emp-rest.run]
  :main emp-rest.run
  :ring {:handler emp-rest.handler/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.0"]]}})
