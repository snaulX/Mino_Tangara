using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Tangara
{
    public class Platform
    {
        public string import = "import", start_block = "{", end_block = "}", class_keyword = "class", lib = "lib",
            function = "function", var_keyword = "var", const_keyword = "const", use = "use", if_keyword = "if",
            else_keyword = "else", switch_keyword = "switch", case_keyword = "case", break_keyword = "break",
            continue_keyword = "continue", while_keyword = "while", for_keyword = "for", try_keyword = "try",
            catch_keyword = "catch", attributte_start = "@", attribute_end = string.Empty, foreach_keyword = "foreach",
            end_expression = ";", string_start = "\"", string_end = "\"";

        public static Platform GetPlatformFromXML(string path)
        {
            Platform platform = new Platform();
            return platform;
        }

        public static Platform GetPlatformFromJson(string path)
        {
            Platform platform = new Platform();
            return platform;
        }

        public static Platform GetPlatformFromYaml(string path)
        {
            Platform platform = new Platform();
            return platform;
        }
    }
}
