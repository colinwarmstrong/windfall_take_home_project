# Windfall Programming Assignment by Colin Armstrong

## Technologies used
- Kotlin version 2.0.0
- JVM version 21
- Gradle version 8.5
- IntelliJ IDEA 2024.1.4 (used for writing and running program during development)

## Running the Program

This program is written in Kotlin and runs via the JVM. It uses Gradle as a build tool. It was primarily written and run via the IntelliJ IDE during development.

To download the code repository to your local machine via git, execute the following command in your terminal:

```
git clone https://github.com/colinwarmstrong/windfall_take_home_project.git
```

### Selecting a CSV file as input for the program
- In the `Spreadsheet.kt` class file, set the `pathname` string variable in the top level `main()` function to your desired CSV file path
- I have included multiple example CSV files in the `src/main/resources` directory that you can use
- By default, the `pathname` variable is pointed at `base_example.csv`. There are multiple other lines in the top level `main()` function with CSV file paths that are commented out.  Feel free to comment/uncomment these lines to set the `pathname` variable to the other example CSVs

### Running via IntelliJ (similar steps should work for other IDEs)
- Open project in IntelliJ/other IDE
- Navigate to the `src/main/kotlin/Spreadsheet.kt` file
- Click the play button next to line 5 to run program or select run "Current File" from the run menu/dropdown
- On Mac in IntelliJ, executing the ^R keyboard shortcut while in the `Spreadsheet.kt` file should also work

### Running via command line
- If you are unable to run the program via your IDE, you should be able to run it directly from the command line
- For more detailed instructions on compiling and running Kotlin programs via the command line, documentation can be found [here](https://kotlinlang.org/docs/command-line.html)
- Make sure you have Kotlin installed on your machine.  If using Homebrew, you can use:
```
brew install kotlin
```
- You will also need Java installed on your machine. Documentation for installing Java on a Mac can be found [here](https://www.java.com/en/download/help/mac_install.html)
- Navigate to the project's root directory from the command line
- Compile the program using `kotlinc`:
```
kotlinc src/main/kotlin -include-runtime -d spreadsheet.jar
```
- This command takes all the files in the `src/main/kotlin` directory and compiles them to the `spreadsheet.jar` file which should now be present in the root directory of the project
- Execute the newly created `spreadsheet.jar` file using Java from the command line:
```
java -jar spreadsheet.jar
```
- This will execute the program and output the calculated spreadsheet totals in CSV format to `stdout`
- To compile and run the program using a single command, execute the following from the project's root directory:
```
kotlinc src/main/kotlin -include-runtime -d spreadsheet.jar && java -jar spreadsheet.jar
```

## Program Summary
- All functionality outlined in the assignment description is present
- Can handle CSV cells with integers, floats, cell references, and +/- operators
- Error handling includes:
  - Circular reference detection
  - Invalid cell reference detection (i.e a cell references B3 but the CSV only has two rows)
  - Empty cell detection

## Areas for Improvement
- Error handling could be improved
- Additional edge cases could be accounted for
- Add ability to pass in a file path at runtime
- Add functionality to write spreadsheet totals to an actual file instead of just outputting text to `stdout`

## Contact Info
- Name: Colin Armstrong
- Email: colinwarmstrong@gmail.com
- Phone: 815-685-2473
- LinkedIn: [Colin Armstrong](https://www.linkedin.com/in/colinwarmstrong/)
