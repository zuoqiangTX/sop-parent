/**
 * 请求工具
 */
var ApiUtil = (function () {
    // 接口URL,更改此处即可
    var url = SopConfig.url;
    var URI_CHAR = '/';
    var params = {};

    (function () {
        var aPairs, aTmp;
        var queryString = window.location.search.toString();
        queryString = queryString.substring(1, queryString.length); //remove   "?"
        aPairs = queryString.split("&");
        for (var i = 0; i < aPairs.length; i++) {
            aTmp = aPairs[i].split("=");
            params[aTmp[0]] = decodeURIComponent(aTmp[1]);
        }
    })();

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
                    var code = resp.code
                    if (!code || code === '-9') {
                        layer.alert('系统错误');
                        return
                    }
                    if (code === '-100' || code === '18' || code === '21') { // 未登录
                        ApiUtil.logout()
                        return
                    }
                    if (code === '0') { // 成功
                        callback(resp)
                    } else {
                        layer.alert(resp.msg);
                    }
                }
            });
        }
        , getUrl: function () {
            return url;
        }
        , createUrl: function (uri, params) {
            if (!uri) {
                throw new Error('uri不能为空');
            }
            return url + formatUri(uri) + (params ? '?data=' + encodeURIComponent(JSON.stringify(params)) : '');
        }
        , getParam: function (paramName) {
            return params[paramName];
        }
    }
})();
