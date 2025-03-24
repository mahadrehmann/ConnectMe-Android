# ConnectMe

**ConnectMe** is an Android application that allows users to connect with others through various forms of communication such as messaging, calling, and video chatting. The app offers a seamless user experience, integrating with Firebase for real-time communication and SQLite for local storage of messages and user data.

## Features

- **User Authentication**: Users can sign up, log in, and log out securely using Firebase Authentication.
- **Voice and Video Calls**: Make voice and video calls using integrated APIs.
- **Push Notifications**: Get notified instantly of new messages and calls using Firebase Cloud Messaging.

## Installation

To set up and run **ConnectMe** on your local machine:

1. Clone the repository:
    ```bash
    git clone https://github.com/mahadrehmann/ConnectMe.git
    ```

2. Open the project in Android Studio.

3. Set up Firebase in your project:
   - Go to the [Firebase Console](https://console.firebase.google.com/) and create a new project.
   - Add Firebase Authentication and Real Time Database to your project.
   - Download the `google-services.json` file and add it to your app's `app/` directory.

4. Build and run the project on an Android device or emulator.

## Technologies Used

- **Kotlin**: Main programming language for Android app development.
- **Firebase**: For authentication, real-time messaging, and cloud messaging.

