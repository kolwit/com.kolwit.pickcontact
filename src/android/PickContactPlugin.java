package com.kolwit.cordova;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONObject;

public class PickContactPlugin extends CordovaPlugin {

    private Context context;
    private CallbackContext callbackContext;

    private static final int CHOOSE_CONTACT = 1;

	@Override
	public boolean execute(String action, JSONArray data, CallbackContext callbackContext) {
        this.callbackContext = callbackContext;
	    this.context = cordova.getActivity().getApplicationContext();

		if (action.equals("chooseContact")) {
            Intent intent = new Intent(Intent.ACTION_PICK,
                     ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
            cordova.startActivityForResult(this, intent, CHOOSE_CONTACT);

            PluginResult r = new PluginResult(PluginResult.Status.NO_RESULT);
            r.setKeepCallback(true);
            callbackContext.sendPluginResult(r);
            return true;
		}

		return false;
	}

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {

            Uri contactData = data.getData();
            ContentResolver resolver = context.getContentResolver();
            Cursor c =  resolver.query(contactData, null, null, null, null);

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
                                new String[]{ contactId }, null);
                        phoneCursor.moveToFirst();
                        phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        phoneCursor.close();
                    }
					
					String email = "";
					Cursor emailCur = context.getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{contactId}, null);
					if(emailCur.getCount()>0) {
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
					if(addressCur.getCount()>0) {
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
                    callbackContext.success(contact);

                } catch (Exception e) {
                    callbackContext.error("Parsing contact failed: " + e.getMessage());
                }

            } else {
                callbackContext.error("Contact was not available.");
            }

            c.close();

        } else if (resultCode == Activity.RESULT_CANCELED) {
            callbackContext.error("No contact was selected.");
        }
    }

}
