(ns emp-rest.tests
  (:require [compojure.core :refer :all]
            [emp-rest.handler :as handler]
            [ring.server.standalone]))


(defn -main []
  (ring.server.standalone/serve handler/app {:port 3000}))
