using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using TokensAPI;
using TokensAPI.identifers;
using TokensBuilder;

namespace Tangara
{
    class Program
    {
        static void Main(string[] args)
        {
            Console.WriteLine("Tangara started");
            Generator generator = new Generator();
            List<Expression> expressions = new List<Expression>();
            expressions.Add(new Expression
            {
                token = Token.DIRECTIVA,
                args = new List<Identifer>
                {
                    new SimpleIdentifer("enable"),
                    new SimpleIdentifer("Entrypoint")
                }
            });
            expressions.Add(new Expression
            {
                token = Token.DIRECTIVA,
                args = new List<Identifer>
                {
                    new SimpleIdentifer("version"),
                    new LongIdentifer("(1.0.0.3)")
                }
            });
            expressions.Add(new Expression
            {
                token = Token.DIRECTIVA,
                args = new List<Identifer>
                {
                    new SimpleIdentifer("company"),
                    new LongIdentifer("(snaulX)")
                }
            });
            expressions.Add(new Expression
            {
                token = Token.DIRECTIVA,
                args = new List<Identifer>
                {
                    new SimpleIdentifer("description"),
                    new LongIdentifer("(Tokens Application build with Tangara)")
                }
            });
            expressions.Add(new Expression
            {
                token = Token.DIRECTIVA,
                args = new List<Identifer>
                {
                    new SimpleIdentifer("title"),
                    new SimpleIdentifer("TokensAppliction")
                }
            });
            expressions.Add(new Expression
            {
                token = Token.USE,
                args = new List<Identifer>
                {
                    new SimpleIdentifer("System")
                }
            });
            expressions.Add(new Expression
            {
                token = Token.CLASS,
                args = new List<Identifer>
                {
                    new SimpleIdentifer("TokensApp"),
                    new SimpleIdentifer("Public")
                }
            });
            expressions.Add(new Expression
            {
                token = Token.ATTRIBUTE,
                args = new List<Identifer>
                {
                    new SimpleIdentifer("Entrypoint")
                }
            });
            expressions.Add(new Expression
            {
                token = Token.METHOD,
                args = new List<Identifer>
                {
                    new SimpleIdentifer("Main"),
                    new SimpleIdentifer("Static")
                }
            });
            expressions.Add(new Expression()); //add NULL
            expressions.Add(new Expression { token = Token.END, args = new List<Identifer>() });
            expressions.Add(new Expression { token = Token.END, args = new List<Identifer>() });
            generator.expressions = expressions;
            generator.Build("TokensApp", false);
            generator.CreatePE("TokensApp.exe");
        }
    }
}
