(ns assignment3.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [clojure.java.io :as cjio]))

(def command-message (atom ""))

(def pen-state (atom "down"))

(defmulti exec :command)

(def todo-commands (atom []))

(def completed-commands (atom []))

(def command-keys [:command :value])

(defmethod exec "pen" [x]
  (reset! command-message (str "pen " (:value x)))
  (reset! pen-state (:value x)))

(defmethod exec "move" [x]
  (reset! command-message (str "move " (:value x)))
  (when (= @pen-state "down")
    (q/line 0 0 (read-string (:value x)) 0))
  (q/translate (read-string (:value x)) 0))

(defmethod exec "turn" [x]
  (let [val (read-string (:value x))]
    (reset! command-message (str "turn " val))
    (q/rotate (q/radians val))))

(defn vec->map [ks vs]
  (let [[key1 key2] ks
        [val1 val2] vs]
    {key1 val1 key2 val2}))

(defn get-lines [file-name]
  (with-open [r (cjio/reader (cjio/resource file-name))]
    (doall (line-seq r))))

(defn file->coll [file-name]
  (map #(clojure.string/split % #" ") (get-lines file-name)))

(defn read-comand-file [file-name]
  (map (partial vec->map command-keys) (file->coll file-name)))

(defn init-command-list [file-name]
  (reset! todo-commands (reverse (into '() (read-comand-file file-name)))))

(defn run-completed
  []
  (q/background 240)
  (reset! pen-state "down")
  (doseq [command @completed-commands]
    (exec command)))

(defn run-all
  []
  (reset! completed-commands (into [] (concat @completed-commands @todo-commands)))
  (reset! todo-commands '())
  (run-completed))

(defn back
  []
  (when (seq @completed-commands)
    (let [command (peek @completed-commands)]
      (swap! completed-commands pop)
      (reset! todo-commands (conj @todo-commands command)))))

(defn forward
  []
  (when (seq @todo-commands)
    (let [command (peek @todo-commands)]
      (swap! todo-commands pop)
      (reset! completed-commands (conj @completed-commands command)))))

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
  (q/translate
    (/ (q/width) 2)
    (/ (q/height) 2))
  (run-completed)
  (q/reset-matrix)
  (q/text @command-message 10 10))

(defn setup []
  (q/frame-rate 60)
  (init-command-list "turtleTester.txt"))

(q/defsketch assignment3
  :title "Assignment3: Turtle Graphics"
  :size [500 500]
  ; setup function called only once, during sketch initialization.
  :setup setup
  ; update-state is called on each iteration before draw-state.
  :draw draw-state
  :key-pressed keyboard-action
  :features [:keep-on-top])

