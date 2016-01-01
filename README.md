# Launchpad S+

![Launchpad S+ icon gif](http://helionmusic.com/extras/launchpad_s_plus/logo.gif)

Novation Launchpad S Java programming API and demo program

Only warning: This was my first ever real Java project, done during a semester in the context of a Java course. The source code might not be the best but it gets the job done. And I am absolutely terrible with working on GUI.

Project requirements set for the project during the course included setting up a Microsoft Access database to use within the program.

This means all the statistics and game scores within the program are saved in a Microsoft Access file (I don't like it either).

I will definitely update the code to be much more efficient in the near future once I free up some more time.


I would love to get my hands on a Launchpad MK2 and make this work with RGB colors instead of the Red+Green only that the Launchpad S has.


Note: This program should be more or less compatible with the original Launchpad, with the exception of the colors being completely off.

Since I do not own an original Launchpad, I have not been able to test it.

For this reason, I have made the program ignore anything other than a Launchpad S.


Now onto a little preview:

Compiling and running the program, you will be met with a main window as follows, containing the various demos I've set up to demonstrate what was possible with the API, in the short time period I've had during the course.
![Launchpad S+ main window and statistics window](http://helionmusic.com/extras/launchpad_s_plus/preview.png)
The picture also shows a preview of the statistics window that will be able to show what has been used since the program was first run and during the current program session.
This information is all stored within the Microsoft Access database file, so resetting that will reset the statistics.
Statistics are accessible from the ? toolbar button

Go ahead and discover the rest of the available demos.

From left-to-right top-to-bottom, the demos are the following:
  * Game: Checkers/Draughts (still buggy and incomplete at this point - No GUI window ready)
  * Tech Demo: Conrad's Game of Life - Pretty self explanatory. Contains speed, play/pause simulation, known shapes generation and random board filling controls.
  * Game: Lights Out (Puzzle game)
  * Game: Reaction/Whack-a-mole type game (Incomplete GUI - Game output is on the console for now)
  * Game: Simon (Memory game)
  * Game: Snake - We all know this one. Supports keyboard arrows control and Launchpad S arrows control
  * Tech Demo: Piano - Recreated and modified MIDI keyboard mode from Image Line's FL Studio Launchpad piano mode. This tech demo supports Virtual MIDI to route all midi received from the program to any other MIDI enabled program. By default, the program will instead use the Java piano synthesizer, but you are able to use Virtual MIDI instead IF you have LoopBe1 Monitor installed. If it is not installed, you will be presented with a link under the Toggle Virtual MIDI button to download and install it.
  * Tech Demo: Scrolling Text - Allows to display custom text messages on the Launchpad with control over color, speed, color intensity and text looping. Text is limited to 31 characters because the Launchpad S's buffer can only contain 31 characters + 1 character for the message termination.
  * Tech Demo: Reactive animations - Allows to display different animations from the pad that is pressed on the launchpad. Animation switch control is possible from the Launchpad itself with the Arrow right and Arrow left buttons. Mixer button quits the demo.
  * Tech Demo: Animate - Displays animations on the Launchpad without requiring user input. Some animations have controls for particle density, some don't. Buttons are disabled for those that do not have the option on the GUI. Animation switch control is possible from the Launchpad itself with the Arrow right and Arrow left buttons, and particle density is available with the User 1 and User 2 buttons. Mixer button quits the demo


With that out of the way, have fun :)

I will update the program as often as I can. And I will definitely make the API a standalone package. Hopefully my noob code isn't terrible



Enjoy!
-Helion

___________________________

If you're interested in music or would like to communicate, take a look at my stuff:

Website: http://www.helionmusic.com

YouTube: https://www.youtube.com/helionmusic

Twitter: https://www.twitter.com/helionmusic

Instagram: https://www.instagram.com/helionmusic

Facebook: https://www.facebook.com/helionmusic

Soundcloud: https://soundcloud.com/helionmusic

iTunes: http://itunes.apple.com/us/artist/helion/id443923289
