(ns chengfen.utils)

(defn hex-to-rgb
  [hex]
  (let [r (Integer/parseInt (subs hex 1 3) 16)
        g (Integer/parseInt (subs hex 3 5) 16)
        b (Integer/parseInt (subs hex 5 7) 16)]
    [r g b]))
