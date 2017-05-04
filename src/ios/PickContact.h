#import <UIKit/UIKit.h>
#import <Foundation/Foundation.h>
#import <ContactsUI/ContactsUI.h>
#import <Cordova/CDVPlugin.h>

@interface PickContact : CDVPlugin <CNContactPickerDelegate>

@property(strong) NSString* callbackID;

- (void) chooseContact:(CDVInvokedUrlCommand*)command;

@end
