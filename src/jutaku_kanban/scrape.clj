(ns jutaku-kanban.scrape
  (:require [net.cgrand.enlive-html :as html]))

(defn- fetch-url
  "Returns an HTML resource at URL."
  [url]
  (html/html-resource (java.net.URL. url)))

(defn result-pages
  "Returns a seq of all result page-URLs from a search results page."
  [search-resource]
  (->> (html/select search-resource [:div#pageSelection :option])
       (map #(get-in % [:attrs :value]))
       (map #(str "https://www.immobilienscout24.de" %))))

(defn results
  "Get a seq of result items on a page."
  [search-resource]
  (-> (html/select search-resource [:ul#resultListItems])
      (first)
      (html/select [(html/attr= :data-item "result")])))

(defn result-id
  "Get the ID of a result item."
  [result-item]
  (get-in result-item [:attrs :data-obid]))

(defn result-title
  "Get the title of a result item."
  [result-item]
  (let [t (html/select result-item
                       [:h5.result-list-entry__brand-title html/text-node])
        [title-or-neu nil-or-title] t]
    (if (nil? nil-or-title) title-or-neu nil-or-title)))

(defn expose-url
  "Returns the URL of offer with ID's expose."
  [id]
  (str "https://www.immobilienscout24.de/expose/" id))

(defn all-results
  "Returns a map of all result IDs to their titles for a search URL."
  [url]
  (println "Getting initial result page.")
  (let [init (fetch-url url)]
    (->> (result-pages init)
         (into #{})
         (#(disj % url))
         ((fn [x] (println "Getting" (count x) "more result pages.") x))
         (map fetch-url)
         (#(conj % init))
         (map results)
         (reduce concat)
         (map (juxt result-id result-title))
         (into {}))))
