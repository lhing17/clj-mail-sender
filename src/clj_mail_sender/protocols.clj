(ns clj-mail-sender.protocols
  (:import (java.io File)
           (jakarta.activation FileDataSource URLDataSource DataSource)
           (java.net URL)))

(defprotocol DataSourceFactory
  (^DataSource create-data-source [src]))

(extend-protocol DataSourceFactory
  File
  (create-data-source [src]
    (FileDataSource. src))

  URL
  (create-data-source [src]
    (URLDataSource. src))

  String
  (create-data-source [src]
    (FileDataSource. src))
  )
