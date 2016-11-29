(ns csv-to-kafka-json.core
  (:require [clojure.java.io :as io]
            [kinsky.client :as kc]
            [cheshire.core :as json]
            [clojure.string :as s]
            [clojure-csv.core :as csv])
  (:gen-class))

(def kafka-topic "wjoel")

(defn csv-line-to-json-string [line field-names]
  (->> (map vector field-names line)
       (reduce (fn [m [k v]] (assoc m k v)) {})
       json/generate-string))

(defn send-csv-line-to-kafka! [kafka-producer csv-line field-names]
  (.send kafka-producer
         (kc/->record {:topic kafka-topic
                       :value (csv-line-to-json-string csv-line field-names)})))

(defn send-csv-to-kafka! [kafka-producer csv-contents]
  (let [[field-names & csv-lines] (csv/parse-csv csv-contents)]
    (doseq [csv-line csv-lines]
      (send-csv-line-to-kafka! kafka-producer csv-line field-names))))

(defn -main [filename]
  (let [kafka-producer (kc/producer {:bootstrap.servers "localhost:9092"}
                                    (kc/string-serializer)
                                    (kc/string-serializer))]
    (send-csv-to-kafka! kafka-producer (io/reader filename))))
