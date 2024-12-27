1. General Information:
    - Student Id: s3979367
    - Student Full Name: Tran Nguyen Anh Minh
    - Course: COSC2657 - Android Development
    - Assignment: Assignment 2
    - Lecturer: Dr. Minh VT

2. Project Information:
    - Application Name: BloodBond
    - Motivation: Blood drives have been successfully held by nonprofits like the Red Cross and community blood banks to increase awareness and save lives, and they have received a lot of participation from people all over the world. However, the Covid pandemic caused many of these activities to halt. There is a renewed emphasis on reviving these initiatives as the pandemic fades. To facilitate this, an effective online system is required to efficiently manage volunteers, donation sites, and communications.
    - Version: 1.0
    - Date Completed: Friday, 27/12/2024
    - Description: BloodBond is a mobile application that aims to connect blood donors with blood donation sites. The application allows users to register as donors, search for donation sites, and schedule appointments. The application also provides a platform for blood donation sites to manage their volunteers and communicate with donors. The application is designed to be user-friendly and intuitive, with a clean and modern interface. The application is built using Android Studio and Firebase, and it is compatible with Android devices running Android 5.0 and above.
    - Application Detailed Information:
        + Packages/Folders (Inside com.example.bloodbond):
            # not in package (activity):
                * LoginView: Represents the login activity for the application, handling user login with email and password
                * SignUpView: Represents the sign-up activity for the application, handling user registration with email and password, user role selection, and user data input
                * DonorView/SiteManagerView/SuperUserView: Handling the navigation between fragments
                * AddDonationSiteActivity: Represents the add donation site activity for site managers, handling the addition of new donation sites
                * UpdateDonationSiteActivity: Represents the update donation site activity for site managers, handling the update of donation sites' information
                * DonationSiteDetailActivity: Represents the donation site detail activity display for donors/site managers/super users, handling the donation site's information & dynamic setting visibility of the buttons/functionalities

            # adapter: Contains the adapters for the RecyclerViews, TabLayout, ViewPager in the application

            # model: Contains the model classes for the application
                * UserModel: Represents a user registered for the application, the parent class of Donor, SiteManager, and SuperUser
                * Donor: Represents a donor in the application, extends from UserModel, handling String bloodType, double bloodAmountDonated
                * SiteManager: Represents a site manager in the application, extends from UserModel, handling String phoneNumber, List<String> sitesManagedNames
                * SuperUser: Represents a super user in the application, extends from UserModel, handling boolean canGenerateReports
                * DonationSite: Represents a donation site in the application, handling String name, String address, String phoneNumber, String email, String openingHours, String closingHours, List<String> bloodTypesAccepted, List<String> volunteers, List<String> donors

            # helper: Contains AuthHelper and FirestoreHelper classes to handle Firebase Authentication and Firestore Database

            # fragment: Contains the fragments for the application
                * DonorMainFragment: Represents the main fragment for donors, handling the search functionality and all donation sites
                * SiteManagerMainFragment: Represents the main fragment for site managers, handling the list of donation sites managed by the site manager
                * SuperUserMainFragment: Represents the main fragment for super users, handling the list of all donation sites
                * DonorUserFragment/SiteManagerUserFragment/SuperUserFragment: Represents the user profile fragment for donors, site managers, and super users, displaying user's information & logout functionalities
                * BottomMapInfoFragment: Represents the bottom sheet fragment for the Google Maps, displaying the donation site's information and get directions
                * DonorMapFragment: Represents the map fragment for donors, displaying all donation sites on Google Maps with customised markers

        + Features & Unique Functionalities:
            # General:
                * User Authentication: Users can register and log in to the application using their email and password
                * User Profile: Users can view and edit their profile information, including their name, email, and password
                * User Roles: Users can register as donors or site managers each with different permissions and capabilities

            # Donor:
                * Search Donation Sites: Donors can search for donation sites based on the existing donation site's name and blood type needed through donation
                * View All Donation Sites: Donors can view all donation sites and their information
                * Register as Donor: Donors can register as donors with their data passed through Intent from the Sign Up/Login Activity
                * View Donation Sites On Google Maps: Donors can view all donation sites on Google Maps, the donation site's name will appear based on user zoom in/out level to cover the area that holding the customised marker

            # Site Manager:
                * Register as Volunteer: Site managers can register themselves as volunteers for their donation sites
                * View Registered Donors/Volunteers: Site managers can view all registered donors and volunteers for their donation sites
                * Add Donation Site: Site managers can add new donation sites in the Add Donation Site Activity (their phoneNumber is passed through Intent)
                * Edit Donation Site: Site managers can edit their donation sites' information in the Update Donation Site Activity
                * Delete Donation Site: Site managers can delete their donation sites
                * View Amount of Blood Collected: Site managers can view the total amount of blood donated to their donation sites
                * Generate PDF Report of BloodAmountDonated: Donors can generate a PDF report of their blood amount donated
                * Generate PDF Report of Donors/Volunteers: Donors can generate a PDF report of all donors and volunteers for their donation sites

            # Super User:
                * Generate PDF Reports: Super users can generate reports for all donation sites, including the total number of donors, volunteers, blood collected, ...
                * View All Donation Sites: Super users can view all donation sites and their information (excluding Registered Donors/Volunteers functionalities)

            # Donation Site:
                * Add Donation Site: New donation sites can be added by Site Managers in the Add Donation Site Activity
                * Edit Donation Site: Donation sites' information can be edited by Site Managers in the Update Donation Site Activity
                * Delete Donation Site: Donation sites can be deleted by Site Managers

        + Screens:
            # activity:
                * activity_add_donation_site:
                * activity_update_donation_site_detail:
                * activity_donation_site_detail:
                * activity_login:
                * activity_sign_up:
                * activity_donor:
                * activity_site_manager:
                * activity_super_user:

            # fragment:
                * fragment_donor_main:
                * fragment_site_manager_main:
                * fragment_super_user_main:
                * fragment_donor_user:
                * fragment_site_manager_user:
                * fragment_super_user_user:
                * fragment_bottom_map_info:
                * fragment_donor_map:
                * fragment_bottom_sheet_info:

            # item:
                * item_donation_site:
                * item_map_donation_site:

        + Technologies:
            # Android Studio: The application is built using Android Studio, an integrated development environment for Android app development
            # Firebase: The application uses Firebase Authentication for user authentication and Firestore Database for data storage
            # Google Maps API: The application integrates the Google Maps API to display donation sites on Google Maps
            # PDF Generator: The application uses the PdfDocument to generate PDF reports for donation sites

        + Dependencies:
            # Firebase Authentication: The application uses Firebase Authentication to handle user authentication
            # Firestore Database: The application uses Firestore Database to store user data and donation site information
            # Google Maps API: The application integrates the Google Maps API to display donation sites on Google Maps
            # Material Design: The application uses Material Design components and guidelines for a modern and intuitive user interface
            # Location Permission: The application requests location permission to display donation sites on Google Maps


3. Drawbacks:
    - Time Constraints: The application was developed within a limited time frame, which may have affected the quality of the code and the user experience since I had to rush some parts of the application & finished group project of the DSA course
    - Lack of Testing: The application was not thoroughly tested on different devices and screen sizes, which may result in compatibility issues and bugs
    - Directions API Integration: The application does not integrate the Directions API to provide directions from the user's current location to the selected donation site
    - Notifications for Update/Delete Donation Site Using Firebase Cloud Messaging: The application does not send notifications to donors and volunteers when a donation site's information is updated or the donation site is deleted
    - Booting Application: The application might take quite a long time to boot up
