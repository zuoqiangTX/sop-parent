var Common = (function () {

    function initProfiles() {
        var $profileList = $('#profileList');
        if ($profileList.length > 0) {
            ApiUtil.post('system.profile.list', {}, function (resp) {
                var list = resp.data;
                var html = [];
                for (var i = 0; i < list.length; i++) {
                    html.push('<li' + (i == 0 ? ' class="layui-this"' : '') + '>' + list[i] + '</li>');
                }
                $profileList.html(html.join(''))

                $profileList.find('li').on('click', function () {
                    window.profile = $(this).text();
                });
            });
        }
    }

    initProfiles();

    return {}
})();