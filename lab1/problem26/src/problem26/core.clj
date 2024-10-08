(ns problem26.core
  (:gen-class))

(defn recurring-cycle-length [d]
  (letfn [(cycle-length [n rems pos]
            (let [rem (mod (* 10 n) d)]
              (cond
                (= rem 0) 0
                (contains? rems rem) (- pos (rems rem))
                :else (recur rem (assoc rems rem pos) (inc pos)))))]
    (cycle-length 1 {} 0)))

; Монолитная реализация с использованием хвостовой рекурсии
(defn recurring-tail-recursion [limit]
  (loop [d 2
         max-d 0
         max-len 0]
    (if (>= d limit)
      max-d
      (let [len (recurring-cycle-length d)]
        (if (> len max-len)
          (recur (inc d) d len)
          (recur (inc d) max-d max-len))))))

; Монолитная реализация с использованием рекурсии 
(defn recurring-recursion [d limit max-d max-len]
  (if (>= d limit)
    max-d
    (let [len (recurring-cycle-length d)]
      (if (> len max-len)
        (recurring-recursion (inc d) limit d len)
        (recurring-recursion (inc d) limit max-d max-len)))))

; Модульная реализация с использованием filter
(defn recurring-modular [limit]
  (let [denominators (range 2 limit)
        lengths (map (fn [d] [d (recurring-cycle-length d)]) denominators)
        filtered-lengths (filter (fn [[_ len]] (pos? len)) lengths)]
    (first (reduce (fn [max-pair current-pair]
                     (if (> (second current-pair) (second max-pair))
                       current-pair
                       max-pair))
                   [0 0]
                   filtered-lengths))))

; Генерация последовательности при помощи отображения (map)
(defn recurring-map [limit]
  (first (apply max-key second
                (map (fn [d] [d (recurring-cycle-length d)])
                     (range 2 limit)))))

; Работа со специальным синтаксисом для циклов (for)
(defn recurring-for [limit]
  (let [lengths (for [d (range 2 limit)]
                  [d (recurring-cycle-length d)])]
    (first (apply max-key second lengths))))

; Итератор
(defn recurring-iterator [limit]
  (let [state (atom 1)]
    (defn next-pair []
      (let [d (swap! state inc)]
        (when (< d limit)
          [d (recurring-cycle-length d)])))
    (defn pairs []
      (lazy-seq
       (when-let [pair (next-pair)]
         (cons pair (pairs)))))
    (first (apply max-key second (pairs)))))

(defn -main
  "Solutions for problem 7"
  [& args]
  (println "Monolith tail recursion:")
  (println (recurring-tail-recursion 1000))
  (println "Monlith recursion:")
  (println (recurring-recursion 2 1000 0 0))
  (println "Modular:")
  (println (recurring-modular 1000))
  (println "Map:")
  (println (recurring-map 1000))
  (println "For:")
  (println (recurring-for 1000))
  (println "Iterator:")
  (println (recurring-iterator 1000)))
