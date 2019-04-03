using System;
using System.Collections;
using System.Collections.Generic;
using SDKCSharp.Client;
using SDKCSharp.Model;
using SDKCSharp.Request;
using SDKCSharp.Response;
using SDKCSharp.Utility;

namespace SDKTest
{
    class MainClass
    {
        static string url = "http://localhost:8081/api"; // zuul
        static string appId = "2019032617262200001";
        // 支付宝私钥
        static string privateKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCXJv1pQFqWNA/++OYEV7WYXwexZK/J8LY1OWlP9X0T6wHFOvxNKRvMkJ5544SbgsJpVcvRDPrcxmhPbi/sAhdO4x2PiPKIz9Yni2OtYCCeaiE056B+e1O2jXoLeXbfi9fPivJZkxH/tb4xfLkH3bA8ZAQnQsoXA0SguykMRZntF0TndUfvDrLqwhlR8r5iRdZLB6F8o8qXH6UPDfNEnf/K8wX5T4EB1b8x8QJ7Ua4GcIUqeUxGHdQpzNbJdaQvoi06lgccmL+PHzminkFYON7alj1CjDN833j7QMHdPtS9l7B67fOU/p2LAAkPMtoVBfxQt9aFj7B8rEhGCz02iJIBAgMBAAECggEARqOuIpY0v6WtJBfmR3lGIOOokLrhfJrGTLF8CiZMQha+SRJ7/wOLPlsH9SbjPlopyViTXCuYwbzn2tdABigkBHYXxpDV6CJZjzmRZ+FY3S/0POlTFElGojYUJ3CooWiVfyUMhdg5vSuOq0oCny53woFrf32zPHYGiKdvU5Djku1onbDU0Lw8w+5tguuEZ76kZ/lUcccGy5978FFmYpzY/65RHCpvLiLqYyWTtaNT1aQ/9pw4jX9HO9NfdJ9gYFK8r/2f36ZE4hxluAfeOXQfRC/WhPmiw/ReUhxPznG/WgKaa/OaRtAx3inbQ+JuCND7uuKeRe4osP2jLPHPP6AUwQKBgQDUNu3BkLoKaimjGOjCTAwtp71g1oo+k5/uEInAo7lyEwpV0EuUMwLA/HCqUgR4K9pyYV+Oyb8d6f0+Hz0BMD92I2pqlXrD7xV2WzDvyXM3s63NvorRooKcyfd9i6ccMjAyTR2qfLkxv0hlbBbsPHz4BbU63xhTJp3Ghi0/ey/1HQKBgQC2VsgqC6ykfSidZUNLmQZe3J0p/Qf9VLkfrQ+xaHapOs6AzDU2H2osuysqXTLJHsGfrwVaTs00ER2z8ljTJPBUtNtOLrwNRlvgdnzyVAKHfOgDBGwJgiwpeE9voB1oAV/mXqSaUWNnuwlOIhvQEBwekqNyWvhLqC7nCAIhj3yvNQKBgQCqYbeec56LAhWP903Zwcj9VvG7sESqXUhIkUqoOkuIBTWFFIm54QLTA1tJxDQGb98heoCIWf5x/A3xNI98RsqNBX5JON6qNWjb7/dobitti3t99v/ptDp9u8JTMC7penoryLKK0Ty3bkan95Kn9SC42YxaSghzqkt+uvfVQgiNGQKBgGxU6P2aDAt6VNwWosHSe+d2WWXt8IZBhO9d6dn0f7ORvcjmCqNKTNGgrkewMZEuVcliueJquR47IROdY8qmwqcBAN7Vg2K7r7CPlTKAWTRYMJxCT1Hi5gwJb+CZF3+IeYqsJk2NF2s0w5WJTE70k1BSvQsfIzAIDz2yE1oPHvwVAoGAA6e+xQkVH4fMEph55RJIZ5goI4Y76BSvt2N5OKZKd4HtaV+eIhM3SDsVYRLIm9ZquJHMiZQGyUGnsvrKL6AAVNK7eQZCRDk9KQz+0GKOGqku0nOZjUbAu6A2/vtXAaAuFSFx1rUQVVjFulLexkXR3KcztL1Qu2k5pB6Si0K/uwQ=";


        // 声明一个就行
        static OpenClient client = new OpenClient(url, appId, privateKey);

        public static void Main(string[] args)
        {
            TestGet();
        }

        // 标准用法
        private static void TestGet()
        {
            // 创建请求对象
            GetStoryRequest request = new GetStoryRequest();
            // 请求参数
            GetStoryModel model = new GetStoryModel();
            model.Name = "白雪公主";
            request.BizModel = model;

            // 发送请求
            GetStoryResponse response = client.Execute(request);

            if (response.IsSuccess())
            {
                // 返回结果
                Console.WriteLine("故事名称:{0}", response.Name);
            }
            else
            {
                Console.WriteLine("错误, code:{0}, msg:{1}, subCode:{2}, subMsg:{3}", 
                    response.Code, response.Msg, response.SubCode, response.SubMsg);
            }
        }

        // 懒人版，如果不想添加Request,Response,Model。可以用这种方式，返回全部是String，后续自己处理json
        private static void TestCommon()
        {
            // 创建请求对象
            CommonRequest request = new CommonRequest("alipay.story.find");
            // 请求参数
            Dictionary<string, string> bizModel = new Dictionary<string, string>
            {
                ["name"] = "白雪公主"
            };

            request.BizModel = bizModel;

            // 发送请求
            CommonResponse response = client.Execute(request);

            if (response.IsSuccess())
            {
                // 返回结果
                string body = response.Body;
                Dictionary<string, object> dict = JsonUtil.ParseToDictionary(body);
                Console.WriteLine(dict.ToString());
            }
            else
            {
                Console.WriteLine("错误, code:{0}, msg:{1}, subCode:{2}, subMsg:{3}",
                    response.Code, response.Msg, response.SubCode, response.SubMsg);
            }
        }
    }
}
