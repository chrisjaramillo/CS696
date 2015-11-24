(ns assignment4.core
  (:require [reagent.core :as r]))

(enable-console-print!)

(println "Edits to this text should show up in your developer console.")

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (r/atom {:draw-state :none
                            :shape :none
                            :start 0
                            :end 0
                            :clicks 0
                            :shapes '()
                            :current-x 0
                            :current-y 0
                            :start-x 0
                            :start-y 0
                            :current-shape '()}))

(defn rectangle-click
  []
  (swap! app-state assoc-in [:draw-state] :selected)
  (swap! app-state assoc-in [:shape] :rectangle))

(defn undo-click
  []
  (swap! app-state assoc-in [:draw-state] :none)
  (swap! app-state assoc-in [:shape] :none)
  (swap! app-state assoc-in [:shapes] (pop (:shapes @app-state))))

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
  [x]
  (let [deltaX (- (:current-x @app-state) (:start-x @app-state))
        deltaY (- (:current-y @app-state) (:start-y @app-state))
        deltaX2 (Math/pow deltaX 2)
        deltaY2 (Math/pow deltaY 2)
        radius (Math/sqrt (+ deltaX2 deltaY2))]
    (swap! app-state assoc-in [:current-shape] (list [:circle {:cx (:start-x @app-state) :cy (:start-y @app-state) :fill "none" :r radius}]))))

(defn draw-line
  [x]
  (println @app-state)
  (swap! app-state assoc-in [:current-shape] (list [:line {:x1 (:start-x @app-state) :y1 (:start-y @app-state) :x2 (:current-x @app-state) :y2 (:current-y @app-state)}])))

(defn draw-rectangle
  [x]
  (let [width (- (:current-x @app-state) (:start-x @app-state))
        height (- (:current-y @app-state) (:start-y @app-state))]
    (swap! app-state assoc-in [:current-shape] (list [:rect {:x (:start-x @app-state)
                                                             :y (:start-y @app-state)
                                                             :width width
                                                             :height height
                                                             :fill "none"}]))))

(defn start-drawing
  [x]
  (swap! app-state assoc-in [:start-x] (-> x .-clientX))
  (swap! app-state assoc-in [:start-y] (-> x .-clientY))
  (swap! app-state assoc-in [:draw-state] :started))

(defn finish-drawing
  [x]
  (swap! app-state assoc-in [:draw-state] :done)
  (swap! app-state update-in [:shapes] conj (:current-shape @app-state))
  (swap! app-state assoc-in [:current-shape] '()))

(defn drawing-click
  [x]
  (cond
    (= (@app-state :draw-state) :selected) (start-drawing x)
    (= (@app-state :draw-state) :started) (finish-drawing x))
  (println @app-state))

(defn mouse-moving
  [x]
  (swap! app-state assoc-in [:current-x] (-> x .-clientX))
  (swap! app-state assoc-in [:current-y] (-> x .-clientY))
  (when (= (@app-state :draw-state) :started)
    (cond
      (= (@app-state :shape) :line)(draw-line x)
      (= (@app-state :shape) :circle)(draw-circle x)
      (= (@app-state :shape) :rectangle)(draw-rectangle x)))
  )

(defn hello-world []
  [:div
   [:svg {:width 800 :height 800 :stroke "black"
          :style {:position :fixed :top 0 :left 0 :border "red solid 1px"}
          :on-click drawing-click
          :on-mouse-move mouse-moving}
    (:shapes @app-state)
    (:current-shape @app-state)
   ]
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
