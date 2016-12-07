(defproject csv-to-kafka-json "0.1.0"
  :description "Sends CSV files to Kafka as JSON encoded messages"
  :url "http://github.com/wjoel/csv-to-kafka-json"
  :license {:name "MIT License"
            :url "http://www.opensource.org/licenses/mit-license.php"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [clojure-csv/clojure-csv "2.0.1"]
                 [cheshire "5.6.3"]
                 [org.clojure/tools.cli "0.3.5"]
                 [org.apache.kafka/kafka-clients "0.10.0.1"]]
  :main ^:skip-aot csv-to-kafka-json.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
