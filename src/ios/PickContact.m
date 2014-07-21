#import "PickContact.h"
#import <Cordova/CDVAvailability.h>

@implementation PickContact;
@synthesize callbackID;

- (void) chooseContact:(CDVInvokedUrlCommand*)command{
    self.callbackID = command.callbackId;

    ABPeoplePickerNavigationController *picker = [[ABPeoplePickerNavigationController alloc] init];
    picker.peoplePickerDelegate = self;
    [self.viewController presentViewController:picker animated:YES completion:nil];
}

- (BOOL)peoplePickerNavigationController:(ABPeoplePickerNavigationController*)peoplePicker
      shouldContinueAfterSelectingPerson:(ABRecordRef)person
                                property:(ABPropertyID)property
                              identifier:(ABMultiValueIdentifier)identifier
{
    if (kABPersonPhoneProperty == property)
    {
        ABMultiValueRef multi = ABRecordCopyValue(person, kABPersonPhoneProperty);
        int index = ABMultiValueGetIndexForIdentifier(multi, identifier);

        NSString *displayName = (__bridge NSString *)ABRecordCopyCompositeName(person);


        ABMultiValueRef multiPhones = ABRecordCopyValue(person, kABPersonPhoneProperty);
        NSString* phoneNumber = @"";
        for(CFIndex i = 0; i < ABMultiValueGetCount(multiPhones); i++) {
            if(identifier == ABMultiValueGetIdentifierAtIndex (multiPhones, i)) {
                phoneNumber = (__bridge NSString *)ABMultiValueCopyValueAtIndex(multiPhones, i);
                break;
            }
        }

        NSMutableDictionary* contact = [NSMutableDictionary dictionaryWithCapacity:2];
        [contact setObject:displayName forKey: @"displayName"];
        [contact setObject:phoneNumber forKey: @"phoneNr"];

        [super writeJavascript:[[CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:contact] toSuccessCallbackString:self.callbackID]];
        [self.viewController dismissViewControllerAnimated:YES completion:nil];
        return NO;
    }
    return YES;
}

- (BOOL) personViewController:(ABPersonViewController*)personView shouldPerformDefaultActionForPerson:(ABRecordRef)person property:(ABPropertyID)property identifier:(ABMultiValueIdentifier)identifierForValue
{
    return YES;
}

- (void)peoplePickerNavigationControllerDidCancel:(ABPeoplePickerNavigationController *)peoplePicker{
    [self.viewController dismissViewControllerAnimated:YES completion:nil];
    [super writeJavascript:[[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR
                                                              messageAsString:@"PickContact abort"]
                                            toErrorCallbackString:self.callbackID]];
}

@end