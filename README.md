# Connectify - Social Network Application

Connectify is a simple yet feature-rich Android-based social media application that I built as a personal project during college to explore and learn Android development. It lets users sign up, log in, and customize their profiles while connecting with others through real-time chat and post sharing. Users can follow or unfollow others, like, comment on, and save posts—offering an engaging and interactive experience.

The app is developed using Java/XML in Android Studio, with Firebase Realtime Database and Firebase Authentication handling the backend. It also makes use of Android Architecture Components to maintain a clean and scalable structure. This project helped me understand the full flow of a social media app, from user authentication to data handling and real-time interactions.

## Features

- **User Authentication**

  - Email/Password Sign Up and Sign In
  - Google Sign In integration
  - Secure user profile management

- **Social Features**

  - Create and share posts with text and images
  - Like and comment on posts
  - View friends' posts in a feed
  - Find and add new friends
  - View and manage your friend list

- **Messaging**

  - Real-time chat functionality
  - One-on-one messaging
  - Message history and notifications

- **Profile Management**

  - Customizable user profiles
  - Profile picture upload
  - View and edit personal information
  - Manage account settings

- **Content Management**
  - Create and share posts
  - View your own posts
  - Interact with others' content
  - Image cropping and optimization

## Technical Stack

- **Frontend**

  - Android SDK
  - Material Design components
  - Navigation Component
  - Picasso for image loading
  - CircleImageView for profile pictures

- **Backend**
  - Firebase Authentication
  - Firebase Realtime Database
  - Firebase Storage
  - Firebase Cloud Firestore
  - Firebase Cloud Messaging
  - Firebase Analytics

## Setup Instructions

1. Clone the repository
2. Open the project in Android Studio
3. Add your `google-services.json` file to the `app` directory
4. Sync the project with Gradle files
5. Build and run the application

## Dependencies

The project uses several key dependencies:

- Firebase SDKs for authentication, database, storage, and messaging
- AndroidX libraries for modern Android development
- Material Design components
- Navigation Component for fragment management
- Picasso for image loading
- Firebase UI for database operations
- Image cropping library
- Stories progress view for UI components

## Project Structure

- `MainActivity.java` - Main application activity
- `SplashScreenActivity.java` - Initial loading screen
- `RegisterActivity.java` - User registration flow
- `ChatActivity.java` - Messaging interface
- `PersonActivity.java` - User profile view
- Various Fragments for different features:
  - `HomeFragment.java` - Main feed
  - `ProfileFragment.java` - User profile
  - `FriendsFragment.java` - Friends list
  - `FindFriendsFragment.java` - Friend discovery
  - `PostFragment.java` - Post creation
  - `MyPostsFragment.java` - User's posts
  - `SettingsFragment.java` - Application settings

## Demo

https://appetize.io/app/q3g294nb7ymgy3a94wn8meky4w?device=nexus5&scale=75&osVersion=8.1&toolbar=true
