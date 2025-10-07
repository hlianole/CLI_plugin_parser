# CLI plugin parser tool
CLI tool that takes a plugin artifact (.zip or .jar), 
parses its zip entries and saves the result 
to an output file with JSON format. It is possible to compare two output files.
___
# Usage:

Clone the repository first

```bash
git clone https://github.com/hlianole/CLI_plugin_parser.git
```
Go to the project folder
```bash
cd CLI_plugin_parser
```
## Mac/Linux

Build with gradle
```bash
./gradlew clean build
```
The tool artifact will appear in `build/libs/` as `app.jar`

You can use
```bash
java -jar build/libs/app.jar parse <path-to-.jar-or-.zip-file> <path-to-output-file>
```
The output file will be created if not exits

To shrink the command I created bash script

First use the following 
command to make the script executable:
```bash
chmod +x jarparser
```
Then
```bash
./jarparser parse <path-to-.jar-or-.zip-file> <path-to-output-file>
```
To compare output files use
```bash
java -jar build/libs/app.jar compare <path-to-output-file-1> <path-to-output-file-2>
```
or
```bash
./jarparser compare <path-to-output-file-1> <path-to-output-file-2>
```
## Windows

Build with gradle
```bash
gradlew clean build
```
The tool artifact will appear in `build/libs/` as `app.jar`

You can use
```bash
java -jar build/libs/app.jar parse <path-to-.jar-or-.zip-file> <path-to-output-file>
```
The output file will be created if not exits

To shrink the command I created .bat script

Use:
```bash
jarparser.bat parse <path-to-.jar-or-.zip> <path-to-output-json>
```
To compare output files use
```bash
java -jar build/libs/app.jar compare <path-to-output-file-1> <path-to-output-file-2>
```
or
```bash
jarparser.bat compare <path-to-output-file-1> <path-to-output-file-2>
```
___
# Project structure
main() method requires 3 arguments, first must be "parse" or "compare", and other 2
are paths to the files.
Main logic is presented in the `JarZipParser` class. 

Method `parse()` takes plugin artifact (.jar/.zip) and replies with
serializable class `model/ParsingResult`. I use Kotlinx serialization 
to work with Json. The result of parsing is written in the output file in 
the Json format.

Method `compare()` takes two files with Json format, decodes their structure and maps them 
back to the Kotlin class we can easily work with. I provided two types of comparison:
strict and basic. Strict compares based on completely matching zip entries,
with the same path and internal structure (files have the same hash).
Basic compares based on the same paths, but hash can differ (same files, but 
implementation is different).

For hashing, I used `MessageDigest` with `SHA-256` hashing algorithm.
