Teachable IR
====================

Required Libraries
---------------------
+OSCP5 (http://www.sojamo.de/libraries/oscP5/)
+Processing Core (http://processing.org/reference/)

Required Software
---------------------
+OSCulator (http://www.osculator.net/)

Get Started
---------------------
+Run OSCulator and pair your Wiimote
+Ensure that IR messages are being captured
+Ensure OSC messages are being forward to port 9000 (you will see a green light in OSCulator by IR messages when they are being forwarded properly)
+Power on your IR circuit (consists of TWO (2) IR LEDs, each in series with a 1 Watt, 100 Ohm resistor, if powered from a 6V power supply). It is important to calculate the proper forward resistors for you circuit by looking at the forward voltage specification corresponding to the resistors you will be using.
+Change variable in code to match the Wiimote string, i.e. "/wii/2/ir/xys/1"
+Save and run application

Testing The Code
---------------------
+If working properly, you will see a blue dot move across the screen as you move your Wiimote in front of your IR LED circuit
+Press the "C" key to set each calibration point. Each set calibration point will appear as a red dot.
+After pressing "C" FOUR (4) times, you will define the calibrated working space
+Now, move the Wiimote so that the blue dot enters the quadrilateral representing the calibrated working space
+You will see a mapped green dot on the right side of the screen corresponding to the mapping between the calibrated working space and the rectangle defined by the right side of the screen.

Where The Magic Happens
---------------------
+public void calculateTransform(ArrayList<PVector> dp, ArrayList<PVector> cp), Make sure to read: http://docs.oracle.com/cd/E17802_01/products/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/PerspectiveTransform.html
+public void drawCurrentLocation(PVector p)
