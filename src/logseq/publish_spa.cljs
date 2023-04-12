(ns logseq.publish-spa
  "Exports SPA publishing app"
  (:require [logseq.graph-parser.cli :as gp-cli]
            [logseq.publishing :as publishing]
            ["fs" :as fs]
            ["path" :as node-path]
            [clojure.edn :as edn]))

(defn- get-db [graph-dir]
  (let [{:keys [conn]} (gp-cli/parse-graph graph-dir {:verbose false})] @conn))

(defn ^:api -main
  [& args]
  (when-not (= 3 (count args))
    (println "Usage: logseq-publish-spa STATIC-DIR GRAPH-DIR OUT-DIR")
    (js/process.exit 1))
  (let [[static-dir graph-dir output-path]
        ;; Offset relative paths for CI since it is run in a different dir
        (map #(if js/process.env.CI (node-path/resolve ".." %) %) args)
        repo-config (-> (node-path/join graph-dir "logseq" "config.edn") fs/readFileSync str edn/read-string)]
    (publishing/export (get-db graph-dir)
                       static-dir
                       graph-dir
                       output-path
                       {:repo-config repo-config
                        :notification-fn (fn [msg]
                                           (if (= "error" (:type msg))
                                             (do (js/console.error (:payload msg))
                                               (js/process.exit 1))
                                             (js/console.log (:payload msg))))})))

#js {:main -main}
