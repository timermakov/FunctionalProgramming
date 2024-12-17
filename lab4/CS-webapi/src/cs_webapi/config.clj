(ns cs-webapi.config
  (:require [clojure.java.io :as io]
            [schema.core :as s]
            [ring.swagger.json-schema :as rjs]
            [cheshire.core :refer :all]))

(def config-default {:total (Integer. 5)
                     :quorum (Integer. 3)
                     :max 3072
                     :prime "prime4096"
                     :alphabet "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
                     :salt "FT9NRt3KvGZO3eMpKECOVCzjzZjFOD1BcvUqajeHM3eS9qsN"
                     :pgp-pub-keyring (str (System/getenv "HOME") "/.gnupg/pubring.kbx")})

(defn- k [type key default]
  (rjs/field type {:example (get default key)}))

(def config-scheme
  {(s/optional-key :total)    (k s/Int :total    config-default)
   (s/optional-key :quorum)   (k s/Int :quorum   config-default)
   (s/optional-key :alphabet) (k s/Str :alphabet config-default)
   (s/optional-key :salt)     (k s/Str :salt     config-default)
   (s/optional-key :prime)    (k s/Str :prime    config-default)
   (s/optional-key :max)      (k s/Int :max      config-default)
   (s/optional-key :pgp-pub-keyring) (k s/Str :pgp-pub-keyring config-default)})

(s/defschema Config
  {(s/required-key :config) config-scheme})

(s/defschema Secret
  {(s/required-key :data)
   (rjs/field s/Str {:example "Test seed phrase"})
   (s/optional-key :config) config-scheme})

(s/defschema Shares
  {(s/required-key :data)
   (rjs/field
    [s/Str]
    {:example
     ["V8U4QGN5Z2GSG4MX4M7XU8VMX2DZBNPKVEG9XBM3N6NG5DUW",
      "KZU575KX6LQBGZX6957NH2XMV5LZZSP58QPLL8HN47MQNK7C8"
      "DEUWQ36ZWRRUDZ3N4PV8UVZ7QW595CMDGLLQV2BDEG3RW8RCM"
      "EXU9Z37L9LMBXWDN5E72FD5W6W8X5A7NPW8L25SG3PWPG6MHX"
      "72U9P4DG88ZSEKMPM8RPU88RVMPWRH4PWEXGE5TXLEDVG75AL"]})
   (s/optional-key :config) config-scheme})

(s/defschema Keyring
  {(s/optional-key :data) {(s/optional-key :public-keys) [s/Str]
                           (s/optional-key :secret-keys) [s/Str]}
   (s/required-key :config) config-scheme})

(defn config-read
  "read configs"
  ([] (config-read config-default))
  ([default]
   (let [home (System/getenv "HOME")
         pwd  (System/getenv "PWD")]
     (loop [[p & paths] ["/etc/cs/config.json"
                         (str home "/.cs/config.json")
                         (str pwd "/config.json")]
            res default]
       (let [res (merge res
                        (if (.exists (io/as-file p))
                          (conj {:config p} (parse-stream (io/reader p) true))))]
         (if (empty? paths) (conj {:config false} res)
             (recur paths res)))))))

(defn config-write
  "write configs to file"
  [conf file]
  (generate-stream conf (io/writer file)
                   {:pretty true}))

