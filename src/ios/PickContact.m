#import "PickContact.h"
#import <Cordova/CDVAvailability.h>

@implementation PickContact;
@synthesize callbackID;
CDVPluginResult* pluginResult;

- (void) chooseContact:(CDVInvokedUrlCommand*)command{
    self.callbackID = command.callbackId;

    ABPeoplePickerNavigationController *picker = [[ABPeoplePickerNavigationController alloc] init];
    picker.peoplePickerDelegate = self;
    [self.viewController presentViewController:picker animated:YES completion:nil];
}


- (void)peoplePickerNavigationController:(ABPeoplePickerNavigationController*)peoplePicker
                         didSelectPerson:(ABRecordRef)person
                                property:(ABPropertyID)property
                              identifier:(ABMultiValueIdentifier)identifier {
    
    [self peoplePickerNavigationController:peoplePicker shouldContinueAfterSelectingPerson:person property:property identifier:identifier];
}

- (BOOL)peoplePickerNavigationController:(ABPeoplePickerNavigationController*)peoplePicker
      shouldContinueAfterSelectingPerson:(ABRecordRef)person
                                property:(ABPropertyID)property
                              identifier:(ABMultiValueIdentifier)identifier


{
    if (kABPersonPhoneProperty == property)
    {	   
	    NSString *email = @"";
		ABMultiValueRef multiEmails = ABRecordCopyValue(person, kABPersonEmailProperty);
		
		NSLog(@"property:%d",property);
		NSLog(@"kABPersonEmailProperty:%d",kABPersonEmailProperty);
		NSLog(@"kABPersonPhoneProperty:%d",kABPersonPhoneProperty);
		NSLog(@"identifier:%d",identifier);
		NSLog(@"ABMultiValueGetCount(multiEmails):%ld",ABMultiValueGetCount(multiEmails));
		if (kABPersonEmailProperty == property)
		{
			int index = ABMultiValueGetIndexForIdentifier(multiEmails, identifier);
			NSLog(@"index:%d",index);
			email = (__bridge NSString *)ABMultiValueCopyValueAtIndex(multiEmails, index);
			NSLog(@"email:%@",email);
		}
		else if (ABMultiValueGetCount(multiEmails) > 0)
		{
			email = (__bridge NSString *)ABMultiValueCopyValueAtIndex(multiEmails, 0);
		}
	   

        NSString *displayName = (__bridge NSString *)ABRecordCopyCompositeName(person);


        ABMultiValueRef multiPhones = ABRecordCopyValue(person, kABPersonPhoneProperty);
        NSString* phoneNumber = @"";
        for(CFIndex i = 0; i < ABMultiValueGetCount(multiPhones); i++) {
            if(identifier == ABMultiValueGetIdentifierAtIndex (multiPhones, i)) {
                phoneNumber = (__bridge NSString *)ABMultiValueCopyValueAtIndex(multiPhones, i);
                break;
            }
        }

        NSMutableDictionary* contact = [NSMutableDictionary dictionaryWithCapacity:3];
		[contact setObject:email forKey: @"emailAddress"];
        [contact setObject:displayName forKey: @"displayName"];
        [contact setObject:phoneNumber forKey: @"phoneNr"];
		
		CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:contact];
		[self.commandDelegate sendPluginResult:result callbackId:self.callbackID];
        [self.viewController dismissViewControllerAnimated:YES completion:nil];
        return NO;
    }
    return YES;
}

- (BOOL) personViewController:(ABPersonViewController*)personView shouldPerformDefaultActionForPerson:(ABRecordRef)person property:(ABPropertyID)property identifier:(ABMultiValueIdentifier)identifierForValue
{
    return YES;
}

- (void)peoplePickerNavigationControllerDidCancel:(ABPeoplePickerNavigationController *)peoplePicker
{
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR
                                                messageAsString:@"PickContact abort"];
    [self.commandDelegate sendPluginResult:result callbackId:self.callbackID];
    [self.viewController dismissViewControllerAnimated:YES completion:nil];
}

@end