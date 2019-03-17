lib.use(['element', 'table', 'tree', 'form'], function () {
    var form = layui.form;
    var table = layui.table;
    var currentServiceId;

    var serverTable = table.render({
        elem: '#table'
        , url: ApiUtil.createUrl('route.list')
        , cellMinWidth: 80 //全局定义常规单元格的最小宽度，layui 2.2.1 新增
        , cols: [[
            {field: 'id', title: 'id(接口名+版本号)'}
            , {field: 'uri', title: 'uri'}
            , {
                field: 'ignoreValidate', width: 80, title: '忽略验证', templet: function (row) {
                    return row.ignoreValidate
                        ? '<span class="x-red">是</span>'
                        : '<span>否</span>';
                }
            }
            , {
                field: 'disabled', title: '状态', width: 80, templet: function (row) {
                    return row.disabled
                        ? '<span class="x-red">禁用</span>'
                        : '<span class="x-green">启用</span>';
                }
            }
            , {fixed: 'right', title: '操作', toolbar: '#optBar', width: 100}
        ]]
    });

    $('#profileList').find('li').on('click', function () {
        initTree($(this).text());
    })

    function initTree(profile) {
        ApiUtil.post('service.list', {profile: profile}, function (resp) {
            var serviceList = resp.data;
            var children = [];
            for (var i = 0; i < serviceList.length; i++) {
                var serviceInfo = serviceList[i];
                children.push({
                    id: i + 1,
                    name: serviceInfo.serviceId
                })
            }
            // 清空表格
            reloadRightPart({name: ''})
            // 清空树
            $('#leftTree').text('');
            layui.tree({
                elem: '#leftTree' //传入元素选择器
                , nodes: [{ //节点
                    name: '服务列表(' + profile + ')'
                    , spread: true // 展开
                    , children: children
                }]
                , click: function (node) {
                    if (node.id) {
                        reloadRightPart(node)
                    }
                }
            });
        });
    }

    /**
     * 更新右边部分
     * @param node 树节点
     */
    function reloadRightPart(node) {
        var serviceId = node.name;
        currentServiceId = serviceId;
        searchTable({
            serviceId: serviceId
        });
    }

    form.on('submit(searchFilter)', function (data) {
        var param = data.field;
        param.serviceId = currentServiceId;
        searchTable(param)
        return false;
    });

    function searchTable(param) {
        serverTable.reload({
            where: {
                data: JSON.stringify(param)
                , profile: window.profile
            }
        })
    }

    initTree('default');
});