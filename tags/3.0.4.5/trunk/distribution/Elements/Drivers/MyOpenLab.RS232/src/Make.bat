set CLASSPATH=.;%CLASSPATH%;..\bin\RXTXcomm.jar;C:\Users\carmelo\Documents\NetBeansProjects\myopenlab_source\distribution\Elements;
echo %classpath%
javac -source 1.7 -target 1.7 -d ..\bin  *.java
cmd