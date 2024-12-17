(ns cs-webapi.core-test
  (:require [cheshire.core :as cheshire]
            [midje.sweet :refer :all]
            [cs-webapi.handler :refer :all]
            [ring.mock.request :as mock]))

(defn parse-body [body]
  (cheshire/parse-string (slurp body) true))
