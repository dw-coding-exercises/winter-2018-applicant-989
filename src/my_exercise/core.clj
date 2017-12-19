(ns my-exercise.core
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.reload :refer [wrap-reload]]
            ;;[ring.middleware.anti-forgery :refer [wrap-anti-forgery]]
            ;;[ring.middleware.session :refer [wrap-session]]
            [my-exercise.home :as home]))

(defroutes app
           (GET "/" [] home/page)
           (POST "/search" [] home/ocd-search)
           (route/resources "/")
           (route/not-found "Not found!!"))

(def handler
  (-> app

      (wrap-defaults site-defaults)
      wrap-reload))