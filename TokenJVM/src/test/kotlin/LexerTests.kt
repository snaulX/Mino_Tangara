import com.snaulX.Tangara.Parser
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*

val parser: Parser = Parser()

@Test
fun `test lexerize of simple program`() {
    parser.code = """
        import std;
        lib standart; //line
    """.trimIndent()
    parser.lexerize()
    assertEquals(parser.lexemes, listOf<String>("import", "std", ";", "\n",
        "lib", "standart", ";", "//", "line", "\n"))
}