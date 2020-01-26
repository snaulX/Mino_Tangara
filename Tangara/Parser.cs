using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using TokensBuilder;

namespace Tangara
{
    public class Parser
    {
        public Platform platform;
        
        public Parser()
        {
            platform = new Platform();
        }

        public Generator Parse(string code, Config config)
        {
            Generator gen = new Generator();
            return gen;
        }
    }
}
