; Question 1
(defn lowfactors [num]
  (filter #(= (mod num %) 0) (range 1 (+(int(Math/sqrt num)) 1))))

(defn highfactors [lowfactors num]
  (map #(/ num %) lowfactors))

(defn divisors [x]
  (let[low (lowfactors x) high (highfactors low x)]
    (sort (into high low))))

;Question 2
(defn abundance [x]
  (let [factors (divisors x) sum (reduce + factors)]
    (- sum (* x 2))))

; Question 3
(defn abundant [x]
  (let [potential (range 1 (+ x 1))]
    (filter #(> (abundance %) 0) potential)))

