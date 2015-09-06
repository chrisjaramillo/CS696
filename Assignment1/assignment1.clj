; Question One
(defn bill-total [coll]
  (let [[x & rest] coll]
    (if (empty? rest)
      (* (x :amount) (x :quantity))
      (+ (* (x :amount) (x :quantity)) (bill-total rest)))))
; Question Two
(defn add-to-bill [bill items]
  (into bill items))
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
(defn deriv [x] [(* (first x) (second x)) (- (second x) 1)])
(defn differentiate [x]
  (let [v (filter #(not= (second %) 0) x)]
    (map deriv v)))
; Question Five

; Question Six
(defn withdraw [x y] (update-in x [:balance] - y))
(defn deposit [x y] (update-in x [:balance] + y))
