(ns reagent-internals.prod
  (:require [reagent-internals.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
