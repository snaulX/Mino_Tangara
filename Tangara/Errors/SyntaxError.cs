using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Tangara.Errors
{
    public class SyntaxError : TangaraError
    {
        public SyntaxError(int line, int position, string message) : base(line, position, message)
        {
        }
    }
}
