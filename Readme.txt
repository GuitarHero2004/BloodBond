1. General Information:
    - Student Id: s3979367
    - Student Full Name: Tran Nguyen Anh Minh
    - Course: COSC2657 - Android Development
    - Assignment: Assignment 2
    - Lecturer: Dr. Minh VT

2. Project Information:
    - Application Name: BloodBond
    - Motivation: Blood donation plays a vital role in saving lives and supporting healthcare systems globally. Organizations like the Red Cross and community blood banks have successfully raised awareness and mobilized volunteers through in-person campaigns. However, the COVID-19 pandemic disrupted many of these life-saving initiatives, creating an urgent need for digital platforms to connect donors and donation sites. BloodBond aims to address this gap by providing a user-friendly and efficient mobile solution to manage blood donation activities, thus encouraging participation and ensuring continuity in life-saving efforts.
    - Version: 1.0
    - Date Completed: Friday, 27/12/2024
    - Description: BloodBond is an innovative mobile application designed to bridge the gap between blood donors, donation site managers, and administrators. With a modern, intuitive interface and features like dynamic map-based navigation, volunteer management, and PDF report generation, BloodBond offers a comprehensive platform for managing blood donation activities efficiently. It leverages Firebase for secure authentication and data storage, ensuring seamless and scalable functionality. BloodBond is a powerful tool for promoting blood donation, saving lives, and supporting healthcare systems worldwide. The application is built using Android Studio, Firebase Authentication, Firestore Database, Google Maps API, and Material Design components. It integrates user roles, search functionality, and PDF report generation to enhance the user experience and streamline blood donation activities. BloodBond is a testament to the power of technology in driving positive social impact and fostering community engagement. It is a valuable resource for blood donation organizations, volunteers, and donors seeking to make a difference in the world.
    - Application Detailed Information:
        + Packages/Folders (Inside com.example.bloodbond):
            # not in package (activity):
                * LoginView: Manages user authentication, allowing donors and site managers to log in securely using email and password.
                * SignUpView: Enables users to register for an account, choose their roles (donor or site manager), and input personal details.
                * DonorView/SiteManagerView/SuperUserView: Serve as the main containers for navigating between feature fragments based on user roles.
                * AddDonationSiteActivity: Allows site managers to add new donation sites with details like location, contact information, and blood type requirements.
                * UpdateDonationSiteActivity: Facilitates editing existing donation site details for site managers.
                * DonationSiteDetailActivity: Displays detailed information about specific donation sites, including dynamic visibility of actions based on user roles.

            # adapter: Contains adapters for RecyclerViews, TabLayouts, and ViewPagers to ensure efficient and dynamic display of data across the app.

            # model: Contains the model classes for the application
                * UserModel: The base class for all users, with common attributes like name, email, and user role.
                * Donor: Extends UserModel, with attributes like bloodType and bloodAmountDonated to track donor-specific data.
                * SiteManager: Extends UserModel, adding attributes like phoneNumber and a list of managed sites.
                * SuperUser: Extends UserModel with privileges for administrative tasks such as generating cross-site reports.
                * DonationSite: Represents donation sites, encapsulating details like name, address, accepted blood types, and registered volunteers/donors.

            # helper: Provide reusable functionality for core operations:
                * FirestoreHelper: Facilitates data read/write operations with Firestore for storing user and site information.
                * AuthHelper: Manages user authentication through Firebase.

            # fragment: Contains the fragments for the application
                * DonorMainFragment: Enables donors to search and view donation sites, with filtering options for blood type requirements.
                * SiteManagerMainFragment: Provides site managers with tools to manage their sites, including adding, editing, and deleting sites.
                * SuperUserMainFragment: Allows super users to view all donation sites and generate high-level reports.
                * DonorUserFragment/SiteManagerUserFragment/SuperUserFragment: Represents the user profile fragment for donors, site managers, and super users, displaying user's information & logout functionalities
                * BottomMapInfoFragment: Represents the bottom sheet fragment for the Google Maps, displaying the donation site's information and get directions
                * DonorMapFragment: Integrate Google Maps API for location-based functionalities, including displaying donation sites with customized markers.

        + Features & Unique Functionalities:
            # General Features:
                * User Authentication: Secure email and password-based login and registration.
                * User Profile: Customised dashboards and permissions for donors, site managers, and super users.
                * User Roles: Allow users to update personal information and log out seamlessly.

            # Donor:
                * Search Donation Sites: Search donation sites by name or blood type.
                * View All Donation Sites: Donors can view all donation sites and their information.
                * Register as Donor: Donors can register as donors with their data passed through Intent from the Sign Up/Login Activity.
                * View Donation Sites On Google Maps: View donation sites on an interactive map with clickable markers and site information.

            # Site Manager:
                * Register as Volunteer: Register as volunteers for managed donation sites.
                * View Registered Donors/Volunteers: Site managers can view all registered donors and volunteers for their donation sites.
                * Add/Edit/Delete Donation Site: Add, edit, and delete donation sites with real-time updates in the database.
                * View Amount of Blood Collected: Site managers can view the total amount of blood donated to their donation sites.
                * Generate PDF Report of Blood Amount Donated/Registered Donors/Registered Volunteers: View detailed reports of registered donors, volunteers, and collected blood amounts.

            # Super User:
                * Generate PDF Reports: Generate detailed, system-wide reports on donor participation, blood donations, and site performance.
                * View All Donation Sites: Access all donation site information for administrative oversight.

            # Donation Site:
                * Add Donation Site: New donation sites can be added by Site Managers in the Add Donation Site Activity
                * Edit Donation Site: Donation sites' information can be edited by Site Managers in the Update Donation Site Activity
                * Delete Donation Site: Donation sites can be deleted by Site Managers

        + Screens:
            # activity:
                * activity_add_donation_site: Represents the add donation site activity for site managers
                * activity_update_donation_site_detail: Represents the update donation site activity for site managers
                * activity_donation_site_detail: Represents the donation site detail activity for donors/site managers/super users
                * activity_login: Represents the login activity for the application
                * activity_sign_up: Represents the sign-up activity for the application
                * activity_donor: Represents the donor activity for the application (includes 3 fragments: DonorMainFragment, DonorUserFragment, DonorMapFragment)
                * activity_site_manager: Represents the site manager activity for the application (includes 2 fragments: SiteManagerMainFragment, SiteManagerUserFragment)
                * activity_super_user: Represents the super user activity for the application (includes 2 fragments: SuperUserMainFragment, SuperUserUserFragment)

            # fragment:
                * fragment_donor_main: Represents the main fragment for donors
                * fragment_site_manager_main: Represents the main fragment for site managers
                * fragment_super_user_main: Represents the main fragment for super users
                * fragment_donor_user: Represents the user profile fragment for donors
                * fragment_site_manager_user: Represents the user profile fragment for site managers
                * fragment_super_user_user: Represents the user profile fragment for super users
                * fragment_bottom_map_info: Represents the bottom sheet fragment for the Google Maps
                * fragment_donor_map: Represents the map fragment for donors
                * fragment_bottom_sheet_info: Represents the bottom sheet fragment for the donation site detail activity when triggering the View functionalities

            # item:
                * item_donation_site: Represents the donation site item for the RecyclerView in the application
                * item_map_donation_site: Represents the donation site item for the Google Maps in the application

        + Technologies:
            # Android Studio: Primary IDE for developing the application.
            # Firebase: The application uses Firebase Authentication for user authentication and Firestore Database for data storage.
            # Google Maps API: Integrates interactive maps to enhance location-based functionality.
            # PDF Generator: Uses PdfDocument to create downloadable reports.

        + Dependencies:
            # Firebase Authentication: Ensures secure login and account management for all users.
            # Firestore Database: Stores and retrieves user, site, and donation-related data in real-time.
            # Google Maps API: Integrates interactive maps to enhance location-based functionality.
            # Material Design: Provides a modern, consistent UI experience across the app.
            # Location Permission: The application requests location permission to display donation sites on Google Maps.


3. Drawbacks:
    - Time Constraints: Development was completed within a tight deadline, which limited feature enhancements and optimisation.
    - Lack of Testing: The app has not been tested extensively across different devices, screen sizes, and Android versions, which may lead to compatibility issues.
    - Directions API Absence: The app does not provide step-by-step navigation or directions from a user’s location to donation sites.
    - Notification System: Firebase Cloud Messaging for real-time updates (e.g., site deletions or updates) has not been implemented.
    - Startup Performance: The app’s boot time may be longer due to initialization of Firebase services and map features.
