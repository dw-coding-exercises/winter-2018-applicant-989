(ns my-exercise.geoservice
  (:require [clojure.string :as str]
            [clojure.pprint :as pp]
            [my-exercise.utility :as util]
            [cemerick.url :refer (url url-encode)]
            [cheshire.core :refer :all]))

(def tamu-api-key "ddcabef1ceec4cee811ea93af3c04578")       ;; My free account. 2500 address limit, so test reasonably

(def tamu-geocode
  "https://geoservices.tamu.edu/Services/Geocode/WebService/GeocoderWebServiceHttpNonParsed_V04_01.aspx")
(def tamu-geocode-api3
  "https://geoservices.tamu.edu/Services/Geocode/WebService/GeocoderWebServiceHttpNonParsedDetailed_V04_01.aspx?")
(def tamu-normalization
  "https://geoservices.tamu.edu/Services/AddressNormalization/WebService/v04_01/HTTP/default.aspx")


(defn election-addr-verified
  "Given a map of address components, returns TAMU-verified
   map of address components suitable for TurboVote ODC search.

   Texas A&M Geoservices is verifier: https://geoservices.tamu.edu/Services/Geocode/WebService/GeocoderWebServiceDetailed.aspx

   Returns nil if address verification fails or yields zero results.

   Example:
            (election-addr-verified {:street   \"171 Wset 73\"
                                     :street-2 \"Apt No Such\"
                                     :city     \"New Yrok\"
                                     :state    \"NY\"
                                     :zip      \"10023\"})

       => {:city \"New York\",
           :county \"New York\",
           :state \"NY\",
           :zip \"10023\",
           :zip-centroid [\"40.777277\" \"-73.982489\"]}"

  [{:keys [street street-2 city state zip]}]

  (let [api-reqd {:version   "4.01"
                  :apiKey    tamu-api-key
                  :census    "false"
                  :geom      "true"
                  :allowTies "false"
                  :verbose   "true"
                  :format    "json"}

        response (parse-string
                   (util/svc-get-url-body
                     (str (assoc (url tamu-geocode-api3)
                            :query (merge api-reqd
                                          {:streetAddress (url-encode (str/join "," [street street-2]))
                                           :city          (url-encode city)
                                           :state         (url-encode state)
                                           :zip           (url-encode zip)}))))
                   true)
        {:keys [FeatureMatchingResultCount
                ExceptionOccured]} response] ;; typo reported to TAMU

    (when-not (or (= FeatureMatchingResultCount "0")
                  (= ExceptionOccured "True"))            ;; "Occured" typo reported to TAMU
      (let [best (->> (:OutputGeocodes response)
                     (sort-by #(Integer/parseInt %))
                     reverse
                     first)
            {:keys [Latitude Longitude]} (:OutputGeocode best)
            {:keys [City County Zip]} (:ReferenceFeature best)]

        {:city City
         :county County
         :state state ;; Ref State would be full name
         :zip Zip
         ;; todo work off geocode to list nearby polling places
         :zip-centroid [Latitude Longitude]}))))
