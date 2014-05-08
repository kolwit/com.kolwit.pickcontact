# PickContact plugin for Cordova 3.x and PhoneGap

This plugin is based on the plugins [com.monday.contact-chooser](https://github.com/monday-consulting/ContactChooser) and [org.apache.cordova.contacts](https://github.com/apache/cordova-plugin-contacts)
It is fully inspired on the ContactChooser plugin, the trigger to get data is the same. The only thing different is the data that is returned. The plugin will return more data, for example phone numbers, emails and adresses (as in the cordova contacts plugin).

This plugin brings up a native iOS or Android contact-picker overlay, accessing the addressbook and returning the selected contact's name, email and phone number.

## iOS limited result
At this moment the plugin provides a limited result for iOS. If you have suggestions to extend the iOS part as the Android is; you are very welcome to contribute.


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
	firstName: "John",
	lastName: "Doe",
    displayName: "John Doe",
	nameFormated: "John Phil Doe",
	nameMiddle: "Phil",
	phoneNr: "+55 55 555 55",
    emailAddress: "john.doe@mail.com",
	address: "Rue 123, Brussels 1000, Belgium",
	street: "Rue 123",
	city: "Brussels",
	region: "Brussels",
	zipcode: "1000",
	country: "BE",
	contactId: "123",
}
```

## Installation Instructions Cordova

The PickContact plugin provides support for Cordova's command-line tooling.
Simply navigate to your project's root directory and execute the following command:

```
cordova plugin add https://github.com/kolwit/com.kolwit.pickcontact.git
```

## Installation Instructions PhoneGap

The PickContact plugin provides support for PhoneGap 3.0 CLI.
Simply navigate to your project's root directory and execute the following command:

```
phonegap local plugin add https://github.com/kolwit/com.kolwit.pickcontact.git
```

## Installation Instructions PhoneGap Build

The PickContact plugin provides support for PhoneGap Build.
Add the following to your config.xml to use version 1.0.1 of this plugin (recommended):

```
<gap:plugin name="com.kolwit.pickcontact" version="1.0.1" />
```

When you would like to automatically update when there is a new version of the plugin available, you can use the same command without the version code in it.

```
<gap:plugin name="com.kolwit.pickcontact" />
```
