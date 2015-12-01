;; Christopher Jaramillo
;; CS 696
;; Spring 2015
;; Assignment 4 part 2

(ns assignment4.core
  (:require [reagent.core :as r]
            [schema.core :as s :include-macros true]))

(def shapes (s/enum :none :circle :line :rectangle))
(def draw-states (s/enum :none :selected :started))

;; define your app data so that it doesn't get over-written on reload
;; Schema defined ratom
(defonce app-state (r/atom {:draw-state draw-states
                            :shape shapes
                            :shapes '()
                            :current-x s/Num
                            :current-y s/Num
                            :start-x s/Num
                            :start-y s/Num
                            :current-shape '()}))

;; Cursors
(def draw-state (r/cursor app-state [:draw-state]))

(def selected-shape (r/cursor app-state [:shape]))

(def shape-list (r/cursor app-state [:shapes]))

(def current-x (r/cursor app-state [:current-x]))

(def current-y (r/cursor app-state [:current-y]))

(def start-x (r/cursor app-state [:start-x]))

(def start-y (r/cursor app-state [:start-y]))

(def current-shape (r/cursor app-state [:current-shape]))

;; Cursor setters
(defn set-draw-state!
  [x]
  {:pre [(s/validate draw-states x)]}
  (reset! draw-state x))

(defn set-selected-shape!
  [x]
  {:pre [(s/validate shapes x)]}
  (reset! selected-shape x))

(defn set-current-x!
  [x]
  {:pre [s/validate s/Num x]}
  (reset! current-x x))

(defn set-current-y!
  [x]
  {:pre [s/validate s/Num x]}
  (reset! current-y x))

(defn set-start-x!
  [x]
  {:pre [s/validate s/Num x]}
  (reset! start-x x))

(defn set-start-y!
  [x]
  {:pre [s/validate s/Num x]}
  (reset! start-y x))

;;Shape drawing
(defn draw-circle
  []
  (let [deltaX (- @current-x @start-x)
        deltaY (- @current-y @start-y)
        deltaX2 (Math/pow deltaX 2)
        deltaY2 (Math/pow deltaY 2)
        radius (Math/sqrt (+ deltaX2 deltaY2))]
    (reset! current-shape (list [:circle {:cx @start-x :cy @start-y :fill "none" :r radius}]))))

(defn draw-line
  []
  (reset! current-shape (list [:line {:x1 @start-x :y1 @start-y :x2 @current-x :y2 @current-y}])))

(defn draw-rectangle
  []
  (let [width (Math/abs (- @current-x @start-x))
        height (Math/abs(- @current-y @start-y))
        begin-x (min @start-x @current-x)
        begin-y (min @start-y @current-y)]
    (reset! current-shape (list [:rect {:x begin-x :y begin-y :width width :height height :fill "none"}]))))

(defn start-drawing
  [x]
  (set-start-x! (-> x .-clientX))
  (set-start-y! (-> x .-clientY))
  (set-draw-state! :started))

(defn finish-drawing
  []
  (set-draw-state! :selected)
  (swap! shape-list conj @current-shape)
  (reset! current-shape '()))

;; Click handling
(defn shape-click
  [x]
  (set-draw-state! :selected)
  (set-selected-shape! (keyword (-> x .-target .-id))))

(defn undo-click
  []
  (if (= @draw-state :selected)
    (do
      (set-draw-state! :none)
      (set-selected-shape! :none))
    (when (seq @shape-list)
      (swap! shape-list pop @shape-list))))

(defn drawing-click
  [x]
  (cond
    (= @draw-state :selected) (start-drawing x)
    (= @draw-state :started) (finish-drawing)))

(defn mouse-moving
  [x]
  (set-current-x! (-> x .-clientX))
  (set-current-y! (-> x .-clientY))
  (when (= @draw-state :started)
    (cond
      (= @selected-shape :line)(draw-line)
      (= @selected-shape :circle)(draw-circle)
      (= @selected-shape :rectangle)(draw-rectangle))))

(defn shape-button
  [xs]
  (let [[text-x title id rect-x handler] xs]
    (list
     [:text {:x text-x :y 65} title]
     [:rect#blah {:id id :x rect-x :y 10 :width 180 :height 100 :fill-opacity "0.3" :rx "20" :ry "20" :stroke-width "3" :on-click handler}])))

(defn drawing-area
  []
  (list
   [:svg {:width 800 :height 800 :stroke "black"
          :style {:position :fixed :top 0 :left 0 :border "red solid 1px"}
          :on-click drawing-click
          :on-mouse-move mouse-moving}
    (list @shape-list)
    (list @current-shape)]))

(defn buttons
  []
  (list
   [:svg {:width 800 :height 115 :stroke "black" :style {:position :fixed :top 800 :left 0 :border "none"}}
    (shape-button [80 "LINE" :line 10 shape-click])
    (shape-button [270 "CIRCLE" :circle 210 shape-click])
    (shape-button [455 "RECTANGLE" :rectangle 410 shape-click])
    (shape-button [680 "UNDO" :undo 610 undo-click])]))

(defn main []
  [:div
   (drawing-area)
   (buttons)])

(r/render-component [main]
                    (. js/document (getElementById "app")))


(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)
