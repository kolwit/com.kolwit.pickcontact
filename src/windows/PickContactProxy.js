cordova.define("com.kolwit.pickcontact.PickContactProxy", function (require, exports, module) {

var cordova = require('cordova');

var Contact = function (displayName, nameFormated, phoneNr, emailAddress, address, contactId) {
    this.firstName = null;
    this.lastName = null;
    this.displayName = displayName || null;
    this.nameFormated = nameFormated || null;
    this.nameMiddle = null;
    this.phoneNr = phoneNr || null;
    this.emailAddress = emailAddress || null;
    this.address = address || null;
    this.street = null;
    this.city = null;
    this.region = null;
    this.zipcode = null;
    this.country = null;
    this.contactId = contactId || null;
    this.extra = [];
};

var ContactError = function (err) {
    this.code = (typeof err != 'undefined' ? err : null);
};

ContactError.UNKNOWN_ERROR = 0;
ContactError.INVALID_ARGUMENT_ERROR = 1;
ContactError.TIMEOUT_ERROR = 2;
ContactError.PENDING_OPERATION_ERROR = 3;
ContactError.IO_ERROR = 4;
ContactError.NOT_SUPPORTED_ERROR = 5;
ContactError.PERMISSION_DENIED_ERROR = 20;

function convertToContact(windowsContact) {
    var contact = new Contact();

    contact.displayName = windowsContact.displayName || windowsContact.name;
    contact.nameFormated = windowsContact.name;
    contact.contactId = windowsContact.id;

    contact.firstName = windowsContact.firstName;
    contact.lastName = windowsContact.lastName;
    contact.nameMiddle = windowsContact.middleName;

    if (windowsContact.emails.size > 0) {
        contact.emailAddress = windowsContact.emails[0].value || windowsContact.emails[0].address;
    }

    if (windowsContact.phones.size > 0) {
        contact.phoneNr = windowsContact.phones[0].value || windowsContact.phones[0].number;
    }


    var addressSource = windowsContact.locations || windowsContact.addresses;
    if (addressSource.size > 0) {
        contact.address = addressSource[0].unstructuredAddress;
        contact.street = addressSource[0].street || addressSource[0].streetAddress;
        contact.city = addressSource[0].city || addressSource[0].locality;
        contact.region = addressSource[0].region;
        contact.zipcode = addressSource[0].postalCode;
        contact.country = addressSource[0].country;
    }

    return contact;
}

var contactsNS = Windows.ApplicationModel.Contacts;

module.exports = {

    chooseContact: function (win, fail, args) {
        //var runningOnPhone = navigator.userAgent.indexOf('Windows Phone') !== -1;

        var picker = new contactsNS.ContactPicker();
        var pickRequest = picker.pickContactAsync ? picker.pickContactAsync() : picker.pickSingleContactAsync();
        pickRequest.done(function (contact) {
            if (!contact) {
                fail && fail(new Error("User did not pick a contact."));
                return;
            }
            contactsNS.ContactManager.requestStoreAsync().done(function (contactStore) {
                contactStore.getContactAsync(contact.id).done(function (con) {
                    win(convertToContact(con));
                }, function () {
                    fail(new ContactError(ContactError.UNKNOWN_ERROR));
                });
            }, function () {
                fail(new ContactError(ContactError.UNKNOWN_ERROR));
            });
        });
    }
};

require("cordova/exec/proxy").add("PickContact", module.exports);
});