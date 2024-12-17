(ns cs.random
  (:gen-class)
  (:import  [java.security SecureRandom]))

; single rand digit 0-9
(defn digit
  [max]
  (.nextInt (java.security.SecureRandom.) max))

; string chaining digits up to len
(defn intchain
  [length]
  (loop [x    length
         ; 1st digit is not 0
         res (int (digit 9))]
    (if (> x 1)
      (recur (dec x) (str res (digit 10)))
      res)))

; entropy of string 
(defn entropy
  [s]
  (let [len (count s), log-2 (Math/log 2)]
    (->> (frequencies s)
         (map (fn [[_ v]]
                (let [rf (/ v len)]
                  (-> (Math/log rf) (/ log-2) (* rf) Math/abs))))
         (reduce +))))

; create random BI
(defn create
  [length]
  (let [res (intchain length)]
    {:integer (biginteger res)
     :string (str res)}))
