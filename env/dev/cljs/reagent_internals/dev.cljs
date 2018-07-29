(ns ^:figwheel-no-load reagent-internals.dev
  (:require
    [reagent-internals.core :as core]
    [devtools.core :as devtools]))

(devtools/install!)

(enable-console-print!)

(core/init!)
