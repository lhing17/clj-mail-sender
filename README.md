# clj-mail-sender

A simple Clojure library designed to send an email, based on jakarta mail 2.x.

## Usage

```clojure
(send-mail
  {:protocol        "smtp"
   :auth            "true"
   :host            "smtp.163.com"
   :sender-address  "XXX@163.com"
   :sender-password "YOURPASS"
   :to              ["XXX@163.com"]
   :cc              ["XXX@163.com"]
   :bcc             ["XXX@163.com"]
   :title           "Test title"
   :text            "Test text content"
   :attachments     ["/Users/XXX/Documents/a.md" "/Users/XXX/Documents/b.pdf"]
   })
```

## License

Copyright © 2022 FIXME

This program and the accompanying materials are made available under the terms of the Eclipse Public License 2.0 which
is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary Licenses when the conditions for such
availability set forth in the Eclipse Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your option) any later version, with the GNU
Classpath Exception which is available at https://www.gnu.org/software/classpath/license.html.
