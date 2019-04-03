using System;
using System.Collections;
using System.Collections.Generic;

namespace SDKCSharp.Utility
{
    /// <summary>
    /// 签名工具类
    /// </summary>
    public class SignUtil
    {

        /// <summary>
        /// 构建签名。
        /// </summary>
        /// <param name="parameters">参数.</param>
        /// <param name="privateKeyPem">私钥.</param>
        /// <param name="charset">字符集.</param>
        /// <param name="signType">签名类型.</param>
        /// <returns>返回签名.</returns>
        public static string CreateSign(IDictionary<string, string> parameters, string privateKeyPem, string charset, bool isFromFile, string signType)
        {
            return AlipaySignature.RSASign(parameters, privateKeyPem, charset, isFromFile, signType);
        }

    }
}
