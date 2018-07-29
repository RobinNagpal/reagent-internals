(ns reagent-internals.core
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.ratom :as ratom]
            [secretary.core :as secretary :include-macros true]
            [accountant.core :as accountant]))

;; -------------------------
;; Views

(defn home-page []
  [:div [:h2 "Welcome to reagent-internals"]
   [:div [:a {:href "/about"} "go to about page"]]])

(defn about-page []
  [:div [:h2 "About reagent-internals"]
   [:div [:a {:href "/"} "go to the home page"]]])

;; -------------------------
;; Routes

(defonce page (atom #'home-page))

(defn current-page []
  [:div [@page]])

(def app-state (reagent.ratom/atom {:state-var-1 {:var-a 2
                                                  :var-b 3}
                                    :state-var-2 {:var-a 7
                                                  :var-b 9}}))

(def app-var2-reaction (reagent.ratom/make-reaction
                         #(get-in @app-state [:state-var-2 :var-a])))

(defn component-using-make-reaction []
  [:div#component-using-make-reaction
   [:div>h3 "component-using-make-reaction"]
   [:div "Sate 2 - var a : " @app-var2-reaction]])

(defn component-using-atom []
  [:div#component-using-atom
   [:div>h3 "component-using-atom"]
   [:div "Sate 1 - var1 : " (get-in @app-state [:state-var-1 :var-a])]])


(defn components []
  [:div
   [component-using-atom]
   [component-using-make-reaction]])



(secretary/defroute "/" []
                    (reset! page #'home-page))

(secretary/defroute "/about" []
                    (reset! page #'about-page))

;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent/render [components] (.getElementById js/document "app")))

(defn init! []
  (accountant/configure-navigation!
    {:nav-handler
     (fn [path]
       (secretary/dispatch! path))
     :path-exists?
     (fn [path]
       (secretary/locate-route path))})
  (accountant/dispatch-current!)
  (mount-root))
