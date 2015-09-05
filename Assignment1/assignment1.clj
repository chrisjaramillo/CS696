; Question one
(defn bill-total [coll] (let [[x & rest] coll] (if (empty? rest) (* (x :amount) (x :quantity)) (+ (* (x :amount) (x :quantity)) (bill-total rest)))) )
; Question two
