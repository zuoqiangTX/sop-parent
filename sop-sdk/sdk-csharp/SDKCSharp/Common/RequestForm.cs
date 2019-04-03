using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SDKCSharp.Common
{
    public class RequestForm
    {
        
        private Dictionary<string, string> form;

        /// <summary>
        /// 请求表单内容
        /// </summary>
        public Dictionary<string, string> Form
        {
            get { return form; }
            set { form = value; }
        }

        private List<UploadFile> files;

        /// <summary>
        /// 上传文件
        /// </summary>
        public List<UploadFile> Files
        {
            get { return files; }
            set { files = value; }
        }

        public RequestForm(Dictionary<string, string> form)
        {
            this.form = form;
        }

    }
}
