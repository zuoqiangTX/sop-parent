;(function () {
    var currentProfile = ApiUtil.getParam('profile') || 'default';

    ApiUtil.post('system.profile.list', {}, function (resp) {
        var profileList = resp.data;
        var html = ['<div class="layui-tab layui-tab-brief" style="margin-top: 0px;">'];
        html.push('<ul id="profileList" class="layui-tab-title">')
        for (var i = 0; i < profileList.length; i++) {
            var profile = profileList[i];
            var cls = currentProfile == profile ? 'layui-this' : '';
            html.push('<li><a class="' + cls + '" href="' + getCurrentPage(profile) + '">' + profile + '</a></li>');
        }
        html.push('</ul>')
        html.push('</div>')
        $('.x-body').prepend(html.join(''));
    });

    function getCurrentPage(profile) {
        var currentUrl = location.href.toString();
        var indexStart = currentUrl.lastIndexOf('/') + 1;
        var indexEnd = currentUrl.lastIndexOf('?');
        var page = indexEnd > -1
            ? currentUrl.substring(indexStart, indexEnd)
            : currentUrl.substring(indexStart);

        return page + '?q=' + new Date().getTime() + '&profile=' + profile;
    }

    window.profile = currentProfile;
})();