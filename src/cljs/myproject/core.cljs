(ns myproject.core
    (:require [reagent.core :as r :refer [atom]]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]))

;;defines Reagent's own version of atom that
;;rerenders automatically every time it's deref'ed :D
(def user-phone-nmbr-test-atom (r/atom "0735 - 00 78 09"))
(def session-user-bank-name (r/atom "din bank"))
;; -------------------------
;; Views
;; A general input component

;;A TIMER COMPONENT COULD BE USED ON THE COUNTDOWN page
;;SE HERE http://reagent-project.github.io/

; (defn timer-component []
;   (let [seconds-elapsed (r/atom 0)]
;     (fn []
;       (js/setTimeout #(swap! seconds-elapsed inc) 1000)
;       [:div
;        "Seconds Elapsed: " @seconds-elapsed])))


(defn input-element
  "An input element which updates its value on change"
  [id name type value in-focus]
  [:input {:id id
           :name name
           :class "form-control"
           :type type
           :required ""
           :value @value
           :on-change #(reset! value (-> % .-target .-value))
           ;;change state when in forcus
           :on-focus #(swap! in-focus not)
           :on-blur #(swap! in-focus not)}])

(defn check-nil-then-apply
  "Check if the value is nil, then apply"
  [value apply]
  (if (nil? value)
    false
    (apply value)))

(defn eight-or-more-characters?
  [word]
  (check-nil-then-apply word (fn [arg] (> (count arg) 7))))

(defn has-special-character?
  [word]
  (check-nil-then-apply word (fn [arg] (boolean (first (re-seq #"\w+" arg))))))

(defn has-number?
  [word]
  (check-nil-then-apply word (fn [arg] (boolean (re-seq #"\d+" arg)))))

(defn password-requirements
  "A list to describe which password requirements have been met so far"
  [password requirements]
  [:div
    ;; the requirements that aren’t passed, and then map those
    ;;requirements to create :li elements
    [:ul (->> requirements
              (filter (fn [req] (not ((:check-fn req) @password))))
              (doall)
              (map (fn [req] ^{:key req} [:li (:message req)])))]])

(defn wrap-as-element-in-form
  [element]
  [:div {:class "row input-group"} element])


;;a generic function
(defn prompt-message
  "A prompt that will animate to help the user with a given input"
  [message]
  [:div {:class "my-messages"}
    [:div {:class "prompt-message-animation"} [:p message]]])

;;specific function
(defn email-prompt
  []
  (prompt-message "What's ypur email address?"))



;; NEW
; (defn input-group
;   "Cretas an input field with a label"
;   [input-id input-name input-type input-value required? in-focus]
;   (let [input-focus (atom false)]
;     (fn []
;       [:div {:class "input-group"}]
;       [input-element input-id input-name input-type input-value in-focus])))
  ;;NEW  id name type value in-focus



(defn input-and-prompt
  "Creates an input box and a prompt box that appears above the input
  when the input comes into focus"
  [label-value input-name input-type input-element-arg prompt-element required?]
  (let [input-focus (atom false)]
    (fn []
      [:div
        [:label label-value]
        ;;If the input-focus atom is set to true, we return the prompt-element,
        ;;otherwise, we return an empty div.
        (if @input-focus prompt-element [:div])
        [input-element input-name input-name input-type input-element-arg input-focus]
        ;;if required is true and it is true that the value of input args is ""
        (if (and required? (= "" @input-element-arg))
          [:div "Field is required"]
          [:div])])))

;;an email input component
(defn email-form
  [email-address-atom]
  (input-and-prompt "email"
                    "email"
                    "email"
                    email-address-atom
                    [prompt-message "What's your email?"]
                    true)) ;;sets 'required?' input to true

(defn name-form [name-atom]
  (input-and-prompt "name"
                    "name"
                    "text"
                    name-atom
                    (prompt-message "What's your name?")
                    true))

(defn password-form
  [password]
  (let [password-type-atom (atom "password")]
    (fn []
      [:div
        [(input-and-prompt "password"
                           "password"
                           @password-type-atom
                           password
                           (prompt-message "What's your password?")
                           true)]
        [password-requirements password [{:message "8 or more characters" :check-fn eight-or-more-characters?}
                                         {:message "At least one special character" :check-fn has-special-character?}
                                         {:message "At least one number" :check-fn has-number?}]]])))



; (defn home-page [] ;;home-page is just a function that returns  DOM elements
;   (let [email-address (atom nil)
;         name (atom nil)
;         password (atom nil)]
;     (fn[]
;       [:div {:class "mobile-container"}
;         [:div {:class "mobile-content"}]
;         [:div {:class "old-form-box"}[:h2 "Welcome to myproject"]
;           [:div "EMAIL ADDRESS IS" @email-address]
;           [:div [:a {:href "/about"} "go to about page"]
;             [:div {:class "signup-wrapper"}
;               [:h2 "Welcome to Lisas super page!!"]
;               [:form
;                 (wrap-as-element-in-form [email-form email-address])
;                 (wrap-as-element-in-form [name-form name])
;                 (wrap-as-element-in-form [password-form password])]]]]])))

;;above - we compose the email-input component into a form div by just
;;placing a vector [email-input email-address] inside the vector describing the form.

;;This is used by Lisa

(defn printFunc
  []
  (println "Button clicked yeeeeey!"))

(defn print-this
  [txt-to-print]
  (println txt-to-print))


(defn button-component
  [href bt-text class func]
  [:a {:href href
       :class class
       :on-click func}
    bt-text
    [:div {:class "anim"}]])

(defn txt-link-component
  [href link-txt func]
  [:a {:href href
       :class "txt-link"
       :on-click func}
    link-txt
    [:div {:class "txt-link-icon"}]])


(defn input-template
    [id name type value]
    [:input {:id id
             :name name
             :type type
             :required true ;;this need to be here otherwise the inout will always be seen as valid (css input:valid)
             :value @value
             :on-change #(reset! value (-> % .-target .-value))}])

(defn input-drop-down-template
   [name value]
   [:option {:name name
             :class "form-control"
             :value @value
             :on-change #(reset! value (-> % .-target .-value))}])


(defn phone-nmbr-input
 [phone-nmbr-input-atom]
 (input-template "phone-nmbr" "phone-nmbr" "text" phone-nmbr-input-atom))

(defn sms-code-input
  [sms-code-atom show?]
  (if show?
    (input-template "sms-code" "sms-code" "text" sms-code-atom)
    [:div]))

(defn name-input
  [user-name-atom]
  (input-template "user-name" "user-name" "text" user-name-atom))

(defn st-address-input
  [st-address-atom]
  (input-template "street-address" "street-address" "text" st-address-atom))

(defn postal-code-input
  [postal-code-atom]
  (input-template "postal-code" "postal-code" "text" postal-code-atom))

(defn city-input
  [city-atom]
  (input-template "city" "city" "text" city-atom))

(defn clearing-nmbr-input
  [clearing-nmbr-atom]
  (input-template "clearing-nmbr" "clearing-nmbr" "clearing-nmbr" clearing-nmbr-atom))

(defn account-nmbr-input
  [account-nmbr-atom]
  (input-template "account-nmbr" "account-nmbr" "account-nmbr" account-nmbr-atom))

(defn set-session-user-bank-name
  [chosen-bank-name]
  (fn[]
    (print-this chosen-bank-name)
    (reset! session-user-bank-name chosen-bank-name)
    (print-this @session-user-bank-name)))

(defn show-chosen-bank-logo
  [chosen-bank-class]
  (let [chosen-bank-logo (str chosen-bank-class "-logo")]
    [:div {:class "chosen-bank-box"}
      [:div {:class chosen-bank-logo}
        (txt-link-component "/hej" "Change " (print-this "someone tried to change bank"))]]))

;;This is a comment that is added! git hub desktop please see this!?!?

(defn header-component
  [header-txt back-bt-url]
  [:div {:class "header"}
    (button-component back-bt-url "" "back-bt" printFunc)
    [:span {:class "header-txt"} header-txt]])



;
; (defn continue-button
;   [href-value]
;   [:input {:class "continue-bt"
;            :type "button"
;            :value "Hejhejhej"
;            :href @href-value
;            :on-click #(reset! href-value (-> % .-target .-href-value))}])

; (defn show-sms-code-input
;   [show-sms-code-input-atom]
;   (compare-and-set! show-sms-code-input-atom false true))







; (defn input-group
;   [input-field label-name input-atom-value]
;   [:div {:class "input-group"}
;     [name-input input-atom-value]])

(defn home-page [] ;;home-page is just a function that returns  DOM elements
  (let [user-name-atom (atom "John Doe")
        st-address-atom (atom "Stampgatan 3D")
        postal-code-atom (atom "411 09")
        city-atom (atom "Göteborg")
        country-atom (atom nil)]
    (fn[]
      [:div {:class "mobile-container"}
        [:div {:class "mobile-display"}
          [:div {:class "content"}
            (header-component "withdraw 500 SEK" "/")
            [:h2 "Is this your information?"]
            [:form
              [:div {:class "input-group"}
                [name-input user-name-atom]
                [:span {:class "bar"}]
                [:label "Name"]]

              [:div {:class "input-group"}
                [st-address-input st-address-atom]
                [:span {:class "bar"}]
                [:label "Street Address"]]

              [:div {:class "input-group small margin-right"}
                [postal-code-input postal-code-atom]
                [:span {:class "bar"}]
                [:label "Postal Code"]]

              [:div {:class "input-group small"}
                [city-input city-atom]
                [:span {:class "bar"}]
                [:label "City"]]

              [:div {:class "input-group"}
                [:select
                  [input-drop-down-template "Sweden" country-atom]]]
              (button-component "/sign-in" "Continue" "default-bt" printFunc)]]]])))



;;input-id input-name input-type input-value required? in-focus]

(defn test-page []
  (let [email-address (atom nil)
        name (atom nil)
        password (atom nil)]
    (fn[]
      [:div {:class "mobile-container"}
        [:div {:class "mobile-display"}
          [:div {:class "content"}
            [:h2 "Welcome to myproject"]
            [:div "EMAIL ADDRESS IS" @email-address
              [:div {:class "signup-wrapper"}
                [:h2 "Welcome to Lisas super page!!"]
                [:form
                  (wrap-as-element-in-form [email-form email-address])
                  (wrap-as-element-in-form [name-form name])
                  (wrap-as-element-in-form [password-form password])
                  [:div [:a {:href "/about"} "go to about page"]]]]]]]])))

(defn about-page []
  [:div {:class "mobile-container"}
    [:div {:class "mobile-display"}
      [:div {:class "content"}
        [:div [:h2 "About myproject"]
         [:div [:a {:href "/"} "go to the home page"]]]]]])

(defn sign-in-page []
  (let [sms-code-atom (atom nil)
        show-sms-code-input-atom (atom false)]
    (fn[]
     [:div {:class "mobile-container"}
       [:div {:class "mobile-display"}
         [:div {:class "content"}
           (header-component "withdraw 500 SEK" "/")
           [:h1 "Hi,"]
           [:h2 "Confirm your phone number to proceed"]
           [:form
             [:div {:class "input-group"}
               [phone-nmbr-input user-phone-nmbr-test-atom]
               [:span {:class "bar"}]
               [:label "Phone number"]
               (button-component "/verify-sms" "Send SMS code to verify" "default-bt" printFunc)]]]]])))



(defn verify-sms-page []
  (let [sms-code-atom (atom nil)
        show-sms-code-input-atom (atom true)]
    (fn[]
     [:div {:class "mobile-container"}
       [:div {:class "mobile-display"}
         [:div {:class "content"}
           (header-component "withdraw 500 SEK" "/sign-in")
           [:h1 "Hi,"]
           [:h2 "Confirm your phone number to proceed"]
           [:form
             [:div {:class "input-group"}
               [phone-nmbr-input user-phone-nmbr-test-atom]
               [:span {:class "bar"}]
               [:label "Phone number"]]

             [:div {:class "input-group"}
               [sms-code-input sms-code-atom @show-sms-code-input-atom]
               [:span {:class "bar"}]
               [:label "SMS code"]
               (button-component "/choose-bank" "Continue" "default-bt" printFunc)]]]]])))

(defn choose-bank-page []
 (let [user-phone-nmbr (atom nil)
       sms-code-atom (atom nil)
       show-sms-code-input-atom (atom true)]
   (fn[]
    [:div {:class "mobile-container"}
      [:div {:class "mobile-display"}
        [:div {:class "content"}
          (header-component "withdraw 500 SEK" "verify-sms")
          [:h2 "What is your bank?"]
          [:div {:class "bank-options"}
            (button-component "/account-details" "Swedbank" "default-bt bank-bt swedbank-logo" (set-session-user-bank-name "swedbank"))
            (button-component "/account-details" "Nordea" "default-bt bank-bt nordea-logo" printFunc)
            (button-component "/account-details" "Handelsbanken" "default-bt bank-bt handelsbanken-logo" printFunc)
            (button-component "/account-details" "SEB" "default-bt bank-bt seb-logo" printFunc)
            (button-component "/account-details" "Länsförsäkringar" "default-bt bank-bt lansforsakringar-logo" printFunc)
            (button-component "/account-details" "Danskebank" "default-bt bank-bt danskebank-logo" printFunc)
            (button-component "/account-details" "Ica-banken" "default-bt bank-bt icabanken-logo" printFunc)
            (button-component "/account-details" "Skandiabanken" "default-bt bank-bt skandiabanken-logo" printFunc)
            (txt-link-component "/account-details" "My bank is not shown above " printFunc)
            [:div {:class "clear-box"}]]]]])))

(defn account-details-page []
  (let [clearing-nmbr (atom nil)
        account-nmbr (atom nil)
        show-sms-code-input-atom (atom true)]
    (fn[]
     [:div {:class "mobile-container"}
       [:div {:class "mobile-display"}
         [:div {:class "content"}
           (header-component "withdraw 500 SEK" "/sign-in")
           [:h2 "Where should we send your money?"]
           [:form
             (show-chosen-bank-logo @session-user-bank-name)
             [:div {:class "input-group"}
               [clearing-nmbr-input clearing-nmbr]
               [:span {:class "bar"}]
               [:label "Clearing number"]]

             [:div {:class "input-group"}
               [account-nmbr-input account-nmbr]
               [:span {:class "bar"}]
               [:label "Account number"]
               (button-component "/choose-bank" "Continue" "default-bt" printFunc)]]]]])))




;; -------------------------
;; Routes

(def page (atom #'home-page))

(defn current-page []
  [:div [@page]])

(secretary/defroute "/" []
  (reset! page #'home-page))

(secretary/defroute "/about" []
  (reset! page #'about-page))

(secretary/defroute "/sign-in" []
  (reset! page #'sign-in-page))

(secretary/defroute "/verify-sms" []
  (reset! page #'verify-sms-page))

(secretary/defroute "/choose-bank" []
  (reset! page #'choose-bank-page))

(secretary/defroute "/account-details" []
  (reset! page #'account-details-page))

(secretary/defroute "/test" []
  (reset! page #'test-page))


;; -------------------------
;; Initialize app

(defn mount-root []
  (r/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (accountant/configure-navigation!
    {:nav-handler
     (fn [path]
       (secretary/dispatch! path))
     :path-exists?
     (fn [path]
       (secretary/locate-route path))})
  (accountant/dispatch-current!)
  (mount-root))
