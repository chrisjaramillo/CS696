(ns assignment3.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [clojure.java.io :as cjio]))

(def command-message (atom ""))

(defmulti exec :command)

(defmethod exec "pen" [x]
  (reset! command-message (str "pen" (:value x))))

(defmethod exec "move" [x]
  (reset! command-message "move"))

(defmethod exec "turn" [x]
  (reset! command-message "turn"))

(defmulti me-undo :command)

(defmethod undo "pen" [x]
  (reset! command-message (str "undo pen" (:value x))))

(defmethod undo "move" [x]
  (reset! command-message (str "undo move" (:value x))))

(defmethod undo "turn" [x]
  (reset! command-message (str "undo turn" (:value x))))

(def todo-commands (atom []))

(def completed-commands (atom []))

(def command-keys [:key :value])

(defn vec->map [ks vs]
  (let [[key1 key2] ks
        [val1 val2] vs]
    {key1 val1 key2 val2}))

(defn get-lines [file-name]
  (with-open [r (cjio/reader file-name)]
    (doall (line-seq r))))

(defn file->coll [file-name]
  (map #(clojure.string/split % #" ") (get-lines file-name)))

(defn read-comand-file [file-name]
  (map (partial vec->map command-keys) (file->coll file-name)))

(defn init-command-list [file-name]
  (reset! todo-commands (into '() (read-comand-file file-name))))

(defn setup []
  (q/frame-rate 60)
  (init-command-list "/Users/christopherjaramillo/Documents/myTestTurtle.txt")
  (reset! command-message (str @todo-commands)))

(defn run-all
  []
  (reset! command-message "Run all"))

(defn back
  []
  (reset! command-message "Back"))

(defn forward
  []
  (let [command (peek @todo-commands)]
    ;(swap! todo-commands pop)
    ;(reset! completed-commands (conj completed-commands command))
    (exec command)))

(defn keyboard-action
  []
  (let [key (q/key-as-keyword)]
    (cond
     (= key :r)(run-all)
     (= key :R)(run-all)
     (= key :left)(back)
     (= key :right)(forward))))

(defn draw-state
  []
  (q/background 240)
  (q/fill 0)
  (q/text @command-message 10 10))

(q/defsketch assignment3
  :title "Assignment3: Turtle Graphics"
  :size [500 500]
  ; setup function called only once, during sketch initialization.
  :setup setup
  ; update-state is called on each iteration before draw-state.
  :draw draw-state
  :key-pressed keyboard-action
  :features [:keep-on-top])

#_((defn setup []
  ; Set frame rate to 30 frames per second.
  (q/frame-rate 30)
  ; Set color mode to HSB (HSV) instead of default RGB.
  (q/color-mode :hsb)
  ; setup function returns initial state. It contains
  ; circle color and position.
  {:color 0
   :angle 0})

(defn update-state [state]
  ; Update sketch state by changing circle color and position.
  {:color (mod (+ (:color state) 0.7) 255)
   :angle (+ (:angle state) 0.1)})

(defn draw-state [state]
  ; Clear the sketch by filling it with light-grey color.
  (q/background 240)
  ; Set circle color.
  (q/fill (:color state) 255 255)
  ; Calculate x and y coordinates of the circle.
  (let [angle (:angle state)
        x (* 150 (q/cos angle))
        y (* 150 (q/sin angle))]
    ; Move origin point to the center of the sketch.
    (q/with-translation [(/ (q/width) 2)
                         (/ (q/height) 2)]
      ; Draw the circle.
      (q/ellipse x y 100 100))))

(q/defsketch assignment3
  :title "You spin my circle right round"
  :size [500 500]
  ; setup function called only once, during sketch initialization.
  :setup setup
  ; update-state is called on each iteration before draw-state.
  :update update-state
  :draw draw-state
  :features [:keep-on-top]
  ; This sketch uses functional-mode middleware.
  ; Check quil wiki for more info about middlewares and particularly
  ; fun-mode.
  :middleware [m/fun-mode]))

