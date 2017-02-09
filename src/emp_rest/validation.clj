(ns emp-rest.validation
  (:require [clj-uuid :as uuid]
            [clj-time.format :as f]
            [clj-time.coerce :as c]
            [clojure.data.json :as json]
            [schema.coerce :as coerce]
            [schema.core :as s]))

(defn validate-schema [schema data]
  (if (nil? (s/check schema data))
    data
    (throw (ex-info "Invalid schema" {:input data :error "Invalid schema"}))))

(defn get-time [s]
  (c/to-string
    (c/from-long s)))

(defn make-time [t]
  (c/to-long t))

(defn coerce-uuid [s]
  (if (uuid/uuidable? s)
    (uuid/as-uuid s)
    (throw (ex-info "Invalid UUID" {:input s :msg "Invalid UUID"}))))

(defn validate-coerce-id [id-string app-state]
  (let [id (coerce-uuid id-string)]
    (if (nil? (get-in app-state [:employees id]))
             (throw (ex-info "Employee does not exist" {:input id :error "Employee does not exist"}))
             id)))

(defn validate-employee-status [id app-state]
  (let [active-id? (get-in app-state [:employees id :active])]
    (if (true? active-id?)
      id
      (throw (ex-info "ID is for inactive employee" {:input id :error "Can't add timecard for inactive employee"})))))

(defn validate-active-id [id-string app-state]
  (-> id-string
      (validate-coerce-id app-state)
      (validate-employee-status app-state)))