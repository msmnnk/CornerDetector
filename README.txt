CMT107 VISUAL COMPUTING COURSEWORK

--Compilation--

This project is recommended to be compiled with JDK version 14 and newer (because it contains new Java expressions such as new switch case syntax). Originally JDK 17.0.3 was used.
This project uses Maven as a build automation tool, so make sure you pressed Load Maven Changes button on the top right corner (Ctrl + Shift + O), as well as pressed Reload All Maven Projects before the compilation.
All the libraries and their versions used in this project could be found in the pom.xml file.

Two corner detection algorithms (Harris and Shi-Tomasi) were implemented. The program is able to: 
• load and display an initial image to be processed by the algorithms; 
• implement Harris corner detection algorithm;
• implement Shi-Tomasi corner detection algorithm;
• display side-by-side the corner detection results using the two algorithms when the key 'p'/'P' is pressed.
Each step of both algorithms and the difference between them is clearly described in the comments.
The CornerDetector file is located in the package Basic.