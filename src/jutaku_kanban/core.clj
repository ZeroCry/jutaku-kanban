(ns jutaku-kanban.core
  (:gen-class)
  (:require [clj-http.client :as client]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.set :as set]
            [clojure.string :as string]
            [jutaku-kanban.scrape :refer [all-results expose-url]]))

(defonce api-root "https://api.trello.com/1")

(defn- api-path [path]
  (string/join "/" (cons api-root path)))

(defn- api-call [method path msg & [opts rest]]
  (let [o (if (some? opts) opts {})
        r (method (api-path path)
                  (assoc o :throw-exceptions false :as :json))]
    (when-not
      (contains? #{200 201 202 203 204 205 206 207 300 301 302 303 307}
                 (:status r))
      (println "HTTP error" msg (:status r))
      (System/exit 1))
    (:body r)))

(defn- check-api
  [api-key api-token list-id]
  (let [r (api-call client/get ["tokens" api-token] "checking key/token"
                    {:query-params {"key" api-key}})
        by-type (reduce #(assoc %1 (:modelType %2) %2) {} (:permissions r))
        board (get by-type "Board")]
    (when-not (and (some? board) (:write board))
      (println "API token does not have board write permissions.")
      (System/exit 1)))
  (api-call client/get ["lists" list-id] "getting list"
            {:query-params {"key" api-key "token" api-token}}))


(defn- create-card [api-key api-token list-id name desc]
  (api-call
   client/post
   ["cards"]
   "creating card"
   {:form-params {"key" api-key "token" api-token "idList" list-id
                  "name" name "desc" desc}}))

(defn -main [& args]
  (let [[api-key api-token list-id file url & rest] args
        seen-ids (if (and (some? file) (.exists (io/file file)))
                     (edn/read-string (slurp file))
                     #{})]

    (when (nil? url)
      (println "No URL specified.")
      (System/exit 1))
    (when-not (set? seen-ids)
      (println "Seen edn is not a set.")
      (System/exit 1))

    (check-api api-key api-token list-id)

    (let [found-items (all-results url)
          found-ids (set (keys found-items))
          new-ids (set/difference found-ids seen-ids)]
      (println (count (set/difference seen-ids found-ids)) "entries gone.")
      (spit file (prn-str found-ids))
      (println "Creating" (count new-ids) "new cards.")
      (doseq [id new-ids]
        (create-card api-key api-token list-id (get found-items id)
                     (expose-url id))))))
