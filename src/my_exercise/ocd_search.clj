(ns my-exercise.ocd-search
  (:require [clojure.pprint :as pp]
            [hiccup.page :refer [html5]]
            [my-exercise.utility :as util]
            [my-exercise.geoservice :as geo]
            [clojure.string :as str]))

(declare search-results-header
         search-to-ocd-ids
         turbo-vote-elections-url)

(defn ocd-search-request-handler [request]
  ;; todo make prettier
  ;; todo re-iterate search address and with cleaned data
  ;; todo support drill-down into election details
  (html5
    (util/html-header request)
    (if-let [ocd-ids (search-to-ocd-ids
                       (select-keys (:params request)
                                    [:street :street-2 :city :state :zip]))]
      (let [turbo-url (turbo-vote-elections-url ocd-ids)]
        (let [elections (util/get-service-data turbo-url)]
          (if (seq elections)
            [:div
             [:h3 "Upcoming Elections"]
             (for [{:keys [date description]} elections]
               [:div
                [:label.dtime (.format (java.text.SimpleDateFormat. "dd-MMM-yyyy") date)]
                [:label.dtime (.format (java.text.SimpleDateFormat. "hh:mm a zzz") date)]
                [:label.descr description]])]
            [:h3 "No elections are upcoming."])))
      [:h3 "Not enough information by which to search."])))

(defn turbo-vote-elections-url [ocd-ids]
  ;; todo generalize as we learn more about the TurboVote API
  (pp/cl-format nil "https://api.turbovote.org/elections/upcoming?district-divisions=~{~a~^,~}" ocd-ids))

(defn search-to-ocd-ids [search]
  (letfn [(build-ocd-id [{:keys [state county city]}]
            ;; For the cl-format DSL, see http://www.lispworks.com/documentation/HyperSpec/Body/22_c.htm,
            ;; especially the conditional formatting.
            (pp/cl-format nil "ocd-division/country:us/state:~(~a~)~@[/county:~(~a~)~]~@[/place:~(~a~)~]"
                          state county city))]
    (when-let [verified (geo/election-addr-verified search)]
      ;; todo make which admin levels are searched user-specifiable
      (->> [[:state]
            [:state :county]
            [:state :city]
            [:state :county :city]]
           (map #(select-keys verified %))
           (remove #(some str/blank? (vals %)))
           (map build-ocd-id)))))


