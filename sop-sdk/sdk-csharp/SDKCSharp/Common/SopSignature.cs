using System;
using System.Collections;
using System.Collections.Generic;
using System.Text;

namespace SDKCSharp.Common
{
    public class SopSignature
    {
        public SopSignature()
        {
        }

        public static String getSignContent(Dictionary<string, string> form)
        {
            StringBuilder content = new StringBuilder();
            List<string> keys = new List<string>(form.Keys);

            keys.Sort();
            int index = 0;
            for (int i = 0; i < keys.Count; i++)
            {
                string key = keys[i];
                string value = form[key];
                if (!string.IsNullOrEmpty(key) && !string.IsNullOrEmpty(value))
                {
                    content.Append((index == 0 ? "" : "&") + key + "=" + value);
                    index++;
                }
            }
            return content.ToString();
        }
    }
}
