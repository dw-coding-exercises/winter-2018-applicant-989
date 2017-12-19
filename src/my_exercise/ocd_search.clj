(ns my-exercise.ocd-search
  (:require [clojure.edn :as edn]
            [hiccup.page :refer [html5]]
            [my-exercise.utility :as util]))

(defn ocd-search [request]
  (prn :bam-search!)
  (let [{:keys [cookies params]} request]
    (prn :svc-params params)

    (html5 ;; (header request)
           "<h1>Hello search</h1>")))

(def or-st-dist "https://api.turbovote.org/elections/upcoming?district-divisions=ocd-division/country:us/state:or")

#_
    (let [raw (util/svc-get-url or-st-dist)
          pbr (java.io.PushbackReader.
                (clojure.java.io/reader raw))]
      (edn/read pbr))


