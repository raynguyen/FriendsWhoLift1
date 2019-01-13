# FriendsWhoLift1
Simple Android app to track weightlifting and running progress. Future refactoring to work with any sport and allow certain users to add  parameters to track.

## Concept and Inspiration
The inspiration for this app came from my need to create my own project to learn and familiarize myself with then android development environment.

Some key concepts that I will utilize are databases (SQLite in the current version of the project), various user interfaces, dialogs, and fragments. As these are fundamental in an android developer's toolkit, I want to ensure that I have a solid foundational understanding of these processes before going into the fine details.

## About Me
I am an engineer looking to switch into software development. I have a degree in physics and engineering but have experienced and worked with enough software develpment that I have committed to becoming a software developer.

## Current Goal
Refactor the main activity to be composed of Fragments (TextView fragment and buttons fragment).

## ToDo's
1. Login Activity (utilize UserPreferences).
2. Connect database to Firebase (research Amazon Web Services).
3. Create a user account system for:
  a. Administrators who can read and write to a database.
  b. Basic user will only have a local SQLite file for personal records and can only read the database.
4. Implement notification system once an Admin has updated stats for any given user.

## Completed
1. Create an SQLite database that can store user input.
2. Able to display contents of the database in a ListView.
3. Able to delete entries in database.
4. Movement across multiple activities.

## Acknowledgments
This list will continue to grow as I progress further into the development and eventual deployment of the app but for now:  
*Mitch Tabian - Coding With Mitch - A good friend who is well into his career of a self-employed software developer
*Nithya Vasudevan - Android Open Tutorials - Online resource with working examples to follow
