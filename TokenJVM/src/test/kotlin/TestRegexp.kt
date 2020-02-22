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