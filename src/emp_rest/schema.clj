(ns emp-rest.schema
  (:require [schema.core :as s]))

(def new-employee
  {:name   s/Str
   :active s/Bool})

(def timecard
  {:timestamp s/Int
   :hours     (s/both s/Num (s/pred pos?))})


(def id-timecard
  {(s/optional-key :start) s/Num
   (s/optional-key :end)   s/Num})

(def timecards
  {(s/optional-key :start) s/Int
   (s/optional-key :end)   s/Int
   (s/optional-key :count) (s/both s/Int (s/pred pos?))
   (s/optional-key :page) s/Int})

(def updated-employee
  {(s/optional-key :name)   s/Str
   (s/optional-key :active) s/Bool})