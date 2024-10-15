(ns problem7.core-test
  (:require [clojure.test :refer :all]
            [problem7.core :refer :all]))

(deftest test-nth-prime-tail-recursion
  (testing "nth-prime-tail-recursion"
    (is (= 13 (nth-prime-tail-recursion 6 0 2 2)))
    (is (= 104743 (nth-prime-tail-recursion 10001 0 2 2)))))

(deftest test-nth-prime-recursion
  (testing "nth-prime-recursion"
    (is (= 13 (nth-prime-recursion 6)))
    (is (= 104743 (nth-prime-recursion 10001)))))

(deftest test-nth-prime-modular
  (testing "nth-prime-modular"
    (is (= 13 (nth-prime-modular 6)))
    (is (= 104743 (nth-prime-modular 10001)))))

(deftest test-nth-prime-map
  (testing "nth-prime-map"
    (is (= 13 (nth-prime-map 6)))
    (is (= 104743 (nth-prime-map 10001)))))

(deftest test-nth-prime-for
  (testing "nth-prime-for"
    (is (= 13 (nth-prime-for 6)))
    (is (= 104743 (nth-prime-for 10001)))))

(deftest test-nth-prime-lazy
  (testing "nth-prime-lazy"
    (is (= 13 (nth-prime-lazy 6)))
    (is (= 104743 (nth-prime-lazy 10001)))))