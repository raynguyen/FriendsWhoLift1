# Kinect
Android App that allows user's to join and create Events for social activities and to connect with people who have the same hobbies.

//google playstore hyper link

## Core Concepts
//Searching events via map
//Creating events and inviting users
### User Authentication
All user authentication processing is completed via FirebaseAuth.

### Application Backend
A combination of Cloud Firestore and Firebase Storage is used as the backend for Kinect. Client-side queries are passed to the Core_FireBaseRepo.class which then hooks the the Firebase API. With the exception of using authentication, all Firebase queries must pass through this repository class in order to retrieve data from the Firebase backend.

## About Me
I am an ex-engineer looking to switch into software development. I have a degree in physics and engineering diploma but after being in the industry for a couple years post graduation, I have come to realise software development is what I am passionate about and enjoy doing.
