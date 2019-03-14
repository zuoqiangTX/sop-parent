/**
 * 请求工具
 */
var ApiUtil = (function () {
    // 接口URL,更改此处即可
    var url = 'http://localhost:8082/api';
    var URI_CHAR = '/';

    function formatUri(uri) {
        if (uri.substring(0, 1) !== URI_CHAR) {
            uri = URI_CHAR + uri;
        }
        if (uri.substring(uri.length - 1) !== URI_CHAR) {
            uri = uri + URI_CHAR;
        }
        return uri;
    }

    return {
        post: function (uri, params, callback) {
            uri = formatUri(uri);
            sdk.post({
                url: url + uri
                , data: params // 请求参数
                , callback: function (resp) { // 成功回调
                    callback(resp);
                }
            });
        }
    }
})();
