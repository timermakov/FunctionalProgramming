(ns problem7.core
  (:gen-class))

; Монолитная реализация с использованием хвостовой рекурсии
(defn nth-prime-tail-recursion [n]
  (loop [count 0
         num 2]
    (let [is-prime (loop [i 2]
                     (cond
                       (> (* i i) num) true
                       (zero? (mod num i)) false
                       :else (recur (inc i))))]
      (if (= count n)
        (dec num)
        (if is-prime
          (recur (inc count) (inc num))
          (recur count (inc num)))))))

; Монолитная реализация с использованием рекурсии 
(defn nth-prime-recursion [n]
  (letfn [(is-prime? [num divisor]
            (if (> (* divisor divisor) num)
              true
              (if (zero? (mod num divisor))
                false
                #(is-prime? num (inc divisor)))))  ; Return a function for trampoline
          (find-prime [count num]
            (if (= count n)
              #(dec num)  ; Return a function that returns (dec num)
              (if (trampoline is-prime? num 2)
                #(find-prime (inc count) (inc num))
                #(find-prime count (inc num)))))]
    (trampoline find-prime 0 2)))  ; Start count at 0

; Модульная реализация с использованием filter
(defn is-prime? [n]
  (loop [divisor 2]
    (cond
      (> (* divisor divisor) n) true
      (zero? (mod n divisor)) false
      :else (recur (inc divisor)))))

(defn nth-prime-modular [n]
  (nth (filter is-prime? (iterate inc 2)) (dec n)))

; Генерация последовательности при помощи отображения (map)
(defn nth-prime-map [n]
  (nth (map first
            (filter second
                    (map (fn [num]
                           [num (is-prime? num)])
                         (iterate inc 2))))
       (dec n)))

; Работа со специальным синтаксисом для циклов (for)
(defn nth-prime-for [n]
  (nth (for [num (iterate inc 2)
             :when (is-prime? num)]
         num)
       (dec n)))

; Работа с бесконечными списками (ленивые коллекции)
(defn primes []
  (filter is-prime? (iterate inc 2)))

(defn nth-prime-lazy [n]
  (nth (primes) (dec n)))

(defn -main
  "Solutions for problem 7"
  [& args]
  (println "Monolith tail recursion:")
  (println (nth-prime-tail-recursion 10001))
  (println "Monolith non-tail recursion:")
  (println (nth-prime-recursion 10001))
  (println "Modular:")
  (println (nth-prime-modular 10001))
  (println "Map:")
  (println (nth-prime-map 10001))
  (println "For:")
  (println (nth-prime-for 10001))
  (println "Lazy:")
  (println (nth-prime-lazy 10001)))
