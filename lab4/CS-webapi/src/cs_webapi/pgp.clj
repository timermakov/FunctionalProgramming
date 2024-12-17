(ns cs-webapi.pgp
  (:require
   [clojure.java.io :as io]
   [clj-pgp.core :as pgp]
   [clj-pgp.keyring :as kr]
   [clj-pgp.generate :as pgp-gen]
   [clj-pgp.message :as pgp-msg]
   [clj-pgp.signature :as pgp-sign]))

(def keyring (atom nil))

(defn init [conf]
  {:pre [(contains? conf :pgp-pub-keyring)]}

  (let [pub (kr/load-public-keyring
             (io/file (:pgp-pub-keyring conf)))
        sec (when (contains? conf :pgp-sec-keyring)
              (let [sec-file (io/file (:pgp-sec-keyring conf))]
                (when (.exists sec-file)
                  (kr/load-secret-keyring sec-file))))]
    (swap! keyring (fn [_] {:pub pub :sec sec}))
    {:public-keys (vec (map pgp/hex-id (kr/list-public-keys pub)))
     :secret-keys (if sec
                    (vec (map pgp/hex-id (kr/list-secret-keys sec)))
                    [])}))
