using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Tangara.Errors
{
    public class ImportError : TangaraError
    {
        public ImportError(int line, int position, string message) : base(line, position, message)
        {

        }
    }
}
