(ns interpolation.input
  (:require [clojure.string :as str]))

(defn parse-point [line]
  (let [[x y] (str/split line #"[;\t]")]
    {:x (Double/parseDouble x) :y (Double/parseDouble y)}))
