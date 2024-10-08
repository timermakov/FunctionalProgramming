(ns problem26.core-test
  (:require [clojure.test :refer :all]
            [problem26.core :refer :all]))

(deftest test-recurring-cycle-length
  (testing "recurring-cycle-length"
    (is (= 0 (recurring-cycle-length 2)))
    (is (= 1 (recurring-cycle-length 3)))
    (is (= 0 (recurring-cycle-length 4)))
    (is (= 6 (recurring-cycle-length 7)))
    (is (= 0 (recurring-cycle-length 8)))
    (is (= 1 (recurring-cycle-length 9)))
    (is (= 0 (recurring-cycle-length 10)))))

(deftest test-recurring-tail-recursion
  (testing "recurring-tail-recursion"
    (is (= 7 (recurring-tail-recursion 10)))
    (is (= 983 (recurring-tail-recursion 1000)))))

(deftest test-recurring-recursion
  (testing "recurring-recursion"
    (is (= 7 (recurring-recursion 2 10 0 0)))
    (is (= 983 (recurring-recursion 2 1000 0 0)))))

(deftest test-recurring-modular
  (testing "recurring-modular"
    (is (= 7 (recurring-modular 10)))
    (is (= 983 (recurring-modular 1000)))))

(deftest test-recurring-map
  (testing "recurring-map"
    (is (= 7 (recurring-map 10)))
    (is (= 983 (recurring-map 1000)))))

(deftest test-recurring-for
  (testing "recurring-for"
    (is (= 7 (recurring-for 10)))
    (is (= 983 (recurring-for 1000)))))

(deftest test-recurring-iterator
  (testing "recurring-iterator"
    (is (= 7 (recurring-iterator 10)))
    (is (= 983 (recurring-iterator 1000)))))
