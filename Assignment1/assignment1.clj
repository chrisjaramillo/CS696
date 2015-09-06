; Question one
(defn bill-total [coll] (let [[x & rest] coll] (if (empty? rest) (* (x :amount) (x :quantity)) (+ (* (x :amount) (x :quantity)) (bill-total rest)))) )
; Question two
(defn add-to-bill [bill items] (into bill items))
; Question three
(defn make-poly [coll] (let [[two one zero] coll] #(+ (* (first two)(Math/pow % (second two))) (* (first one)(Math/pow % (second one))) (* (first zero)(Math/pow % (second zero))))))
; Question four
(defn deriv [x] [(* (first x) (second x)) (- (second x) 1)])
(defn differentiate [x] (let [v (filter #(not= (second %) 0) x)](map deriv v)))
; Question five
; Question six
(defn withdraw [x y] (update-in x [:balance] - y))
(defn deposit [x y] (update-in x [:balance] + y))
