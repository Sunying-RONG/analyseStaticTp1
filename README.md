# analyseStaticTp1
1. Git clone https://github.com/Sunying-RONG/analyseStaticTp1.git  
Code of TP1 part 2 is in /analyseStaticTp1/src/main/java/partie2
2. In eclipse, import maven project analyseStaticTp1
3. Change JRE System Library to version 8
4. Add maven dependencies (should be included already in pom.xml)
5. Add Referenced Libraries : commons-io-2.4.jar  
Build Path - Configure Build Path - Java Build Path - Libraries - Add JARs (commons-io-2.4.jar in the root of the project analyseStaticTp1)
6. At the begining of code in /analyseStaticTp1/src/main/java/partie2/parser.java  
Change variable "projectPath" to the location of the project that you want to test  
Change variable "jrePath" to the location of jre (version 8) of your location machine
7. Run /analyseStaticTp1/src/main/java/partie2/parser.java 
8. Generated "graph.png" will be in root of "analyseStaticTp1" project (picture could be very big, wait for a while before open it)
