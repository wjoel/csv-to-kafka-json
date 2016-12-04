(ns csv-to-kafka-json.core
  (:require [clojure.java.io :as io]
            [kinsky.client :as kc]
            [cheshire.core :as json]
            [clojure-csv.core :as csv]
            [clojure.tools.cli :refer [parse-opts]]
            [csv-to-kafka-json.arguments :as arguments])
  (:gen-class))

(defn csv-line-to-json-string [line field-names]
  (->> (map vector field-names line)
       (reduce (fn [m [k v]] (assoc m k v)) {})
       json/generate-string))

(defn send-csv-line-to-kafka! [kafka-producer kafka-topic csv-line field-names]
  (.send kafka-producer
         (kc/->record {:topic kafka-topic
                       :value (csv-line-to-json-string csv-line field-names)})))

(defn send-csv-to-kafka! [kafka-producer kafka-topic csv-contents]
  (let [[field-names & csv-lines] (csv/parse-csv csv-contents)]
    (doseq [csv-line csv-lines]
      (send-csv-line-to-kafka! kafka-producer kafka-topic csv-line field-names))))

(defn -main [& args]
  (let [{:keys [options errors summary]} (parse-opts args arguments/cli-options)]
    (cond
      (:help options) (arguments/print-usage summary errors)
      errors (throw (IllegalArgumentException. (arguments/print-usage summary errors)))
      :default (let [{:keys [bootstrap-servers topic filename]} options
                     kafka-producer (kc/producer {:bootstrap.servers bootstrap-servers}
                                                 (kc/string-serializer)
                                                 (kc/string-serializer))]
                 (send-csv-to-kafka! kafka-producer topic (io/reader filename))))))
