using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Tangara
{
    public class TangaraError
    {
        public string message;
        public int line, position;

        public TangaraError(int line, int position, string message)
        {
            this.line = line;
            this.position = position;
            this.message = message;
        }
    }
}
