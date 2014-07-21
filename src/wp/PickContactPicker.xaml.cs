
namespace Cordova.Extension.Commands
{
    using System;
    using System.Linq;
    using System.Windows;
    using System.Windows.Controls;
    using System.Windows.Navigation;
    using Microsoft.Phone.Tasks;
    using Microsoft.Phone.UserData;
    using DeviceContacts = Microsoft.Phone.UserData.Contacts;
    using WPCordovaClassLib.Cordova.Commands;
    using WPCordovaClassLib.Cordova.JSON;
    using WPCordovaClassLib.Cordova;
    using WPCordovaClassLib.CordovaLib;

    public partial class PickContactPicker
    {
        #region Fields
        private PickContactTask.PickResult result;
        #endregion

        #region Constructors
        public PickContactPicker()
        {
            InitializeComponent();
            var cons = new DeviceContacts();
            cons.SearchCompleted += this.OnSearchCompleted;
            cons.SearchAsync(string.Empty, FilterKind.None, string.Empty);
        }
        #endregion

        #region Callbacks
        public event EventHandler<PickContactTask.PickResult> Completed;
        #endregion

        protected override void OnNavigatedFrom(NavigationEventArgs e)
        {
            if (this.result == null)
            {
                this.Completed(this, new PickContactTask.PickResult(TaskResult.Cancel));
            }

            base.OnNavigatedFrom(e);
        }

        private void OnSearchCompleted(object sender, ContactsSearchEventArgs e)
        {
            if (e.Results.Count() != 0)
            {
                lstContacts.ItemsSource = e.Results.ToList();
                lstContacts.Visibility = Visibility.Visible;
                NoContactsBlock.Visibility = Visibility.Collapsed;
            }
            else
            {
                lstContacts.Visibility = Visibility.Collapsed;
                NoContactsBlock.Visibility = Visibility.Visible;
            }
        }

        private void ContactsListSelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            this.result = new PickContactTask.PickResult(TaskResult.OK) { Contact = e.AddedItems[0] as Contact };
            this.Completed(this, this.result);

            if (NavigationService.CanGoBack)
            {
                NavigationService.GoBack();
            }
        }
    }
}