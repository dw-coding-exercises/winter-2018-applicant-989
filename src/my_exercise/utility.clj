(ns my-exercise.utility
  (:require [org.httpkit.client :as http]
            [clojure.edn :as edn]
            [hiccup.page :refer [html5]]))

(defn edn-response-to-map [stream]
  (edn/read (java.io.PushbackReader.
              (clojure.java.io/reader stream))))

(defn svc-get-url [url]
  (println :get-url url)
  (let [{:keys [status headers body error] :as resp} @(http/get url)]
    (if error
      (cond
        (instance? java.net.ConnectException error)
        (do
          (prn "Is a service running?"))
        :default
        (println "Failed, exception. Status: " status :error (type error) error))
      (condp = status
        404 (do (println "Get status 404: " url)
                nil)
        body))))

(def postal-state-abbreviations
  (sort ["AL"
         "AK"
         "AS"
         "AZ"
         "AR"
         "AE"
         "AA"
         "AP"
         "CA"
         "CO"
         "CT"
         "DE"
         "DC"
         "FM"
         "FL"
         "GA"
         "GU"
         "HI"
         "ID"
         "IL"
         "IN"
         "IA"
         "KS"
         "KY"
         "LA"
         "ME"
         "MH"
         "MD"
         "MA"
         "MI"
         "MN"
         "MS"
         "MO"
         "MT"
         "NE"
         "NV"
         "NH"
         "NJ"
         "NM"
         "NY"
         "NC"
         "ND"
         "OH"
         "OK"
         "OR"
         "PW"
         "PA"
         "PR"
         "RI"
         "SC"
         "SD"
         "TN"
         "TX"
         "UT"
         "VT"
         "VI"
         "VA"
         "WA"
         "WV"
         "WI"
         "WY"]))
