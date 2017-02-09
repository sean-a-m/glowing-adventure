(ns emp-rest.data
  (:require [clj-uuid :as uuid]
            [clj-time.core :as t]
            [emp-rest.validation :as validate]
            [emp-rest.schema :as schema]
            [emp-rest.util :as util]
            [clojure.algo.generic.functor :as f]
            [schema.core :as s]))


(defn- filter-timecards [start end ids timecards]
  (let [pred (every-pred
                 (fn [tc] (or (nil? start) (<= start (:timestamp (val tc)))))
                 (fn [tc] (or (nil? end) (>= end (:timestamp (val tc)))))
                 (fn [tc] (or (nil? ids) (contains? (into #{} ids) (:emp-id (val tc))))))]
      (filter pred timecards)))

(defn- reformat-entry [employee-kv]
      (util/kv-to-vs employee-kv :id))

(defn- format-time [tc-entry]
  (let [time-entry (:timestamp tc-entry)]
    (assoc tc-entry :timestamp (validate/get-time time-entry))))

(defn get-employees [app-state]
  (map reformat-entry (:employees @app-state)))

(defn get-employee-by-id [app-state req-id]
  (dosync
    (let [id (validate/validate-coerce-id req-id @app-state)]
      (-> @app-state
          (get-in [:employees id])
          (assoc :id id)))))

(defn get-timecards-by-id [app-state req-id req-data]
  (dosync
    (let [id (validate/validate-coerce-id req-id @app-state)
          req-clean (validate/validate-schema schema/id-timecard req-data)]
      (->> @app-state
           (:timecards)
           (filter-timecards (:start req-clean) (:end req-clean) (list id))
           (map reformat-entry)))))
          ; (map format-time)))))

(defn- assoc-employee-to-timecard
  [app-state timecard]
  (merge (get-in app-state [:employees (:emp-id timecard)]) timecard))

(defn get-timecards [app-state req-data]
  (let [req-clean (validate/validate-schema schema/timecards req-data)
        timecards (get @app-state :timecards)]
    (->> timecards
         (filter-timecards (:start req-clean) (:end req-clean) nil)
         (map reformat-entry)
         (map (partial assoc-employee-to-timecard @app-state))
         (util/paginate (:count req-clean) (:page req-clean)))))

(defn get-timecard-by-id [app-state id]
  (reformat-entry
    (get-in @app-state [:timecards id])))

(defn- remove-timecards-by-id
  [timecards id]
  (into {} ;Remove returns a sequence by default, so without rebuilding the map associating another record after removing the last item will throw an exception
    (remove #(= id (:emp-id (val %))) timecards)))

(defn delete-employee! [app-state req-data]
  (let [id (validate/coerce-uuid req-data)]
    (dosync
      (alter app-state update-in [:timecards] remove-timecards-by-id id)
      (alter app-state update-in [:employees] dissoc id))))

(defn create-employee! [app-state req-data new-id]
    (dosync
      (->> req-data
         (validate/validate-schema schema/new-employee)
         (alter app-state assoc-in [:employees new-id]))))

(defn update-employee! [app-state emp-id req-data]
  (dosync
    (let [id (validate/validate-coerce-id emp-id @app-state)]
      (->> req-data
          (validate/validate-schema schema/updated-employee)
          (alter app-state update-in [:employees id] merge)))))

(defn create-timecard! [app-state emp-id tc-id req-data]
    (dosync
      (let [id (validate/validate-active-id emp-id @app-state)
            data (validate/validate-schema schema/timecard req-data)]
        (->> (assoc data :emp-id id)
            (alter app-state assoc-in [:timecards tc-id])))))
