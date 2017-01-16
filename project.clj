(defproject jutaku-kanban "1.0.0"
  :description "Get offers from immobilienscout24.de directly to your Trello board."
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[cheshire "5.6.3"]
                 [clj-http "2.3.0"]
                 [enlive "1.1.6"]
                 [org.clojure/clojure "1.8.0"]]
  :main ^:skip-aot jutaku-kanban.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
