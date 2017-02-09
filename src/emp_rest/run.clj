(ns emp-rest.run
  (:require [emp-rest.handler :as handler]
            [ring.server.standalone])
  (:gen-class))

(defn -main []
  (ring.server.standalone/serve handler/app {:port 3000}))

