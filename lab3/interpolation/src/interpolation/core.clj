(ns interpolation.core
  (:require [clojure.string :as str]
            [clojure.tools.cli :refer [parse-opts]]
            [clojure.java.io :as io]
            [interpolation.input :refer [parse-point]]
            [interpolation.interpolation :refer [linear-interpolation lagrange-interpolation interpolate-series]])
  (:import (java.util Locale)))

(def algorithms
  {"linear"   {:interpolate linear-interpolation :window-size 2}
   "lagrange" {:interpolate lagrange-interpolation :window-size 4}})

(def cli-options
  [["-a" "--algorithm NAME" "Interpolation algorithm name"
    :id :algorithms
    :multi true
    :default []
    :update-fn #(conj %1 (str/lower-case %2))
    :validate [#(contains? algorithms %) (str "Must be an algorithm name (" (str/join ", " (keys algorithms)) ")")]]
   ["-s" "--step NUMBER" "Step size for point calculation"
    :id :step
    :default 1.0
    :parse-fn #(Double/parseDouble %)]
   ["-h" "--help"]])

(defn usage [options-summary]
  (->> ["Functional programming laboratory work #3:"
        ""
        "Usage: program-name options"
        ""
        "Options:"
        options-summary]
       (str/join \newline)))

(defn validate-args [args]
  (let [{:keys [options _ errors summary]} (parse-opts args cli-options)]
    (cond
      (:help options)
      {:exit-message (usage summary)}

      errors
      {:exit-message (str "The following errors occurred while parsing your command:\n" (str/join \newline errors))}

      (empty? (:algorithms options))
      {:exit-message "At least one algorithm name is required."}

      :else
      {:options options})))

(defn find-max-window-size [algorithm-names]
  (apply max (map #(get-in algorithms [% :window-size]) algorithm-names)))

(defn print-values [key result]
  (println (str/join "\t" (map #(String/format Locale/ENGLISH "%.2f" (to-array [(key %)])) result))))

(defn process-line [line points max-window-size]
  (let [point (parse-point line)
        new-points (conj points point)]
    (if (> (count new-points) max-window-size)
      (rest new-points)
      new-points)))

(defn process-algorithms [points options]
  (doseq [algorithm-name (:algorithms options)]
    (let [window-size (get-in algorithms [algorithm-name :window-size])]
      (when (>= (count points) window-size)
        (let [result (interpolate-series points (:step options) window-size (get-in algorithms [algorithm-name :interpolate]))]
          (println (str (str/capitalize algorithm-name) " interpolation result:"))
          (print-values :x result)
          (print-values :y result))))))

(defn -main [& args]
  (let [{:keys [options exit-message]} (validate-args args)]
    (if exit-message
      (println exit-message)
      (let [max-window-size (find-max-window-size (:algorithms options))]
        (with-open [rdr (clojure.java.io/reader *in*)]
          (loop [points []]
            (if-let [line (first (line-seq rdr))]
              (let [new-points (process-line line points max-window-size)]
                (process-algorithms new-points options)
                (recur new-points))
              (println "No more lines to process."))))))))