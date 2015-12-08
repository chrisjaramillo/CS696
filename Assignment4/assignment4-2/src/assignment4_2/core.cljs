;; Christopher Jaramillo
;; CS 696
;; Spring 2015
;; Assignment 4 part 2

(ns assignment4-2.core
  (:require [reagent.core :as r]
            [schema.core :as s :include-macros true]))

(enable-console-print!)

(println "Edits to this text should show up in your developer console.")

;; define your app data so that it doesn't get over-written on reload

(def point-schema {:x s/Num :y s/Num})

(def palette-shapes (s/enum :none :circle :line :rect))

(def drawing-states (s/enum :none :selected :drawing))

(defonce app-state (r/atom {:selection :none :drawing-state :none :start-point {} :end-point {} :clicks [] :draw-fn ()}))

;;Cursors
(def selection (r/cursor app-state [:selection]))

(defn set-selected-shape!
  [x]
  {:pre [(s/validate palette-shapes x)]}
  (reset! selection x))

(def drawing-state (r/cursor app-state [:drawing-state]))

(defn set-draw-state!
  [x]
  {:pre [(s/validate drawing-states x)]}
  (reset! drawing-state x))

(def start-point (r/cursor app-state [:start-point]))

(defn set-start-point!
  [x]
  {:pre [(s/validate point-schema x)]}
  (reset! start-point x))

(def end-point (r/cursor app-state [:end-point]))

(defn set-end-point!
  [x]
  {:pre [(s/validate point-schema x)]}
  (reset! end-point x))

(def clicks (r/cursor app-state [:clicks]))

(defn add-click!
  [coll]
  (swap! clicks conj coll))

(defn remove-click!
  []
  (swap! clicks pop @clicks))

(def draw-fn (r/cursor app-state [:draw-fn]))

(defn set-draw-fn
  [x]
  (reset! draw-fn x))

;; Constants
(def drawing-area-x-offset 40)

(def drawing-area-y-offset 40)

(def drawing-area-width 600)

(def drawing-area-height 600)

(def palette-x-offset (+ drawing-area-x-offset drawing-area-width 20))

(def palette-y-offset (+ drawing-area-y-offset 20))

;; Helpers
(defn start-drawing
 [x]
  (let [start-x (-> x .-clientX)
        start-y (-> x .-clientY)
        start-point {:x start-x :y start-y}]
    (set-start-point! start-point)
    (set-draw-state! :drawing)
    (add-click! {:type :start-drawing :info start-point})))

(defn finish-drawing
 [x]
  (let [end-x (-> x .-clientX)
        end-y (-> x .-clientY)
        end-point {:x end-x :y end-y}]
  (add-click! {:type :finish-drawing :info end-point})
  (set-draw-state! :selected)))

;; Drawing Functions
(defn draw-circle
  []
  (let [deltaX (- (:x @end-point) (:x @start-point))
        deltaY (- (:y @end-point) (:y @start-point))
        deltaX2 (Math/pow deltaX 2)
        deltaY2 (Math/pow deltaY 2)
        radius (Math/sqrt (+ deltaX2 deltaY2))]
    (list [:circle {:cx (:x @start-point) :cy (:y @start-point) :fill "none" :r radius}])))

#_(defn draw-line
  []
  (reset! current-shape (list [:line {:x1 @start-x :y1 @start-y :x2 @current-x :y2 @current-y}])))

#_(defn draw-rectangle
  []
  (let [width (Math/abs (- @current-x @start-x))
        height (Math/abs(- @current-y @start-y))
        begin-x (min @start-x @current-x)
        begin-y (min @start-y @current-y)]
    (reset! current-shape (list [:rect {:x begin-x :y begin-y :width width :height height :fill "none"}]))))

;; Mouse Event handlers
(defmulti handle-click
  (fn [x]
    (-> x .-target .-id)))

(defmethod handle-click :default
  [x]
  (let [id (-> x .-target .-id)]
    (set-selected-shape! (keyword id))
    (add-click! {:type :shape-select :id id})
    (when-not (= @drawing-state :drawing)
      (set-draw-state! :selected)))
  (set-draw-fn draw-circle)
  (println @app-state))

(defmethod handle-click "drawing-area"
  [x]
  (cond
   (= @drawing-state :selected)(start-drawing x)
   (= @drawing-state :drawing)(finish-drawing x))
  (println @app-state))

(defmethod handle-click "undo"
  [x]
  (when (seq @clicks)
    (remove-click!))
  (println @app-state))

(defn handle-move
  [x]
  (let [end-x (-> x .-clientX)
        end-y (-> x .-clientY)
        end-point {:x end-x :y end-y}]
    (set-end-point! end-point))
  (println @app-state))

(defn drawn-shapes
  []
  ;(@draw-fn)
  )

;; Drawn areas
(defn drawing-area
  []
  (list
   [:svg {:id "drawing-area" :width drawing-area-width :height drawing-area-height
          :stroke "black" :style {:position :fixed
                                  :top drawing-area-y-offset :left drawing-area-x-offset
                                  :border "red solid 2px"}
                                  :on-click handle-click
                                  :on-mouse-move handle-move}
    (drawn-shapes)
    ;(@draw-fn)
    ]))

(defn palette
  []
  (let [box-top (+ drawing-area-y-offset 100)
        box-left (+ drawing-area-x-offset drawing-area-width 80)
        box-width 100
        box-height (- drawing-area-height 160)]
    [:svg {:id "palette-svg" :width box-width :height box-height :stroke "black" :style {:position :fixed :top box-top :left box-left :border "none"}}
     [:line {:id "palette-line" :x1 5 :y1 5 :x2 (- box-width 5) :y2 (- (/ box-height 3) 5)}]
     [:rect {:id "line" :x 0 :y 0 :width box-width :height (/ box-height 3) :fill-opacity "0.0" :stroke-width "3px" :on-click handle-click}]
     [:circle {:id "palette-circle" :cx (/ box-width 2) :cy (+ (/ box-height 3) (/ (/ box-height 3) 2)) :r (- (/ box-width 2) 5) :fill "none"}]
     [:rect {:id "circle" :x 0 :y (/ box-height 3) :width box-width :height (/ box-height 3) :fill-opacity "0.0" :stroke-width "3px" :on-click handle-click}]
     [:rect {:id "palette-rect" :x 10 :y (+ (* (/ box-height 3) 2) 10) :width (- box-width 20) :height (- (/ box-height 3) 20) :fill-opacity "0.0"}]
     [:rect {:id "rect" :x 0 :y (* (/ box-height 3) 2) :width box-width :height (/ box-height 3) :fill-opacity "0.0" :stroke-width "3px" :on-click handle-click}]
     ]
    )
  )

(defn undo-button
  []
  (let [box-top (+ drawing-area-y-offset drawing-area-height)
        box-width drawing-area-width
        box-height 60
        text-x (- (/ drawing-area-width 2) 20)
        rect-x (- (/ drawing-area-width 2) 85)]
    (list
      [:svg {:id "undo-svg" :width box-width :height box-height :stroke "black" :style {:position :fixed :top box-top :left drawing-area-x-offset :border "none"}}
       [:text {:id "undo-text" :x text-x :y 35} "UNDO"]
       [:rect {:id "undo" :x rect-x :y 10 :width 180 :height 40 :fill-opacity "0.3" :rx "20" :ry "20" :stroke-width "3"
               :on-click handle-click}]])))

(defn main []
  [:div {:id "main-div"}
   (drawing-area)
   (palette)
   (undo-button)])

(r/render-component [main]
                          (. js/document (getElementById "app")))


(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)
