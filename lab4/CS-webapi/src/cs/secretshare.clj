(ns cs.secretshare
  (:gen-class)
  (:import [com.tiemens.secretshare.engine SecretShare]))

(defn prime384 []
  (SecretShare/getPrimeUsedFor384bitSecretPayload))

(defn prime192 []
  (SecretShare/getPrimeUsedFor192bitSecretPayload))

(defn prime4096 []
  (SecretShare/getPrimeUsedFor4096bigSecretPayload))

(defn get-prime [sym]
  (ns-resolve *ns* (symbol (str "cs.secretshare/" sym))))

(defn shamir-set-header
  [head]
  (SecretShare.
   (com.tiemens.secretshare.engine.SecretShare$PublicInfo.
    (int (:total head))
    (:quorum head)
    ((get-prime (:prime head)))
    (:description head))))

(defn shamir-get-header
  [share]
  (let [pi (.getPublicInfo share)]
    {:_id (.getUuid pi)
     :quorum (.getK pi)
     :total (.getN pi)
     :prime (condp = (.getPrimeModulus pi)
              (prime192) 'prime192
              (prime384) 'prime384
              (prime4096) 'prime4096
              (str "UNKNOWN"))
     :description (.getDescription pi)}))

(defn shamir-get-shares
  [si]
  (map (fn [_] (.getShare _)) si))

(defn map-shares
  [shares]
  (loop [[s & share] shares
         res []
         c 1]
    (let [res (conj res [c s])]
      (if (empty? share) res
          (recur share res (inc c))))))

(defn shamir-split
  [conf secnum]
  (let [si (.getShareInfos (.split (shamir-set-header conf) secnum))
        header (shamir-get-header (first si))
        shares (shamir-get-shares si)]
    (map-shares shares)))

(defn shamir-load
  [conf shares pos new]
  (conj shares
        (com.tiemens.secretshare.engine.SecretShare$ShareInfo.
         pos new
         (com.tiemens.secretshare.engine.SecretShare$PublicInfo.
          (:total conf)
          (:quorum conf)
          (SecretShare/getPrimeUsedFor4096bigSecretPayload)
          (:description conf)))))

(defn shamir-combine
  [conf shares]
  {:pre [(coll? shares)
         (>= (count  shares)
             (:quorum conf))]
   :post [(integer? %)]}
  (loop [[i & slices] shares
         res []]
    (let [pos (biginteger (first i))
          sh  (biginteger (second i))]
      (if (empty? slices)
        (.getSecret
         (.combine (shamir-set-header conf)
                   (if-not (nil? sh)
                     (shamir-load conf res pos sh) res)))
        (recur slices
               (if-not (nil? sh)
                 (shamir-load conf res pos sh) res))))))
