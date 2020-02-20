import 'Parser.dart' as Parser;
import 'dart:io';
import 'dart:convert';

void main(List<String> arguments) {
    if (arguments.isEmpty) {
        print("Tangara 2020-2020");
        print("Author: snaulX");
        print("License: MIT License");
        print("All copyrights resreved");
    }
    else {
        new File(arguments[0])
            .openRead()
            .map(utf8.decode)
            .transform(new LineSplitter())
            .forEach((l) => Parser.code.add(l));
        Parser.parse();
    }
}
