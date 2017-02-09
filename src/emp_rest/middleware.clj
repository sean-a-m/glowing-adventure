(ns emp-rest.middleware
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [ring.util.response :refer [response]]
            [emp-rest.util :as util])
  (:import [org.jsoup Jsoup]
           [org.jsoup.safety Whitelist]))


(defn wrap-ex [handler]
  (fn [request]
    (try (handler request)
         (catch Exception e
           ;Every exception that I throw is an ex-info.  If an exception is not an ex-info, this does not handle it.
           (util/error-response (ex-data e))))))

(defn clean-map-entry [i]
  (let [wl (. Whitelist none)]
    (if (string? i) (Jsoup/clean i wl) i)))

(defn sanitize-inputs [handler]
  (let [wl (. Whitelist none)]
    (fn [request]
      (handler
        (if (map? (:body request))
          (assoc request :body
             (util/update-map (:body request) clean-map-entry))
          request)))))