(ns assignment4.core
  (:require [reagent.core :as r]))

(enable-console-print!)

(println "Edits to this text should show up in your developer console.")

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (r/atom {:text "Hello world!" :draw-state :none :shape :none :start 0 :end 0 :clicks 0 :shapes '() :current-x 0 :current-y 0}))

(defn rectangle-click
  []
  (swap! app-state assoc-in [:draw-state] :selected)
  (swap! app-state assoc-in [:shape] :rectangle))

(defn undo-click
  []
  (swap! app-state assoc-in [:draw-state] :none)
  (swap! app-state assoc-in [:shape] :none))

(defn circle-click
  []
  (swap! app-state assoc-in [:draw-state] :selected)
  (swap! app-state assoc-in [:shape] :circle)
  )

(defn line-click
  []
  (swap! app-state assoc-in [:draw-state] :selected)
  (swap! app-state assoc-in [:shape] :line))

(defn draw-circle
  []
  (if (= (@app-state :clicks) 1)
    (swap! app-state assoc-in [:start]
           )
    (swap! app-state assoc-in [:text] "STOPPING"))
  (println app-state))

(defn draw-line
  [x]
  (swap! app-state update-in [:shapes] conj (list [:line {:x1 (-> x .-clientX) :y1 (-> x .-clientY) :x2 (:current-x @app-state) :y2 (:current-y @app-state)}]) ))

(defn draw-rectangle
  [])

(defn drawing-click
  [x]
  (if (= (@app-state :draw-state) :selected)
    ((swap! app-state assoc-in [:text] (str (@app-state :shape) " DRAWING"))
      (cond
        (= (@app-state :shape) :rectangle) draw-rectangle
        (= (@app-state :shape) :circle) draw-circle
        (= (@app-state :shape) :line) (draw-line x)))
    (swap! app-state assoc-in [:text] "NOT DRAWING"))
  (println (str (-> x .-clientX) " " (-> x .-clientY)))
  )

(defn mouse-moving
  [x]
  (swap! app-state assoc-in [:current-x] (-> x .-clientX))
  (swap! app-state assoc-in [:current-y] (-> x .-clientY)))

(defn hello-world []
  [:div
   [:svg {:width 800 :height 800 :stroke "black"
          :style {:position :fixed :top 0 :left 0 :border "red solid 1px"}
          :on-click drawing-click
          :on-mouse-move mouse-moving}
    (:shapes @app-state)
    ]
    [:h1 (:text @app-state)]
   [:svg {:width 800 :height 100 :stroke "black" :style {:position :fixed :top 800 :left 0 :border "blue solid 1px"}}
    [:text {:x 70 :y 50 } "LINE"]
    [:text {:x 450 :y 50} "RECTANGLE"]
    [:text {:x 670 :y 50} "UNDO"]
    [:text {:x 270 :y 50 } "CIRCLE"]
    [:rect {:x 0 :y 0 :width 200 :height 100 :fill "white" :fill-opacity "0.0" :on-click line-click}]
    [:rect {:x 200 :y 0 :width 200 :height 100 :fill "white" :fill-opacity "0.0" :on-click circle-click}]
    [:rect {:x 400 :y 0 :width 200 :height 100 :fill "white" :fill-opacity "0.0" :on-click rectangle-click}]
    [:rect {:x 600 :y 0 :width 200 :height 100 :fill "white" :fill-opacity "0.0" :on-click undo-click}]
    ]
   ]
  )

(r/render-component [hello-world]
                          (. js/document (getElementById "app")))


(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)
