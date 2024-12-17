(ns cs.core
  (:require [cs.marshalling :as ms]
            [cs.random :as r]
            [hashids.core :as h]
            [clojure.tools.logging :as log]))

(def settings
  {:total (Integer. 5)
   :quorum (Integer. 3)
   :max 1024
   :prime 'prime4096
   :description "Crypto splitter"
   :protocol "CS"
   :type "WEB"
   :alphabet "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
   :salt "FT9NRt3KvGZO3eMpKECOVCzjzZjFOD1BcvUqajeHM3eS9qsN"
   :length 6
   :entropy 3.1})

(defn encode
  [conf pass]
  {:pre [(string? pass)
         (map? conf)]
   :post [(coll? %)
          (string? (first %))
          (= (count %) (:total conf))]}
  (log/info "Encoding with config:" conf "and pass:" pass)
  (let [sequence (ms/string2sequenceuence pass)
        secrets (ms/sequence2secrets conf sequence)
        slices (ms/secrets2slices conf secrets)]
    (log/info "Intermediate sequence:" sequence)
    (log/info "Intermediate secrets:" secrets)
    (log/info "Intermediate slices:" slices)
    (map #(ms/encode-hash conf %) slices)))

(defn decode
  [conf slices]
  {:pre [(coll? slices)
         (map? conf)
         (<= (count slices) (:total conf))]
   :post [(string? %)]}
  (log/info "Decoding with config:" conf "and slices:" slices)
  (let [decoded-slices (map #(ms/decode-hash conf %) slices)
        secrets (ms/slices2secrets conf decoded-slices)
        sequence (ms/secrets2sequence conf secrets)]
    (log/info "Decoded slices:" decoded-slices)
    (log/info "Intermediate secrets:" secrets)
    (log/info "Intermediate sequence:" sequence)
    (ms/sequence2string sequence)))

(defn generate
  ([size] (generate {} size))
  ([conf size]
   (->> (loop [x (/ size 2)
               res [(r/digit 9)]]
          (if (> x 1)
            (recur (dec x)
                   (conj res (r/digit 9)))
            res))
        (h/encode conf))))
