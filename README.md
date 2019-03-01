# KosenXPoly-neto
This is our software solution - neto, helping you, help others. Below is the android app that we have created

Bryan: Defiantly used Google's Firebase services as it is part of his course requirements. #pleasedon'tkillme



# Current Progress

1. Created ```login page```: ```login``` and ```sign up```. User has to agree to terms and conditions to be successfully signed up. On login page, key in email and password. Use this registered email: ```bryantee11998@gmail.com``` password: ```123456```

2. Main map View: The map view is done with Google API. Here is where you can view all the ```SOS events``` created as well as a single unpolished ```SOS button```. Tap on a marker to view it's ```latitude``` and ```longitude```(<-in this order), tap and hold will open the ```SOS event``` and you can view ```event details```, ```date```, ```time``` and an ```image``` of the ```SOS event```. There is also a floating action button at the bottom right corner which will be discussed later on.

Tap the ```SOS button``` will bring up a simple, unpolished ```event report``` screeen. Tap the ```camera icon``` to take a picture (this step at the moment is mandatory, but no error will be shown. No picture taken will result in a program crash when viewing it, I think :P). 

Enter details, and click submit to submit the case to ```Firebase Firestore```, Image sent to ```Firebase Storage```.

3. ```Chat room```/```Documentary room```: Once an event has been created, a chat room is dynamically generated and is instantiated with an admin message. Enter whatever that is seen to this chat room, making this chat room an official documentary of what first aiders did to the victim. I have not tested 2 devices sending and receiving, that will probably not work :P. 

4. ```Navigation drawer```: at any point of time, user can choose slide from left of screen to right to pull out a navigation drawer. Currently, the drawer only allows users to navigate back to map, report an SOS case, edit profile.

Things that are undone:

- Sign out button
- auto sign-in
- polish SOS button
- card based layout on the google maps (refer to google maps), plan to copy exactly >:D
- reporting event on the map
- add user profile picture
- edit user profile picture
- add ML(?)
