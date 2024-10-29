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
(defn recurring-tail-recursion [d limit max-d max-len]
  (if (>= d limit)
    max-d
    (let [len (recurring-cycle-length d)]
      (if (> len max-len)
        (recur (inc d) limit d len)
        (recur (inc d) limit max-d max-len)))))

; Монолитная реализация с использованием рекурсии 
(defn recurring-recursion [d limit max-d max-len]
  (if (>= d limit)
    max-d
    (let [len (recurring-cycle-length d)]
      (if (> len max-len)
        (recurring-recursion (inc d) limit d len)
        (recurring-recursion (inc d) limit max-d max-len)))))

; Модульная реализация с использованием filter
(defn generate-cycle-lengths [limit]
  (map (fn [d] [d (recurring-cycle-length d)]) (range 2 limit)))

(defn filter-non-zero-cycles [pairs]
  (filter (fn [[_ length]] (pos? length)) pairs))

(defn find-max-cycle [pairs]
  (reduce (fn [max-pair current-pair]
            (if (> (second current-pair) (second max-pair))
              current-pair
              max-pair))
          [0 0]
          pairs))

(defn recurring-modular [limit]
  (let [pairs (generate-cycle-lengths limit)
        filtered-pairs (filter-non-zero-cycles pairs)
        [d _] (find-max-cycle filtered-pairs)]
    d))

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
  (let [state (atom 1)
        next-pair (fn []
                    (let [d (swap! state inc)]
                      (when (< d limit)
                        [d (recurring-cycle-length d)])))
        pairs (fn pairs []
                (lazy-seq
                 (when-let [pair (next-pair)]
                   (cons pair (pairs)))))]
    (first (apply max-key second (pairs)))))

(defn -main
  "Solutions for problem 26"
  []
  (println "Monolith tail recursion:")
  (println (recurring-tail-recursion 2 1000 0 0))
  (println "Monolith recursion:")
  (println (recurring-recursion 2 1000 0 0))
  (println "Modular:")
  (println (recurring-modular 1000))
  (println "Map:")
  (println (recurring-map 1000))
  (println "For:")
  (println (recurring-for 1000))
  (println "Iterator:")
  (println (recurring-iterator 1000)))
