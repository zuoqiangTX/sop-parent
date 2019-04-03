using System;
using System.Web;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Collections;
using System.Threading.Tasks;

using SDKCSharp.Common;
using SDKCSharp.Request;
using SDKCSharp.Response;
using SDKCSharp.Utility;

namespace SDKCSharp.Client
{
    /// <summary>
    /// 客户端
    /// </summary>
    public class OpenClient
    {

        private static OpenConfig DEFAULT_CONFIG = new OpenConfig();

        private static char DOT = '.';
        private static char UNDERLINE = '_';
        public static String DATA_SUFFIX = "_response";

        private Dictionary<string, string> header = new Dictionary<string, string>();


        private String url;
        private String appId;
        private String privateKey;

        private OpenConfig openConfig;
        private OpenRequest openRequest;


        public OpenClient(string url, string appId, string privateKey) : this(url, appId, privateKey, DEFAULT_CONFIG)
        {
            
        }

        public OpenClient(string url, string appId, string privateKey, OpenConfig openConfig)
        {
            this.url = url;
            this.appId = appId;
            this.privateKey = privateKey;
            this.openConfig = openConfig;
            this.openRequest = new OpenRequest(openConfig);
        }

        /// <summary>
        /// 发送请求
        /// </summary>
        /// <typeparam name="T">返回的Response类</typeparam>
        /// <param name="request">请求对象</param>
        /// <returns>返回Response类</returns>
        public virtual T Execute<T>(BaseRequest<T> request) where T : BaseResponse
        {
            return this.Execute<T>(request, null);
        }

        /// <summary>
        /// 发送请求
        /// </summary>
        /// <typeparam name="T">返回的Response类</typeparam>
        /// <param name="request">请求对象</param>
        /// <param name="accessToken">accessToken</param>
        /// <returns>返回Response类</returns>
        public virtual T Execute<T>(BaseRequest<T> request, string accessToken) where T : BaseResponse
        {
            RequestForm requestForm = request.CreateRequestForm(this.openConfig);
            Dictionary<string, string> form = requestForm.Form;
            if (!string.IsNullOrEmpty(accessToken))
            {
                form[this.openConfig.AccessTokenName] = accessToken;
            }
            form[this.openConfig.AppKeyName] = this.appId;
            string content = SopSignature.getSignContent(form);
            string sign = SignUtil.CreateSign(form, privateKey, request.Charset, request.SignType);
            form[this.openConfig.SignName] = sign;

            string resp = this.doExecute(url, requestForm, header);

            return this.parseResponse<T>(resp, request);
        }

        /// <summary>
        /// 执行请求
        /// </summary>
        /// <param name="url">请求url</param>
        /// <param name="requestForm">请求内容</param>
        /// <param name="header">请求header</param>
        /// <returns>返回服务器响应内容</returns>
        protected virtual String doExecute(String url, RequestForm requestForm, Dictionary<string, string> header)
        {
            return openRequest.Request(this.url, requestForm, header);
        }

        /// <summary>
        /// 解析返回结果
        /// </summary>
        /// <typeparam name="T">返回的Response</typeparam>
        /// <param name="resp">服务器响应内容</param>
        /// <param name="request">请求Request</param>
        /// <returns>返回Response</returns>
        protected virtual T parseResponse<T>(string resp, BaseRequest<T> request) where T: BaseResponse {
            string method = request.Method;
            string dataName = method.Replace(DOT, UNDERLINE) + DATA_SUFFIX;
            Dictionary<string, object> jsonObject = JsonUtil.ParseToDictionary(resp);
            object data = jsonObject[dataName];
            string jsonData = data == null ? "{}" : data.ToString();           
            T t = JsonUtil.ParseObject<T>(jsonData);
            t.Body = jsonData;
            return t;
        }


    }
}
