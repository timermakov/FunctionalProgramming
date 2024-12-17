(ns cs-webapi.handler
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [schema.core :as s]
            [cs.core :as cs]
            [ring.middleware.defaults :refer
             [wrap-defaults site-defaults]]
            [ring.middleware.session :refer :all]
            [markdown.core :as md]
            [cs-webapi.config :refer :all]
            [cs-webapi.pgp :as pgp]))

(defn- get-config [obj]
  (if (contains? obj :config)
    (let [mc (merge config-default (:config obj))]
      (merge mc {:total  (Integer. (:total mc))
                 :quorum (Integer. (:quorum mc))}))
    nil))

(defn- run-cs [func obj schema]
  (if-let [conf (get-config obj)]
    {:data (func conf (:data obj))
     :config conf}
    {:data (func config-default (:data obj))
     :config config-default}))

(def rest-api
  (api
   {:swagger
    {:ui "/"
     :spec "/swagger.json"
     :data {:info
            {:version "0.1.0"
             :title "CS-webapi"
             :description "CS WEB API"
             :contact {:url "https://github.com/timermakov/FunctionalProgramming/lab4"}}}}}

   (context "/" []
     :tags ["static"]
     (GET "/readme" request
       {:headers {"Content-Type"
                  "text/html; charset=utf-8"}
        :body (md/md-to-html-string
               (slurp "README.md"))}))

   (context "/pgp/v1" []
     :tags ["PGP"]

     (POST "/init" []
       :return Keyring
       :body [config Config]
       :summary "Инициализация связки ключей pgp, возвращает список известных ключей"
       (ok (let [conf (get-config config)]
             {:data (pgp/init conf)
              :config conf}))))

   (context "/cs/v1" []
     :tags ["CS"]

     (POST "/secrets" []
       :return Shares
       :body [secret Secret]
       :summary "Разделение секрета на части"
       :description "
Принимает структуру JSON, состоящую из строки secret и структуры config, состоящей из необязательных полей (при использовании применяются значения по умолчанию)
Она выполняет разделение secret на secret и возвращает массив строк и полную конфигурацию
"
       (ok (run-cs cs/encode secret Secret)))

     (PUT "/secrets" []
       :return Secret
       :body [shares Shares]
       :summary "Соединение частей в секрет"
       (ok (run-cs cs/decode shares Shares)))

     (GET "/random" []
       :return Secret
       :body [config Config]
       :summary "Генерация случайной строки определенной длины"
       (ok (if-let [conf (get-config config)]
             {:data (cs/generate conf (:max conf))
              :config conf}))))))

(def rest-api-defaults
  "Конфигурация по умолчанию для веб-сайта, доступного через браузер и защищенного по протоколу HTTPS."
  (-> site-defaults
      (assoc-in [:cookies] false)
      (assoc-in [:security :anti-forgery] false)
      (assoc-in [:security :ssl-redirect] false)
      (assoc-in [:security :hsts] true)))

(def app
  (wrap-defaults rest-api rest-api-defaults))
