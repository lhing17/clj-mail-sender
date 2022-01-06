(ns clj-mail-sender.core
  (:import (java.util Properties Date)
           (jakarta.mail Session Message$RecipientType MessagingException)
           (jakarta.mail.internet MimeMessage InternetAddress MimeBodyPart MimeMultipart)
           (jakarta.activation DataHandler DataSource))
  (:require [clj-mail-sender.protocols :refer :all]))

(defn- get-auth [options]
  (if (options :auth? true) "true" "false"))

(defn- email-props [options]
  "The properties of email server."
  (doto (Properties.)
    (.setProperty "mail.transport.protocol" (options :protocol "smtp"))
    (.setProperty "mail.smtp.auth" (get-auth options))
    (.setProperty "mail.smtp.host" (options :host))))

(defn- get-session [options]
  (Session/getInstance (email-props options)))

(defn- build-text [text]
  "Build the texture part of an email."
  (doto (MimeBodyPart.)
    (.setContent text "text/html;charset=UTF-8")))


(defn- build-attachment [f]
  "Build an attachment part of an email from a file (path) or a URL."
  (let [dh (DataHandler. ^DataSource (create-data-source f))]
    (doto (MimeBodyPart.)
      (.setDataHandler dh)
      (.setFileName (.getName dh)))))


(defn- add-body-parts [^MimeMultipart mm parts]
  (doseq [part parts]
    (.addBodyPart mm part)))

(defn- build-multi-part [text files]
  "build a multipart as the content of the mime message."
  (doto (MimeMultipart.)
    (add-body-parts [(build-text text)])
    (add-body-parts (map build-attachment files))))

(defn- set-recipients-of-type [^MimeMessage msg key type options]
  (when (key options)
    (.addRecipients msg ^Message$RecipientType type ^"[Ljakarta.mail.Address;"
                    (into-array InternetAddress (map #(InternetAddress. %) (options key))))))

(defn- set-recipients [^MimeMessage msg options]
  (set-recipients-of-type msg :to Message$RecipientType/TO options)
  (set-recipients-of-type msg :cc Message$RecipientType/CC options)
  (set-recipients-of-type msg :bcc Message$RecipientType/BCC options))


(defn- build-mime-message [content session options]
  (doto (MimeMessage. ^Session session)
    (.setFrom (InternetAddress. (options :sender-address)))
    (set-recipients options)
    (.setSubject (options :title "") "UTF-8")
    (.setContent content)
    (.setSentDate (Date.))))


(defn send-mail [options]
  "Send an email according to the supplied options:

   :protocol        - The protocol for email transportation, defaults to smtp.
   :auth?           - Whether authorization is needed while sending email, defaults to true.
   :host            - Host of email server.
   :sender-address  - Email address of sender.
   :sender-password - Password of sender's email address.
   :to              - Email address list of receivers.
   :cc              - Email address list of carbon copy.
   :bcc             - Email address list of blind carbon copy.
   :title           - Title of the email.
   :text            - Text content of the email.
   :attachments     - Attachment file (path) or url list of the email.

  "
  (let [content (build-multi-part (options :text "") (options :attachments))
        session (get-session options)
        msg (build-mime-message content session options)
        transport (.getTransport session)]
    (try
      (doto transport
        (.connect (options :sender-address) (options :sender-password))
        (.sendMessage msg (.getAllRecipients msg)))
      (catch MessagingException e (str "caught exception: " (.getMessage e)))
      (finally (when-not (nil? transport) (.close transport))))
    )
  )
