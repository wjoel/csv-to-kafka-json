(ns csv-to-kafka-json.arguments
  (:require [clojure.tools.cli :refer [parse-opts]]
            [clojure.java.io :as io]
            [clojure.string :as str]))

(def max-topic-name-length 249)

(defn valid-topic-name? [string]
  (and (< (count string) max-topic-name-length)
       (> (count string) 0)
       (not (= string "."))
       (not (= string ".."))
       (re-seq #"[a-zA-Z0-9\\._\\-]+" string)))

(defn safe-parse-int [int-string]
  (try
    (Integer/parseInt int-string)
    (catch NumberFormatException e
      false)))

(defn valid-bootstrap-server? [server-and-port-str]
  (when server-and-port-str
    (let [server-and-port (str/split server-and-port-str #":")]
      (and (= 2 (count server-and-port))
           (safe-parse-int (second server-and-port))
           (> (safe-parse-int (second server-and-port)) 0)))))

(defn valid-bootstrap-servers? [connect-string]
  (when (and connect-string
             (not (= (last connect-string) \,)))
   (let [bootstrap-servers (str/split connect-string #",")]
     (every? valid-bootstrap-server? bootstrap-servers))))

(def cli-options
  [["-t" "--topic" "Topic name"
    :missing "Please provide a destination Kafka topic name"
    :validate valid-topic-name?]
   ["-f" "--filename" "CSV file to send to Kafka"
    :validate #(-> % io/as-file .exists)]
   ["-b" "--bootstrap-servers" "Bootstrap servers to find Kafka cluster"
    :default "localhost:9092"
    :validate valid-bootstrap-servers?]])
