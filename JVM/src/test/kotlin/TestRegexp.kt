import com.snaulX.Tangara.Parser
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*

val parser: Parser = Parser()

@Test
fun `test class regexp`() {
    assertTrue(parser.class_regex.matches("public static class MyClass"))
    assertTrue(parser.class_regex.matches("private final class FinalClass"))
    assertTrue(parser.class_regex.matches("abstract class MyClass"))
    assertTrue(parser.class_regex.matches("class MyClass"))
    assertTrue(parser.class_regex.matches("protected data class DataClass"))
    assertFalse(parser.class_regex.matches("data clas DataClass"))
    assertFalse(parser.class_regex.matches("data DataClass"))
    assertFalse(parser.class_regex.matches("klass MyClass"))
    assertFalse(parser.class_regex.matches("data public class DataClass"))
}

@Test
fun `test interface regexp`() {
    assertTrue(parser.interface_regex.matches("interface MyIface"))
    assertTrue(parser.interface_regex.matches("public interface MyIface"))
    assertTrue(parser.interface_regex.matches("private  interface  MyIface"))
    assertTrue(parser.interface_regex.matches("protected interface MyIface"))
    assertFalse(parser.interface_regex.matches("interface private MyIface"))
    assertFalse(parser.interface_regex.matches("security interface MyIface"))
    assertFalse(parser.interface_regex.matches("iface MyIface"))
}

@Test
fun `test import regexp`() {
    assertTrue(parser.import_regex.matches("import std;"))
    assertFalse(parser.import_regex.matches("import for_games/unity;"))
    assertFalse(parser.import_regex.matches("import for_games.unity;"))
}

@Test
fun `test use regexp`() {
    assertTrue(parser.use_regex.matches("use System;"))
    assertTrue(parser.use_regex.matches("use java.lang;"))
    assertFalse(parser.use_regex.matches("use java/lang;"))
}

@Test
fun `test include regexp`() {
    assertTrue(parser.include_regex.matches("include mscorlib;"))
    assertTrue(parser.include_regex.matches("include MyLib.dll;"))
    assertTrue(parser.include_regex.matches("include libs/MyLib.dll;"))
    assertTrue(parser.include_regex.matches("include libs\\MyLib.dll;"))
    assertFalse(parser.include_regex.matches("include \"mylib.dll\";"))
    assertFalse(parser.include_regex.matches("include 'mylib.dll';"))
    assertFalse(parser.include_regex.matches("include <mylib.dll>;"))
}

@Test
fun `test lib regexp`() {
    assertTrue(parser.lib_regex.matches("lib standart;"))
    assertTrue(parser.lib_regex.matches("lib <mylib>;"))
    assertTrue(parser.lib_regex.matches("lib <libs/mylib>;"))
    assertTrue(parser.lib_regex.matches("lib game\\engine;"))
}