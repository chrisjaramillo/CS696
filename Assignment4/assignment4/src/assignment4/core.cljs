(ns assignment4.core
  (:require [reagent.core :as reagent :refer [atom]]))

(enable-console-print!)

(println "Edits to this text should show up in your developer console.")

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom {:text "Hello world!" :draw-state :false :shape :none :start 0 :end 0 :clicks 0}))

(defn rectangle-click
  []
  (swap! app-state assoc-in [:draw-state] :true)
  (swap! app-state assoc-in [:shape] :rectangle)
  (swap! app-state assoc-in [:clicks] 1))

(defn clear-click
  []
  (swap! app-state assoc-in [:draw-state] :false)
  (swap! app-state assoc-in [:shape] :none)
  (swap! app-state assoc-in [:clicks] 0))

(defn circle-click
  []
  (swap! app-state assoc-in [:draw-state] :true)
  (swap! app-state assoc-in [:shape] :circle))

(defn line-click
  []
  (swap! app-state assoc-in [:draw-state] :true)
  (swap! app-state assoc-in [:shape] :line))

(defn draw-circle
  []
  (if (= (@app-state :clicks) 1)
    (swap! app-state assoc-in [:start] (.-clientX %))
    (swap! app-state assoc-in [:text] "STOPPING"))
  (println app-state))

(defn draw-line
  [])

(defn draw-rectangle
  [])

(defn drawing-click
  []
  (if (= (@app-state :draw-state) :true)
    ((swap! app-state assoc-in [:text] (str (@app-state :shape) " DRAWING"))
      (cond
        (= (@app-state :shape) :rectangle) draw-rectangle
        (= (@app-state :shape) :circle) draw-circle
        (= (@app-state :shape) :line) draw-line))
    (swap! app-state assoc-in [:text] "NOT DRAWING"))
  )

(defn hello-world []
  [:div
   [:svg {:width 800 :height 800 :stroke "black"
          :style {:position :fixed :top 0 :left 0 :border "red solid 1px"} :on-click drawing-click}
    [:rect {:x 0 :y 700 :width 200 :height 100 :fill "none"}]
    [:rect {:x 200 :y 700 :width 200 :height 100 :fill "none"}]
    [:rect {:x 400 :y 700 :width 200 :height 100 :fill "none"}]
    [:rect {:x 600 :y 700 :width 200 :height 100 :fill "none"}]
    [:text {:x 70 :y 750 :on-click line-click} "LINE"]
    [:text {:x 270 :y 750 :on-click circle-click} "CIRCLE"]
    [:text {:x 450 :y 750 :on-click rectangle-click} "RECTANGLE"]
    [:text {:x 670 :y 750 :on-click clear-click} "CLEAR"]]
    [:h1 (:text @app-state)]
   ]
  )

(reagent/render-component [hello-world]
                          (. js/document (getElementById "app")))


(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)
