import com.snaulX.Tangara.Parser

//@Test
fun `simple program using import`() {
    val parser: Parser = Parser()
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