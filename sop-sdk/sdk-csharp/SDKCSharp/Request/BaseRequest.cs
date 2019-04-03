using System;
using System.Collections.Generic;

using SDKCSharp.Common;
using SDKCSharp.Utility;

namespace SDKCSharp.Request
{
    /// <summary>
    /// 接口请求对象，新建的Request要继承这个类
    /// </summary>
    /// <typeparam name="T">对应的Response对象</typeparam>
    public abstract class BaseRequest<T>
    {
        private string method;
        private string format = SdkConfig.FORMAT_TYPE;
        private string charset = SdkConfig.CHARSET;
        private string signType = SdkConfig.SIGN_TYPE;
        private string timestamp = DateTime.Now.ToString(SdkConfig.TIMESTAMP_PATTERN);
        private string version;

        private string bizContent;
        private object bizModel;

        private List<UploadFile> files;

        public string Method { get => method; }

        public List<UploadFile> Files { set => files = value; }
        public string BizContent { set => bizContent = value; }
        public object BizModel { set => bizModel = value; }
        public string Version { get => version; set => version = value; }

        /// <summary>
        /// 返回接口名
        /// </summary>
        /// <returns></returns>
        public abstract string GetMethod();

        /// <summary>
        /// 返回版本号
        /// </summary>
        /// <returns></returns>
        public virtual string GetVersion()
        {
            return SdkConfig.DEFAULT_VERSION;
        }

        public BaseRequest()
        {
            this.method = this.GetMethod();
            this.version = this.GetVersion();
        }

        protected BaseRequest(string name, string version)
        {
            this.method = name;
            this.version = version == null ? SdkConfig.DEFAULT_VERSION : version;
        }


        /// <summary>
        /// 创建请求表单
        /// </summary>
        /// <returns></returns>
        public RequestForm CreateRequestForm(OpenConfig openConfig)
        {
            Dictionary<string, string> dict = new Dictionary<string, string>();
            dict[openConfig.MethodName] = this.Method;
            dict[openConfig.FormatName] = this.format;
            dict[openConfig.CharsetName] = this.charset;
            dict[openConfig.SignTypeName] = this.signType;
            dict[openConfig.TimestampName] = this.timestamp;
            dict[openConfig.VersionName] = this.version;

            // 业务参数
            String biz_content = buildBizContent();

            dict[openConfig.DataName] = biz_content;

            RequestForm requestForm = new RequestForm(dict);
            requestForm.Files = this.files;
            return requestForm;
        }

        protected string buildBizContent()
        {
            if (bizModel != null)
            {
                return JsonUtil.ToJSONString(bizModel);
            }
            else
            {
                return this.bizContent;
            }
        }





    }
}
