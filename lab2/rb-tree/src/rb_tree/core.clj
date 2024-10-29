(ns rb-tree.core)

(defrecord Node [color value left right])

(defn make-leaf []
  (->Node :black nil nil nil))

(defn make-node [color value left right]
  (->Node color value left right))

(defn leaf? [node]
  (or (nil? node)
      (and (nil? (:value node)) (nil? (:left node)) (nil? (:right node)))))

(defn balance [color value left right]
  (cond
    ; left-left red violation
    (and (= (:color left) :red)
         (= (:color (:left left)) :red))
    (make-node :red (:value left)
               (make-node :black (:value (:left left)) (:left (:left left)) (:right (:left left)))
               (make-node :black value (:right left) right))

    ; left-right red violation
    (and (= (:color left) :red)
         (= (:color (:right left)) :red))
    (make-node :red (:value (:right left))
               (make-node :black (:value left) (:left left) (:left (:right left)))
               (make-node :black value (:right (:right left)) right))

    ; right-right red violation
    (and (= (:color right) :red)
         (= (:color (:right right)) :red))
    (make-node :red (:value right)
               (make-node :black value left (:left right))
               (make-node :black (:value (:right right)) (:right (:right right)) (:right right)))

    ; right-left red violation
    (and (= (:color right) :red)
         (= (:color (:left right)) :red))
    (make-node :red (:value (:left right))
               (make-node :black value left (:left (:left right)))
               (make-node :black (:value right) (:right (:left right)) (:right right)))

    ; No violations, return the original node
    :else
    (make-node color value left right)))

(defn insert [tree value]
  (letfn [(ins [node]
            (if (leaf? node)
              (make-node :red value (make-leaf) (make-leaf))
              (let [node-value (:value node)]
                (cond
                  (< (compare value node-value) 0) (balance (:color node) node-value (ins (:left node)) (:right node))
                  (> (compare value node-value) 0) (balance (:color node) node-value (:left node) (ins (:right node)))
                  :else node))))]
    (let [new-tree (ins tree)]
      (make-node :black (:value new-tree) (:left new-tree) (:right new-tree))))) ; ensure root is black

(defn find-max [node]
  (if (leaf? (:right node))
    node
    (recur (:right node))))

(defn del-max [node]
  (if (leaf? (:right node))
    (:left node)
    (balance (:color node) (:value node) (:left node) (del-max (:right node)))))

(defn tree-values [tree]
  (if (leaf? tree)
    []
    (concat (tree-values (:left tree)) [(:value tree)] (tree-values (:right tree)))))

(defn delete [tree value]
  (letfn [(del [node]
            (if (leaf? node)
              node
              (let [node-value (:value node)]
                (cond
                  (< (compare value node-value) 0) (balance (:color node) node-value (del (:left node)) (:right node))
                  (> (compare value node-value) 0) (balance (:color node) node-value (:left node) (del (:right node)))
                  :else (if (leaf? (:left node))
                          (:right node)
                          (let [max-left (find-max (:left node))]
                            (balance (:color node) (:value max-left) (del-max (:left node)) (:right node))))))))]
    (let [new-tree (del tree)]
      (if (leaf? new-tree)
        new-tree
        (make-node :black (:value new-tree) (:left new-tree) (:right new-tree))))))

(defn union [tree1 tree2]
  (reduce insert tree1 (tree-values tree2)))

(defn filter-tree [tree pred]
  (if (leaf? tree)
    tree
    (let [left (filter-tree (:left tree) pred)
          right (filter-tree (:right tree) pred)]
      (if (pred (:value tree))
        (make-node (:color tree) (:value tree) left right)
        (union left right)))))

(defn foldl [f acc tree]
  (if (leaf? tree)
    acc
    (foldl f (f (foldl f acc (:left tree)) (:value tree)) (:right tree))))

(defn foldr [f acc tree]
  (if (leaf? tree)
    acc
    (f (:value tree) (foldr f (foldr f acc (:right tree)) (:left tree)))))

(defn black-root? [tree]
  (= :black (:color tree)))

(defn red-node-has-black-children? [node]
  (if (leaf? node)
    true
    (let [left (:left node)
          right (:right node)]
      (and (or (not= :red (:color node))
               (and (= :black (:color left))
                    (= :black (:color right))))
           (red-node-has-black-children? left)
           (red-node-has-black-children? right)))))

(defn same-black-height? [node]
  (letfn [(black-height [n]
            (if (leaf? n)
              1
              (let [left-height (black-height (:left n))
                    right-height (black-height (:right n))]
                (if (or (nil? left-height) (nil? right-height) (not= left-height right-height))
                  nil
                  (if (= :black (:color n))
                    (inc left-height)
                    left-height)))))]
    (not (nil? (black-height node)))))

(defn valid-red-black-tree? [tree]
  (and (black-root? tree)
       (red-node-has-black-children? tree)
       (same-black-height? tree)))
