namespace Cordova.Extension.Commands
{
    using System;
    using System.Windows;
    using Microsoft.Phone.Controls;
    using Microsoft.Phone.Tasks;
    using Microsoft.Phone.UserData;
    using WPCordovaClassLib.Cordova.Commands;
    using WPCordovaClassLib.Cordova.JSON;
    using WPCordovaClassLib.Cordova;
    using WPCordovaClassLib.CordovaLib;

    public class PickContactTask
    {
        public event EventHandler<PickResult> Completed;

        public void Show()
        {
            Deployment.Current.Dispatcher.BeginInvoke(() =>
            {
                var root = Application.Current.RootVisual as PhoneApplicationFrame;

                string baseUrl = "/";

                if (root != null)
                {
                    root.Navigated += this.OnNavigate;
                    root.Navigate(
                        new Uri(
                            baseUrl + "Plugins/com.kolwit.pickcontact/PickContactPicker.xaml?dummy="
                            + Guid.NewGuid(),
                            UriKind.Relative));
                }
            });
        }

        private void OnNavigate(object sender, System.Windows.Navigation.NavigationEventArgs e)
        {
            if (!(e.Content is PickContactPicker))
            {
                return;
            }

            var phoneApplicationFrame = Application.Current.RootVisual as PhoneApplicationFrame;
            if (phoneApplicationFrame != null)
            {
                phoneApplicationFrame.Navigated -= this.OnNavigate;
            }

            PickContactPicker contactPicker = (PickContactPicker)e.Content;

            if (contactPicker != null)
            {
                contactPicker.Completed += this.Completed;
            }
            else if (this.Completed != null)
            {
                this.Completed(this, new PickResult(TaskResult.Cancel));
            }
        }

        public class PickResult : TaskEventArgs
        {
            public PickResult()
            {
            }

            public PickResult(TaskResult taskResult)
                : base(taskResult)
            {
            }

            public Contact Contact { get; internal set; }
        }
    }
}
