package com.kolwit.cordova;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONObject;

public class PickContactPlugin extends CordovaPlugin {
    private Context context;
    private CallbackContext callbackContext;
    private static final int CHOOSE_CONTACT = 5555;

    public static final int UNKNOWN_ERROR = 0;
    public static final int PARSING_CONTACT_FAILED = 1;
    public static final int NOT_SUPPORTED_ERROR = 2;
    public static final int REQUEST_CANCELED = 3;
    public static final int CONTACT_NOT_AVAILABLE = 4;

	@Override
	public boolean execute(String action, JSONArray data, CallbackContext callbackContext) {
        this.callbackContext = callbackContext;
        this.context = cordova.getActivity().getApplicationContext();

        if (android.os.Build.VERSION.RELEASE.startsWith("1.")) {
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, NOT_SUPPORTED_ERROR));
            return true;
        }

		if (action.equals("chooseContact")) {
            Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
              cordova.startActivityForResult(this, contactPickerIntent, CHOOSE_CONTACT);
            PluginResult r = new PluginResult(PluginResult.Status.NO_RESULT);
            r.setKeepCallback(true);
            this.callbackContext.sendPluginResult(r);
        } else {
            return false;
		}
		return true;
	}

	/**
	 * Called when user picks contact.
	 * @param requestCode       The request code originally supplied to startActivityForResult(),
	 *                          allowing you to identify who this result came from.
	 * @param resultCode        The integer result code returned by the child activity through its setResult().
	 * @param data           	An Intent, which can return result data to the caller (various data can be attached to Intent "extras").
	 * @throws JSONException
	 */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHOOSE_CONTACT) {
            if (resultCode == Activity.RESULT_OK) {

                Uri contactData = data.getData();
                ContentResolver resolver = context.getContentResolver();
                Cursor c = resolver.query(contactData, null, null, null, null);

                if (c.moveToFirst()) {
                    try {
                        String contactId = c.getString(c.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                        String displayName = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));

                        String nameFormated = c.getString(c.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME));
                        String nameFamily = c.getString(c.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME));
                        String nameGiven = c.getString(c.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME));
                        String nameMiddle = c.getString(c.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME));

                        String phoneNumber = "";
                        if (Integer.parseInt(c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                            String query = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
                            Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                    new String[]{contactId}, null);
                            phoneCursor.moveToFirst();
                            phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            phoneCursor.close();
                        }

                        String email = "";
                        Cursor emailCur = context.getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{contactId}, null);
                        if (emailCur.getCount() > 0) {
                            while (emailCur.moveToNext())
                                email = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                        }
                        emailCur.close();

                        String addressFormatted = "";
                        String addressStreetAddress = "";
                        String addressLocality = "";
                        String addressRegion = "";
                        String addressPostalcode = "";
                        String addressCountry = "";
                        Cursor addressCur = context.getContentResolver().query(ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI, null, ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID + " = ?", new String[]{contactId}, null);
                        if (addressCur.getCount() > 0) {
                            while (addressCur.moveToNext()) {
                                addressFormatted = addressCur.getString(addressCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS));
                                addressStreetAddress = addressCur.getString(addressCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
                                addressLocality = addressCur.getString(addressCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
                                addressRegion = addressCur.getString(addressCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION));
                                addressPostalcode = addressCur.getString(addressCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
                                addressCountry = addressCur.getString(addressCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY));
                            }
                        }
                        addressCur.close();

                        JSONObject contact = new JSONObject();
                        contact.put("firstName", nameGiven);
                        contact.put("lastName", nameFamily);
                        contact.put("displayName", displayName);
                        contact.put("nameFormated", nameFormated);
                        contact.put("nameMiddle", nameMiddle);
                        contact.put("phoneNr", phoneNumber);
                        contact.put("emailAddress", email);
                        contact.put("address", addressFormatted);
                        contact.put("street", addressStreetAddress);
                        contact.put("city", addressLocality);
                        contact.put("region", addressRegion);
                        contact.put("zipcode", addressPostalcode);
                        contact.put("country", addressCountry);

                        // added `contactId` as suggested by aiksiang
                        contact.put("contactId", contactId);
                        this.callbackContext.success(contact);
                    } catch (Exception e) {
                        this.callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, PARSING_CONTACT_FAILED));
                    }
                } else {
                    this.callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, CONTACT_NOT_AVAILABLE));
                }
                c.close();
                return;
            } else if (resultCode == Activity.RESULT_CANCELED) {
                this.callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, REQUEST_CANCELED));
                return;
            }
        }
    }
}
