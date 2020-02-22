import com.snaulX.Tangara.Parser
import org.junit.jupiter.api.Test

@Test
fun `simple program using import`() {
    parser.setCode("""
        import std;
        use System;
        Console.WriteLine("Hello World"); //print Hello World to console
        /*
        Testing simple code
        */
    """.trimIndent().lines())
    parser.parse()
}