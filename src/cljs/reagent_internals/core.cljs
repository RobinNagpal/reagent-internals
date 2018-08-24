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


(def counter (ratom/atom 4))

(defn counter-component []
  [:div
   [:h3 "counter-component"]
   [:div "counter : " @counter]])

(defn component-using-make-reaction []
  [:div#component-using-make-reaction
   [:div>h3 "component-using-make-reaction"]
   [:div "Sate 2 - var a : " @app-var2-reaction]])

(defn component-using-atom []
  [:div#component-using-atom
   [:div>h3 "component-using-atom"]
   [:div "Sate 1 - var1 : " (get-in @app-state [:state-var-1 :var-a])]])


(defn form1 [a]
  [:div
   [:div "form1 props = " (pr-str (reagent/props (reagent/current-component)))]
   [:div "form1 children = " (pr-str (reagent/children (reagent/current-component)))]])

(defn form2 [a]
  (fn [a]
    [:div
     [:div "form2 props = " (pr-str (reagent/props (reagent/current-component)))]
     [:div "form2 children = " (pr-str (reagent/children (reagent/current-component)))]]))

(defn test-component
  []
  [:div
   [form1 {:a 1}]
   [form1 17]
   [form2 {:a 1}]
   [form2 17]])

(defn simple-component [greeting name]
  [:div {:style {:color "blue"}}
   [:span greeting] [:span name] "!"])

(defn components []
  [:div
   #_[component-using-atom]
   #_[component-using-make-reaction]
   #_[counter-component]
   #_[test-component]
   [simple-component "Good Morning " "Robin Nagpal"]])



(secretary/defroute "/" []
                    (reset! page #'home-page))

(secretary/defroute "/about" []
                    (reset! page #'about-page))

;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent/render
    [simple-component "Good Morning " "Robin Nagpal"]
    (.getElementById js/document "app")))

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
