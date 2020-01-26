using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Tangara
{
    public class Config
    {
        public Config()
        {
            //pass
        }

        public static Config GetConfigFromXML(string path)
        {
            Config config = new Config();
            return config;
        }
    }
}
