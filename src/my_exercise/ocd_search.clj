(ns my-exercise.ocd-search
  (:require [clojure.pprint :as pp]
            [hiccup.page :refer [html5]]
            [my-exercise.utility :as util]
            [clojure.string :as str]))

(declare search-to-ocd-ids
         turbo-vote-elections-url)

(defn ocd-search [request]
  (prn :bam-search!)
  (let [{:keys [cookies params]} request
        search (select-keys params [:street :street-2 :city :state :zip])]
    (prn :svc-params search)
    (if-let [ocd-ids (search-to-ocd-ids search)]
      (let [turbo (turbo-vote-elections-url ocd-ids)
            elections (util/edn-response-to-map
                        (util/svc-get-url
                          turbo))]
        (do (prn :turbo turbo)
            (prn :elections elections)
            (html5 (str elections))))
      (html5 "No elections upcoming"))
    #_ (html5
      (if (nil? ));; (header request)
      (format "<h1>Hello %s search</h1>"
              (str/join ", " [street city zip])))))

(def or-st-dist "https://api.turbovote.org/elections/upcoming?district-divisions=ocd-division/country:us/state:or")
(def va-st-dist "https://api.turbovote.org/elections/upcoming?district-divisions=ocd-division/country:us/state:va/place:chilhowie")


(def confetti {:street "1207 Horsehoe Bend Rd"
               :city "Chilhowie"
               :state "VA"
               :zip "24319"})

(defn turbo-vote-elections-url [ocd-ids]
  (pp/cl-format nil "https://api.turbovote.org/elections/upcoming?district-divisions=~{~a~^,~}" ocd-ids))

(defn search-to-ocd-ids [{:keys [street street-2 city state zip]}]
  (letfn[(build-ocd-id [state place]
           ;; re the following, see http://www.lispworks.com/documentation/HyperSpec/Body/22_c.htm
            (pp/cl-format nil "ocd-division/country:us/state:~(~a~)~@[/place:~(~a~)~]"
                                      state place))]
    (when (not (str/blank? state))
      (remove nil?
            [(build-ocd-id state nil)
             (when (not (str/blank? city))
              (build-ocd-id state city))]))))

#_ (pp/cl-format nil "https://api.turbovote.org/elections/upcoming?district-divisions=~{~a~^,~}"
                 (search-to-ocd-ids confetti))

#_ (util/edn-response-to-map (util/svc-get-url va-st-dist))
#_(let [raw (util/svc-get-url or-st-dist)
        pbr (java.io.PushbackReader.
              (clojure.java.io/reader raw))]
    (edn/read pbr))




