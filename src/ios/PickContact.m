#import "PickContact.h"
#import <Cordova/CDVAvailability.h>

@implementation PickContact;
@synthesize callbackID;

- (void) chooseContact:(CDVInvokedUrlCommand*)command{
    self.callbackID = command.callbackId;
	CNContactPickerViewController *picker = [[CNContactPickerViewController alloc] init];
	picker.delegate = self;
	[self.viewController presentViewController:picker animated:YES completion:nil];
}

- (void)contactPicker:(CNContactPickerViewController *)picker didSelectContact:(CNContact *)contact {	
	NSString *appContactPhone = @"";
	if( contact.phoneNumbers) appContactPhone = [[[contact.phoneNumbers firstObject] value] stringValue];
	if ( appContactPhone == nil ) appContactPhone = @"";
	
	NSString *appContactEmail = @"";
	if( contact.emailAddresses) appContactEmail = [[contact.emailAddresses firstObject] value];
	if ( appContactEmail == nil ) appContactEmail = @"";
	
	NSString *appContactPostaladdress = @"";
	if( contact.postalAddresses) appContactPostaladdress = [[contact.postalAddresses firstObject] value];
	if ( appContactPostaladdress == nil ) appContactPostaladdress = @"";
	
	NSString *appContactName = [[contact.givenName stringByAppendingString:@" "] stringByAppendingString:contact.familyName];
	
	NSMutableDictionary* appContact = [NSMutableDictionary dictionaryWithCapacity:2];
	[appContact setObject:contact.givenName forKey: @"firstName"];
	[appContact setObject:contact.familyName forKey: @"lastName"];
	[appContact setObject:appContactName forKey: @"displayName"];
	[appContact setObject:appContactName forKey: @"nameFormated"];
	[appContact setObject:contact.middleName forKey: @"nameMiddle"];
	[appContact setObject:appContactPhone forKey: @"phoneNr"];
	[appContact setObject:appContactEmail forKey: @"emailAddress"];
	[appContact setObject:appContactPostaladdress forKey: @"address"];
	
	/*
	[appContact setObject:@"" forKey: @"street"];
	[appContact setObject:@"" forKey: @"city"];
	[appContact setObject:@"" forKey: @"region"];
	[appContact setObject:@"" forKey: @"zipcode"];
	[appContact setObject:@"" forKey: @"country"];
	[appContact setObject:@"" forKey: @"contactId"];
	*/
	 
	CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:appContact];
	NSString * thisCallBackID = self.callbackID;
	[self.commandDelegate sendPluginResult:pluginResult callbackId:thisCallBackID];
	[self.viewController dismissViewControllerAnimated:YES completion:nil];
}

- (void)contactPicker:(CNContactPickerViewController *)picker didSelectContactProperty:(CNContactProperty *)contactProperty {
    
}

- (void)contactPickerDidCancel:(CNContactPickerViewController *)picker {
    [self.viewController dismissViewControllerAnimated:YES completion:nil];
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"PickContact abort"];
    NSString * thisCallBackID = self.callbackID;
    [self.commandDelegate sendPluginResult:pluginResult callbackId:thisCallBackID];
}

@end