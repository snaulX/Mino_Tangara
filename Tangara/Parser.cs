using System;
using System.Collections.Generic;
using System.Linq.Expressions;
using System.Text;
using System.Threading.Tasks;
using Tangara.Errors;
using TokensBuilder;

namespace Tangara
{
    public class Parser
    {
        public List<TangaraError> errors = new List<TangaraError>();
        public List<TangaraWarning> warnings = new List<TangaraWarning>();
        private Generator gen = new Generator();
        private int pos = 0;
        private char cur = '\0';
        private StringBuilder buffer = new StringBuilder();
        private string code = "";
        public Platform platform = new Platform();

        void SkipWhitespaces()
        {
            while (char.IsWhiteSpace(cur))
            {
                try
                {
                    cur = code[++pos];
                }
                catch
                {
                    return;
                }
            }
        }

        public void CreateError(TangaraError error) => errors.Add(error);
        public void CreateWarning(TangaraWarning warning) => warnings.Add(warning);

        public Generator Parse(string input, Config config)
        {
            code = input;
            cur = code[0];
            SkipWhitespaces();
            if (cur == ';')
            {
                CreateWarning(new TangaraWarning(0, 0, "Empty expression"));
            }
            return gen;
        }

        public void ImportPlatform()
        {
            //pass
        }

        public void UseDirective()
        {
            SkipWhitespaces();
            while (!char.IsWhiteSpace(cur))
            {
                try
                {
                    buffer.Append(cur);
                    cur = code[++pos];
                }
                catch
                {
                    break;
                }
            }
            string directive = buffer.ToString();
        }

        public void ReadString()
        {
            SkipWhitespaces();
            if (cur.ToString() == platform.string_start)
            {
                //pass
            }
            else
            {
                CreateError(new SyntaxError(0, 0, "Start of string not found"));
            }
        }
    }
}
