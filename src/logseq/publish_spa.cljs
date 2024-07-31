(ns logseq.publish-spa
  "Exports SPA publishing app"
  (:require ["fs" :as fs]
            ["path" :as node-path]
            [babashka.cli :as cli]
            [clojure.edn :as edn]
            [clojure.string]
            [logseq.graph-parser.cli :as gp-cli]
            [logseq.publishing :as publishing]))

(def ^:private valid-colors
  "From frontend.colors/color-list"
  [:tomato :red :crimson :pink :plum :purple :violet :indigo :blue :cyan :teal :green :grass :orange])

(defn- get-db [graph-dir]
  (let [{:keys [conn]} (gp-cli/parse-graph graph-dir {:verbose false})] @conn))

(def ^:private spec
  "Options spec"
  {:accent-color {:desc
                  (str  "Accent color for frontend. Can be one of " (clojure.string/join  ", " (map name valid-colors)) ". Defaults to \"blue\".")
                  :default "blue"}
   :theme-mode {:desc "Theme mode for frontend. Can be \"dark\" or \"light\". Defaults to \"light\"."
                :default "light"}
   :directory {:desc "Graph directory to export"
               :alias :d
               :default "."}
   :help {:alias :h
          :desc "Print help"}
   :static-directory {:desc "Logseq's static directory"
                      :alias :s
                      :default "../logseq/static"}})

(defn- validate-directories [graph-dir static-dir]
  (when-not (fs/existsSync (node-path/join graph-dir "logseq" "config.edn"))
    (println (str "Error: Invalid graph directory '" graph-dir
                  "' as it has no logseq/config.edn."))
    (js/process.exit 1))
  (when-not (fs/existsSync static-dir)
    (println (str "Error: Logseq static directory '" static-dir
                  "' does not exist. Please provide a valid directory"))
    (js/process.exit 1)))

(defn- get-theme-mode [user-theme-mode]
  (let [theme-mode (or user-theme-mode "light")]
    (if (#{"light" "dark"} theme-mode)
      theme-mode
      (do
        (println "Warning: Skipping :theme-mode since it is invalid. Must be 'light' or 'dark'.")
        "light"))))

(defn- get-accent-color [color*]
  (let [color (keyword (or color* "blue"))]
    (if (contains? (set valid-colors) color)
      color
      (do
        (println "Warning: Skipping :accent-color since it is invalid.")
        :blue))))

(defn- build-common-export-options
  [options]
  {:ui/theme (get-theme-mode (:theme-mode options))
   :ui/radix-color (get-accent-color (:accent-color options))
   :notification-fn (fn [msg]
                      (if (= "error" (:type msg))
                        (do (js/console.error (:payload msg))
                            (js/process.exit 1))
                        (js/console.log (:payload msg))))})

(defn ^:api -main
  [& args]
  (let [options (cli/parse-opts args {:spec spec})
        _ (when (or (:help options) (= 0 (count args)))
            (println (str "Usage: logseq-publish-spa OUT-DIR [OPTIONS]\nOptions:\n"
                          (cli/format-opts {:spec spec})))
            (js/process.exit 1))
        _ (when js/process.env.CI (println "Options:" (pr-str options)))
        [static-dir graph-dir output-path]
        ;; Offset relative paths for CI since it is run in a different dir
        (map #(if js/process.env.CI (node-path/resolve ".." %) %)
             [(:static-directory options) (:directory options) (first args)])
        _ (validate-directories graph-dir static-dir)
        repo-config (-> (node-path/join graph-dir "logseq" "config.edn") fs/readFileSync str edn/read-string)]
    (publishing/export (get-db graph-dir)
                       static-dir
                       graph-dir
                       output-path
                       (merge (build-common-export-options options) {:repo-config repo-config
                                                                     :html-options (:html-options repo-config)}))))

#js {:main -main}
