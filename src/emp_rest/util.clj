(ns emp-rest.util)

(defn paginate [perpage page coll]
  (let [perpage (or perpage 10)
        page (or page 0)
        partitioned (partition-all perpage coll)]
    (nth partitioned page nil)))

(defn kv-to-vs [map-entry key-name]
  (conj (hash-map key-name (key map-entry)) (val map-entry)))

(defn error-response
  "This is a copy-paste of ring.util.response/response with HTTP status 500"
  [body]
  {:status  500
   :headers {}
   :body    body})

(defn update-map [m f]
  "I stole this from an example on the reduce-kv doc page at https://clojuredocs.org/clojure.core/reduce-kv but it's
  pretty obvious how it works"
  (reduce-kv (fn [m k v]
               (assoc m k (f v))) {} m))
