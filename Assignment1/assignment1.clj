; Question One
(defn bill-total [coll]
  (let [[item & rest] coll]
    (if (empty? rest)
      (* (item :amount) (item :quantity))
      (+ (* (item :amount) (item :quantity)) (bill-total rest)))))

; Question Two
(defn update-bill [bill item index]
  (assoc bill index
    (assoc-in (nth bill index) [:quantity]
              (+ (item :quantity) ((nth bill index ) :quantity)))))
(defn map-index-in-vec [xs newmap currindex]
  (if (>= currindex (count xs))
    nil
    (if (= ((nth xs currindex) :name) (newmap :name))
      currindex
      (map-index-in-vec xs newmap (+ currindex 1)))))
(defn bill-add [item bill]
  (let [index (map-index-in-vec bill item 0)]
    (if (nil? index)
      (assoc bill (count bill) item)
      (update-bill bill item index))))
(defn add-to-bill [bill items]
  (let [[item & rest] items]
  (if (empty? rest)
    (bill-add item bill)
    (add-to-bill (bill-add item bill) rest))))

; Question Three
(defn poly-term [coll]
  (let [[coef power]coll]
    #(* coef (Math/pow % power))))
(defn poly-convert [coll]
  (map poly-term coll))
(defn make-poly [coll]
  (let [poly-terms (poly-convert coll)]
    #(reduce + (for [poly poly-terms] (poly %)))))

; Question Four
(defn deriv [x]
  (let [[a n] x][(* a n) (- n 1)]))
(defn differentiate [x]
  (let [v (filter #(not= (second %) 0) x)]
    (map deriv v)))

; Question Five
(defn calcx [eqn eqn' x]
  (- x (/ (eqn x) (eqn' x))))
(defn find-root [tolerance eqn guess]
  (let [px (make-poly eqn) px' (make-poly (differentiate eqn))]
  (let [x1 (calcx px px' guess)]
  (if (<= (Math/abs (- guess x1)) tolerance)
      (format "%.6f" guess)
    (find-root tolerance eqn x1)))))

; Question Six
(defn withdraw [account amount]
  (update-in account [:balance] - amount))
(defn deposit [account amount]
  (update-in account [:balance] + amount))
