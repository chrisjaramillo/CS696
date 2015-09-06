; Question one
(defn bill-total [coll] (let [[x & rest] coll] (if (empty? rest) (* (x :amount) (x :quantity)) (+ (* (x :amount) (x :quantity)) (bill-total rest)))) )
; Question two
(defn add-to-bill [bill items] (into bill items))
; Question three
(defn make-poly [coll] (let [[two one zero] coll] #(+ (* (first two)(Math/pow % (second two))) (* (first one)(Math/pow % (second one))) (* (first zero)(Math/pow % (second zero))))))
; Question four
