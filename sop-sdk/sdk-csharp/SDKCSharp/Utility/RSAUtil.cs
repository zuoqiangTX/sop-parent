using System;
using System.Xml;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Security.Cryptography;
using System.Text;

using Org.BouncyCastle.Asn1.Pkcs;
using Org.BouncyCastle.Asn1.X509;
using Org.BouncyCastle.Crypto.Generators;
using Org.BouncyCastle.Crypto.Parameters;
using Org.BouncyCastle.Math;
using Org.BouncyCastle.Pkcs;
using Org.BouncyCastle.Security;
using Org.BouncyCastle.Crypto.Engines;
using Org.BouncyCastle.X509;
using Org.BouncyCastle.Crypto;
using Org.BouncyCastle.Asn1;
using Org.BouncyCastle.Crypto.Encodings;

namespace SDKCSharp.Utility
{
    public class RSAUtil
    {

        static Encoding UTF8 = Encoding.UTF8;
        static RSA rsa = new RSA();

        /// <summary>
        /// 私钥加密
        /// </summary>
        /// <returns>The by private key.</returns>
        /// <param name="data">内容.</param>
        /// <param name="privateKey">私钥.</param>
        public static string EncryptByPrivateKey(string data, string privateKey)
        {
            return rsa.EncryptByPrivateKey(data, privateKey);
        }


    }




}
