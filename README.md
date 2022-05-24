# DrMarioAi_ThesisProject

## Compiling the java code into a JAR file 

In order to run the code written in the src/drmarioai file, we must first bundle it into a jar file. 

1. Compile the java files 
  
  We must first complie the java files. 
  In order to use the Nintaco API, we must set the classpath to the path of the Nintaco.jar file.
  
  
  Compiling the DRMARIO_TVO files 
  ```Language
    javac -cp "/path/to/Nintaco.jar" -d bin src/drmarioai/Greedy/*.java src/drmarioai/*.java
  ```
  
  Compiling the DRMARIO_ALS files 
  ```Language
    javac -cp "/path/to/Nintaco.jar" -d bin src/drmarioai/Greedy_BST/*.java src/drmarioai/*.java
  ```
  
  
 2. Create a manifest.txt file
 
  A manifest file sets an entry points to the application. 
  It specifies which main class we are going to execute to the jarfile.
  
  manifest files example 
  ```Language
    Main-Class: TopView_V1_main
    
  ```
  
  3. Create the JAR 

  We create the JAR file by giving it the manifest file and bin directory. 
   ```Language
    jar cvfm DRMARIO_TVO.jar manifest.txt -C bin .
   ```
   
## Running the JAR file 

In order to run the JAR files in Nintaco. We must first run Nintaco.jar and select its "Run Program" option under Tools. 
Then we select the appropriate JAR file and main class and click Run. 

