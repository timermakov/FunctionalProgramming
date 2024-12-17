(ns cs.secretshare-test
  (:use midje.sweet)
  (:require
   [cs.secretshare :as csss]
   [cs.random :as rand]
   [cs.core :refer :all]
   [cs.marshalling :as ms]
   [clojure.pprint :as pp]))

(pp/pprint {"------------------------------------------" "SECRETSHARING_TESTS"})

(defn take-first-shares
  [shares]
  (take (:quorum settings) shares))

(defn take-last-shares
  [shares]
  (let [jump (- (:total settings) (:quorum settings))]
    (drop jump shares)))

(defn take-scatter-shares
  [shares]
  (let [sh shares]
    [(first sh) (nth sh 2) (nth sh 4)]))

(def id (:integer (rand/create (:length settings))))

(pp/pprint (str "ID: " id))

(pp/pprint settings)

(def rawsecrets (csss/shamir-split settings id))

(fact "ID is split in num shares"

      (let [a rawsecrets
            f (take-first-shares rawsecrets)
            l (take-last-shares rawsecrets)
            s (take-scatter-shares rawsecrets)]
        ;;(- (:total settings) (:quorum settings))
        (pp/pprint {:rawsecrets rawsecrets
                    :all a
                    :first f
                    :last l
                    :scat s})

        (fact "ID is taken from all num shares"
              (csss/shamir-combine settings rawsecrets) => id)

        (fact "ID is taken from num first quorum shares"
              (csss/shamir-combine settings f) => id)

        (fact "ID is taken from num last quorum shares"
              (csss/shamir-combine settings l) => id)

        (fact "ID is taken from num scattered quorum shares"
              (csss/shamir-combine settings s) => id)

        (fact "ID is taken from num shuffled quorum shares"
              (csss/shamir-combine settings
                                   (take (:quorum settings)
                                         (shuffle rawsecrets))) => id)))
