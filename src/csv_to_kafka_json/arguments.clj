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
       (boolean (re-seq #"^[a-zA-Z0-9\\._\\-]+$" string))))

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
  [["-t" "--topic TOPIC" "Topic name"
    :missing "Destination Kafka topic name is required"
    :validate [valid-topic-name? "Destination Kafka topic name is invalid"]]
   ["-f" "--filename FILENAME" "CSV file to send to Kafka"
    :missing "CSV filename is required"
    :validate [#(-> % io/as-file .exists) "File must exist"]]
   ["-b" "--bootstrap-servers SERVERS" "Bootstrap servers to find Kafka cluster"
    :default "localhost:9092"
    :validate [valid-bootstrap-servers? "Server list is invalid"]]
   ["-h" "--help"]])

(defn print-usage [options-summary errors]
  (println (str/join "\n"
                     ["Send a CSV file to Kafka as JSON messages."
                      ""
                      "Usage: java -jar csv-to-kafka-json.jar [arguments]"
                      "Options:"
                      options-summary
                      (when errors
                        (str/join "\n" (cons "Errors:" errors)))
                      ""]))
  (when errors
    (str/join ", " errors)))
