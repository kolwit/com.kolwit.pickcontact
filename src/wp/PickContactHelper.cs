namespace Cordova.Extension.Commands
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Text;
    using Microsoft.Phone.UserData;
    using System.IO;
    using WPCordovaClassLib.Cordova.Commands;
    using WPCordovaClassLib.Cordova.JSON;
    using WPCordovaClassLib.Cordova;
    using WPCordovaClassLib.CordovaLib;

    internal static class PickContactHelper
    {
        public static string ToJson(this Contact contact)
        {
            var contactFieldsWithJsonVals = contact.PopulateContactDictionary();
            string[] desiredFields = contactFieldsWithJsonVals.Keys.ToArray();
            return FillResultWithFields(desiredFields, contactFieldsWithJsonVals);
        }

        private static string FillResultWithFields(string[] desiredFields, Dictionary<string, Func<string>> contactFieldsWithJsonVals)
        {
            var result = new StringBuilder();
            for (int i = 0; i < desiredFields.Count(); i++)
            {
                if (!contactFieldsWithJsonVals.ContainsKey(desiredFields[i]))
                {
                    continue;
                }

                result.Append(contactFieldsWithJsonVals[desiredFields[i]]());
                if (i != desiredFields.Count() - 1)
                {
                    result.Append(",");
                }
            }

            return "{" + result + "}";
        }

        private static Dictionary<string, Func<string>> PopulateContactDictionary(this Contact contact)
        {

            string phoneNr = string.Empty;
            string emailAddress = string.Empty;
            string formattedAddress = string.Empty;
            string street = string.Empty;
            string city = string.Empty;
            string region = string.Empty;
            string zipcode = string.Empty;
            string country = string.Empty;

            foreach (ContactPhoneNumber number in contact.PhoneNumbers)
            {
                phoneNr = number.PhoneNumber;
            }


            foreach (ContactEmailAddress mailaddress in contact.EmailAddresses)
            {
                emailAddress = mailaddress.EmailAddress;
            }

            foreach (ContactAddress postaladdress in contact.Addresses)
            {
                formattedAddress = EscapeJson(postaladdress.PhysicalAddress.AddressLine1 + " "
                    + postaladdress.PhysicalAddress.AddressLine2 + " "
                    + postaladdress.PhysicalAddress.City + " "
                    + postaladdress.PhysicalAddress.StateProvince + " "
                    + postaladdress.PhysicalAddress.CountryRegion + " "
                    + postaladdress.PhysicalAddress.PostalCode);
                street = postaladdress.PhysicalAddress.AddressLine1 + " " + postaladdress.PhysicalAddress.AddressLine2;
                city = postaladdress.PhysicalAddress.City;
                region = postaladdress.PhysicalAddress.StateProvince;
                zipcode = postaladdress.PhysicalAddress.PostalCode;
                country = postaladdress.PhysicalAddress.CountryRegion;
            }


            var contactFieldsJsonValsDictionary = new Dictionary<string, Func<string>>(StringComparer.InvariantCultureIgnoreCase)
                {
                    { "contactId", () => string.Format("\"contactId\":\"{0}\"", contact.GetHashCode()) },
                    { "firstName", () => string.Format("\"firstName\":\"{0}\"", EscapeJson(contact.CompleteName.FirstName)) },
                    { "lastName", () => string.Format("\"lastName\":\"{0}\"", EscapeJson(contact.CompleteName.LastName)) },
                    { "displayName", () => string.Format("\"displayName\":\"{0}\"", EscapeJson(contact.DisplayName)) },
                    { "nameFormated", () => string.Format("\"displayName\":\"{0}\"", EscapeJson(contact.CompleteName.FirstName + " " + contact.CompleteName.LastName)) },
                    { "nameMiddle", () => string.Format("\"nameMiddle\":\"{0}\"", EscapeJson(contact.CompleteName.MiddleName)) },
                    { "phoneNr", () => string.Format("\"phoneNr\":\"{0}\"", EscapeJson(phoneNr)) },
                    { "emailAddress", () => string.Format("\"emailAddress\":\"{0}\"", EscapeJson(emailAddress)) },
                    { "address", () => string.Format("\"address\":\"{0}\"", EscapeJson(formattedAddress)) },
                    { "street", () => string.Format("\"street\":\"{0}\"", EscapeJson(street)) },
                    { "city", () => string.Format("\"city\":\"{0}\"", EscapeJson(city)) },
                    { "region", () => string.Format("\"region\":\"{0}\"", EscapeJson(region)) },
                    { "zipcode", () => string.Format("\"zipcode\":\"{0}\"", EscapeJson(zipcode)) },
                    { "country", () => string.Format("\"country\":\"{0}\"", EscapeJson(country)) },
                };
            return contactFieldsJsonValsDictionary;
        }

        private static string EscapeJson(string str)
        {
            if (string.IsNullOrEmpty(str))
            {
                return str;
            }

            return str.Replace("\n", "\\n")
                      .Replace("\r", "\\r")
                      .Replace("\t", "\\t")
                      .Replace("\"", "\\\"")
                      .Replace("&", "\\&");
        }
    }
}