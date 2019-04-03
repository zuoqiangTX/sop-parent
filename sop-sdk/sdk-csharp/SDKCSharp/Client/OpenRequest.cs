using System;
using System.Web;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

using SDKCSharp.Common;
using SDKCSharp.Utility;
using SDKCSharp.Response;

namespace SDKCSharp.Client
{
    public class OpenRequest
    {
        private const string AND = "&";
        private const string EQ = "=";
        private const string UTF8 = "UTF-8";

        private const string HTTP_ERROR_CODE = "-400";

        private OpenConfig openConfig;
        private OpenHttp openHttp;

        public OpenRequest(OpenConfig openConfig)
        {
            this.openConfig = openConfig;
            this.openHttp = new OpenHttp(openConfig);
        }

        /// <summary>
        /// 请求服务器
        /// </summary>
        /// <param name="url">url</param>
        /// <param name="requestForm">请求表单信息</param>
        /// <param name="header">请求头</param>
        /// <returns></returns>
        public string Request(string url, RequestForm requestForm, Dictionary<string, string> header)
        {
            return this.doPost(url, requestForm, header);
        }

        public string doGet(string url, RequestForm requestForm, Dictionary<string, string> header)
        {
            StringBuilder queryString = new StringBuilder();
            Dictionary<string, string> form = requestForm.Form;
            Dictionary<string, string>.KeyCollection keys = form.Keys;
            foreach (string keyName in keys)
            {
                queryString.Append(AND).Append(keyName).Append(EQ)
                        .Append(HttpUtility.UrlEncode(form[keyName].ToString(), Encoding.UTF8));
            }

            string requestUrl = url + "?" + queryString.ToString().Substring(1);

            return this.openHttp.Get(requestUrl);

        }

        public string doPost(string url, RequestForm requestForm, Dictionary<string, string> header)
        {
            Dictionary<string, string> form = requestForm.Form;
            List<UploadFile> files = requestForm.Files;
            if (files != null && files.Count > 0)
            {
                return this.openHttp.PostFile(url, form, header, files);
            }
            else
            {
                return this.openHttp.PostJsonBody(url, JsonUtil.ToJSONString(form), header);
            }
        }

        public string PostJsonBody(string url, string json)
        {
            return this.openHttp.PostJsonBody(url, json, null);
        }

        protected string causeException(Exception e)
        {
            ErrorResponse result = new ErrorResponse();
            result.SubCode = HTTP_ERROR_CODE;
            result.SubMsg = e.Message;
            result.Code = HTTP_ERROR_CODE;
            result.Msg = e.Message;
            return JsonUtil.ToJSONString(result);
        }


    }

    class ErrorResponse : BaseResponse
    {
    }
}
