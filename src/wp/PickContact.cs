using Microsoft.Phone.Tasks;
using Microsoft.Phone.UserData;
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Globalization;
using System.Linq;
using System.Runtime.Serialization;
using System.Windows;
using DeviceContacts = Microsoft.Phone.UserData.Contacts;
using WPCordovaClassLib.Cordova.Commands;
using WPCordovaClassLib.Cordova.JSON;
using WPCordovaClassLib.Cordova;
using WPCordovaClassLib.CordovaLib;

namespace Cordova.Extension.Commands
{
    public class PickContact : BaseCommand
    {
        public const int UNKNOWN_ERROR = 0;
        public const int INVALID_ARGUMENT_ERROR = 1;
        public const int TIMEOUT_ERROR = 2;
        public const int PENDING_OPERATION_ERROR = 3;
        public const int IO_ERROR = 4;
        public const int NOT_SUPPORTED_ERROR = 5;
        public const int PERMISSION_DENIED_ERROR = 20;
        public const int SYNTAX_ERR = 8;

        public void chooseContact(string arguments)
        {
            var task = new PickContactTask();

            task.Completed += delegate(Object sender, PickContactTask.PickResult e)
                {
                    if (e.TaskResult == TaskResult.OK)
                    {
                        string strResult = e.Contact.ToJson();
                        var result = new PluginResult(PluginResult.Status.OK)
                            {
                                Message = strResult
                            };
                        DispatchCommandResult(result);
                    }
                    if (e.TaskResult == TaskResult.Cancel)
                    {
                        DispatchCommandResult(new PluginResult(PluginResult.Status.ERROR, "Operation cancelled."));
                    }
                };

            task.Show();
        }
    }
}