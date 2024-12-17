(ns cs.core-test
  (:use midje.sweet)
  (:require  [cs.core :refer :all]
             [cs.random :as r]
             [cs.secretshare :as ss]
             [cs.marshalling :as ms]
             [hashids.core :as h]
             [clojure.pprint :as pp]))

(pp/pprint {"------------------------------------------" "CS_CORE_TESTS"})


(def salt (generate {} 32))
(def password (generate {} 16))

(pp/pprint {:password password
            :salt salt})
            :sequence (ms/string2sequenceuence password)})

(def intseq (ms/string2sequenceuence password))

(fact "String to sequence"
      (ms/sequence2string intseq) => password)

(def secrets (ms/sequence2secrets settings intseq))

(fact "Sequence to secrets"
      (ms/secrets2sequence settings secrets) => intseq
      (fact "Secrets to sequence to password"
            (ms/sequence2string (ms/secrets2sequence settings secrets)) => password))

(def raw-slices (ms/secrets2slices settings secrets))
(def encoded-slices (map #(ms/encode-hash settings %) raw-slices))
(def decoded-slices (map #(ms/decode-hash settings %) encoded-slices))

(fact "Create horizontal slices across vertical secrets"
      (fact "number equals settings total number"
            (count raw-slices) => (:total settings))
      (fact "number equals sequence of compressed ints number"
            (dec (count (first raw-slices))) => (count intseq))
      (fact "are decoded correctly to num format"
            raw-slices => decoded-slices))

(fact "Take vertical secrets from horizontal slices"
      (def decoded-secrets (ms/slices2secrets settings decoded-slices))
      decoded-secrets => secrets

      (fact "Combine secrets into sequence"
            (def decoded-sequence (ms/secrets2sequence settings decoded-secrets))
            decoded-sequence => intseq)

      (fact "Retrieve the password"
            (def decoded-password (ms/sequence2string decoded-sequence))
            (pp/pprint {:decoded-password decoded-password})
            (ms/sequence2string decoded-sequence) => password))

(fact "Public cs codec functions"
      (def pub-encoded (encode settings password))
      (pp/pprint {:pub-encoded pub-encoded})
      (fact "work with all shares"
            (decode settings pub-encoded) => password)
      (fact "work with shuffled shares"
            (decode settings
                    (shuffle
                     (encode settings password))) => password)
      (fact "work with minimum quorum"
            (decode settings
                    (take (:quorum settings)
                          (shuffle (encode settings password)))) => password))

