(ns my-exercise.utility
  (:require [org.httpkit.client :as http]
            [hiccup.page :refer [html5]]))

(defn svc-get-url [url]
  (println :get-url url)
  (let [{:keys [status headers body error] :as resp} @(http/get url)]
    (println :geturlback)
    (if error
      (cond
        (instance? java.net.ConnectException error)
        (do
          (prn "Is a service running?"))
        :default
        (println "Failed, exception. Status: " status :error (type error) error))
      (condp = status
        404 (do (println "Get status 404: " url)
                (html5 [:h4 "GET status 404"]
                      [:p (str "Body: " body)]))
        (do
          ;;(println "HTTP GET headers: " headers)
          (println "svc-get-url sees HTTP GET body: " body)
          body)))))
