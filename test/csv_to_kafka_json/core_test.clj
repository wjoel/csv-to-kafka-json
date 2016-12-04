(ns csv-to-kafka-json.core-test
  (:require [clojure.test :refer :all]
            [csv-to-kafka-json.core :refer :all]
            [cheshire.core :as json])
  (:import [org.apache.kafka.clients.producer MockProducer]
           [org.apache.kafka.common.serialization StringSerializer]))

(def test-csv
  (str "this,is,a,test\n"
       "1,2,3,4\n"
       "there,are,no,types\n"
       "to,be,found,here"))

(def test-jsons
  (map json/generate-string
       [{:this "1" :is "2" :a "3" :test "4"}
        {:this "there" :is "are" :a "no" :test "types"}
        {:this "to" :is "be" :a "found" :test "here"}]))

(deftest sending-csv-file-to-kafka
  (let [mock-producer (MockProducer. true (StringSerializer.) (StringSerializer.))]
    (is (= test-jsons
           (do
             (send-csv-to-kafka! mock-producer "test" test-csv)
             (->> (.history mock-producer)
                  (map #(.value %))))))))
