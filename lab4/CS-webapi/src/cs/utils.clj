(ns cs.utils
  (:require [clojure.walk :refer :all]))

(declare log!)

(defn trunc
  [s n]
  {:pre [(> n 0)
         (sequence s)]}
  (subs s 0 (min (count s) n)))

; remove empty elements
(defn compress
  [coll]
  (clojure.walk/postwalk #(if (coll? %) (into (empty %) (remove nil? %)) %) coll))

(defmacro bench
  ([& forms]
   `(let [start# (System/nanoTime)]
      ~@forms
      (- (System/nanoTime) start#))))

(defn select-all-or-nothing [m keys]
  (when (every? (partial contains? m) keys)
    (select-keys m keys)))

(defn bigdecimal->long
  [bd]
  (.longValue (* bd 100000)))

(defn long->bigdecimal
  [l]
  (/ (BigDecimal. l) 100000))
