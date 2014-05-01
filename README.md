# PickContact plugin for Cordova 3.x and PhoneGap

This plugin is based on the plugins [com.monday.contact-chooser](https://github.com/monday-consulting/ContactChooser) and [org.apache.cordova.contacts](https://github.com/apache/cordova-plugin-contacts)
It is fully inspired on the ContactChooser plugin, the trigger to get data is the same. The only thing different is the data that is returned. The plugin will return more data, for example phone numbers, emails and adresses (as in the cordova contacts plugin).

This plugin brings up a native iOS or Android contact-picker overlay, accessing the addressbook and returning the selected contact's name, email and phone number.

## Usage

Example Usage

```js
window.plugins.PickContact.chooseContact(function (contactInfo) {
    setTimeout(function () { // use timeout to fix iOS alert problem
        alert(contactInfo.displayName + " " + contactInfo.emailAddress + " " + contactInfo.phoneNr );
    }, 0);
});
```

The method which will return a JSON. Example:

```json
{
    displayName: "John Doe",
    emailAddress: "john.doe@mail.com",
    phoneNr: "+(55) 555-55-55"
}
```

## Installation Instructions

The PiCkContact plugin provides support for Cordova's command-line tooling.
Simply navigate to your project's root directory and execute the following command:

```
cordova plugin add https://github.com/kolwit/com.kolwit.pickcontact.git
```

## MIT Licence

Copyright 2014 Kolwit Ltd.


Permission is hereby granted, free of charge, to any person obtaining
a copy of this software and associated documentation files (the
"Software"), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to
the following conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
