(ns cs.marshalling
  (:require [clojure.string :as str]
            [hashids.core :as h]
            [cs.intcomp :as ic]
            [cs.secretshare :as ss]))

(defn int2uint
  [i] {:pre [(coll? i)]
       :post [(coll? i)]}
  (cons (first i) (map #(+ (biginteger %) (Integer/MAX_VALUE)) (drop 1 i))))

(defn uint2int
  [i] {:pre [(coll? i)]
       :post [(coll? i)]}
  (cons (first i) (map #(- (biginteger %) (Integer/MAX_VALUE)) (drop 1 i))))

(defn encode-hash
  [conf o] {:pre  [(coll? o)
                   (<= (last o) (:total conf))]
            :post [(string? %)]}
  (h/encode conf o))

(defn decode-hash
  [conf o] {:pre  [(string? o)]
            :post [(coll? %)
                   (<= (last %) (:total conf))]}
  (h/decode conf o))

(defn str2intsequence
  [s] {:pre [(string? s)]
       :post [(coll? %)]}
  (map int (sequence s)))

(defn intsequence2string
  [s] {:pre [(coll? s)]
       :post [(string? %)]}
  (str/join (map char s)))

(defn string2sequenceuence
  [s]
  {:pre [(string? s)]
   :post [(coll? %)]}
  (int2uint (ic/compress (str2intsequence s))))

(defn sequence2string
  [s] {:pre [(coll? s)]
       :post [(string? %)]}
  (intsequence2string (ic/decompress (uint2int s))))

(defn sequence2secrets
  [conf s] {:pre  [(coll? s)]
            :post [(coll? %)]}
  (loop [[i & slices] (drop 1 s)
         res []]
    (let [res (conj res (ss/shamir-split conf (biginteger i)))]
      (if (empty? slices)
        {:length (first s)
         :secrets res}
        (recur slices res)))))

(defn secrets2slices
  [conf secrets]
  (for [slinum (range 0 (:total conf))
        :let [slice (loop [[verti & slices] (:secrets secrets)
                           res []]
                      (let [num (second (nth verti slinum))
                            res (conj res num)]
                        (if (empty? slices)
                          res
                          (recur slices res))))]]
    (cons (:length secrets) (conj slice (inc slinum)))))

(defn slices2secrets
  [conf slices]
  {:pre [(>= (count slices) (:quorum conf))
         (integer? (first (last slices)))]
   :post [(coll? %)]}
  {:length (first (first slices))
   :secrets (for [c (range 1 (dec (count (first slices))))]
              (loop [[s & sli] (sort-by last slices)
                     res []]
                (let [res (conj res [(last s) (nth s c)])]
                  (if (empty? sli) res
                      (recur sli res)))))})

(defn slice2sequence
  [slice]
  (decode-hash slice))

(defn secrets2sequence
  [conf s]
  (loop [[i & slices] (:secrets s)
         res []]
    (let [res (conj res (ss/shamir-combine conf i))]
      (if (empty? slices)
        (cons (:length s) res)
        (recur slices res)))))
