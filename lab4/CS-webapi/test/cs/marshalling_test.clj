(ns cs.marshalling-test
  (:use midje.sweet)
  (:require [clojure.pprint :as pp]
            [cs.core :refer :all]
            [cs.marshalling :as ms]
            [cs.intcomp :as ic]))

(pp/pprint {"------------------------------------------" "MARSHALLING_TESTS"})

;; Define the test-settings configuration
(def test-settings {:salt "FT9NRt3KvGZO3eMpKECOVCzjzZjFOD1BcvUqajeHM3eS9qsN"
                    :description "Crypto splitter"
                    :protocol "CS"
                    :alphabet "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
                    :quorum 3
                    :prime "prime4096"
                    :type "WEB"
                    :total 5
                    :max 1024
                    :length 6
                    :entropy 3.1})

;; Random share
(def testshare "ETWTWBCXXC")
(def testintseq [68 69 84 87 84 87 66 67 88 88 67])
(def testintcomp [11 0 -673921596 -1010640940 12835032])
(def testuintcomp (11 2147483647N 1473562051N 1136842707N 2160318679N))
(def testintshare [73 4783830863 6413244902 3348030084 9528735128 3662428236 1179060863 4890373559 2849561951 5105731336 2618884129 5466927911 6046625017 3175759218 3472356969 5150433918 3956919956 341961234 4009712991 5640110705 1])

(fact "Marshalling strings into a sequences of integers"
      (fact "from string to sequence"
            (ms/str2intsequence testshare) => testintseq)
      (fact "from sequence to string"
            (ms/intsequence2string testintseq) => testshare))

(fact "Compressing integers"
      (fact "default codec"
            (ic/compress testintseq) => testintcomp
            (ic/decompress testintcomp) => testintseq))

(fact "Making all negative integers unsigned"
      (ms/int2uint testintcomp) => testuintcomp
      (ms/uint2int testuintcomp) => testintcomp)
