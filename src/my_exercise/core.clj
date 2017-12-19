(ns my-exercise.core
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.reload :refer [wrap-reload]]
            [my-exercise.home :as home]
            [my-exercise.ocd-search :refer [ocd-search]]))

(defroutes app
           (GET "/" [] home/page)
           (POST "/search" [] ocd-search)
           (route/resources "/")
           (route/not-found "Not found!!"))

(def handler
  (-> app

      (wrap-defaults site-defaults)
      wrap-reload))