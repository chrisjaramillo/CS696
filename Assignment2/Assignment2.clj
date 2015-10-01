; Question 1
(defn lowfactors [num]
  (filter #(= (mod num %) 0) (range 1 (+(int(Math/sqrt num)) 1))))

(defn highfactors [lowfactors num]
  (map #(/ num %) lowfactors))

(defn divisors [x]
  (let[low (lowfactors x) high (highfactors low x)]
    (distinct (sort (into high low)))))

;Question 2
(defn abundance [x]
  (let [factors (divisors x) sum (reduce + factors)]
    (- sum (* x 2))))

; Question 3
(defn abundant [x]
  (let [potential (range 1 (+ x 1))]
    (filter #(> (abundance %) 0) potential)))


; Question 4
(defn all-substrings-of-length [x length]
  (let [result '()]
    (when (>= (count x) length)
    (into result
          (into (list (subs x 0 length)) (all-substrings-of-length (subs x 1) length))))))

(defn pattern-count [text pattern]
  (let [substrings (all-substrings-of-length text (count pattern))
        matches (filter #(= pattern %) substrings)]
    (count matches)))

; Question 5
(defn most-frequent-word [text length]
  (let [substrings (all-substrings-of-length text length)
        substring-counts (frequencies substrings)
        inverted (clojure.set/map-invert substring-counts)
        count-keys (keys inverted)
        key-max (apply max count-keys)]
    (filter (comp #{key-max} substring-counts) (keys substring-counts))))

;Question 6
(defn filter-map-by-val [val coll]
  (filter #(= (second %) val) coll))

(defn find-clumps [text k l t]
  (let [l-substrings (all-substrings-of-length text l)
        k-substrings (map #(all-substrings-of-length % k) l-substrings)
        k-frequencies (map frequencies k-substrings)]
    (remove nil? (distinct (flatten(map #(keys %)(map #(filter-map-by-val t %) k-frequencies)))))))

;Question 7
(require '[clojure.string :as str])

(defn maximum-spread [path]
  (let [file (slurp path)
        file-lines (flatten(list (str/split file #"\n")))
        map-lines (map #(str/split % #"\t") file-lines)
        map-map (map #(into {} (read-string (first %)) (read-string (second %))) map-lines)]
    map-map))

(maximum-spread "http://www.eli.sdsu.edu/courses/fall15/cs696/assignments/weather.dat")
