(ns emp-rest.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [ring.util.response :refer [response]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [emp-rest.middleware :as m]
            [clj-uuid :as uuid]
            [emp-rest.data :as data]))

(defn app-routes [app-state]
  (compojure.core/routes

    (GET "/" []
      {:status 418 :body "wololo"})

    (GET "/employees" []
      (response
        (data/get-employees app-state)))

    (POST "/employees" request
      (let [new-id (uuid/v1)]
        (data/create-employee! app-state (:body request) new-id)
        (response {:id new-id})))

    (GET "/employees/:id" [id :as request]
      (let [json-data (:body request)]
        (if (= id "time")
          (response (data/get-timecards app-state json-data))
          (response (data/get-employee-by-id app-state id)))))

    (PUT "/employees/:id" [id :as request]
      (let [json-data (:body request)]
        (data/update-employee! app-state id json-data)))

    (DELETE "/employees/:id" [id]
      (data/delete-employee! app-state id))

    (GET "/employees/:id/time" [id :as request]
      (let [json-data (:body request)]
        (response (data/get-timecards-by-id app-state id json-data))))

    (POST "/employees/:id/time" [id :as request]
      (let [json-data (:body request)
            tc-id (uuid/v1)]
        (data/create-timecard! app-state id tc-id json-data)))

    (route/not-found "Not Found")))

(def app
  (let [app-state (ref {:employees {} :timecards {}})]
    (-> (handler/site (app-routes app-state))
        (m/sanitize-inputs)
        (wrap-json-body {:keywords? true})
        (m/wrap-ex)
        (wrap-json-response))))
