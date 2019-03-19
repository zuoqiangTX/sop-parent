lib.use(['element', 'table', 'tree', 'form'], function () {
    var form = layui.form;
    var table = layui.table;
    var currentServiceId;
    var routeTable;
    var profile = window.profile;

    form.on('submit(searchFilter)', function (data) {
        var param = data.field;
        param.serviceId = currentServiceId;
        searchTable(param)
        return false;
    });

    //监听提交
    form.on('submit(updateWinSubmitFilter)', function(data) {
        ApiUtil.post('route.update', data.field, function (resp) {
            layer.closeAll();
            routeTable.reload();
        })
        return false;
    });

    function initTree() {
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
        $('#optTip').hide();
        $('#rightPart').show();
        var serviceId = node.name;
        currentServiceId = serviceId;
        searchTable({
            serviceId: serviceId
        });
    }

    /**
     * 查询表格
     * @param params
     */
    function searchTable(params) {
        var postData = {
            data: JSON.stringify(params)
            , profile: profile
        };

        if (!routeTable) {
            routeTable = table.render({
                elem: '#routeTable'
                , url: ApiUtil.createUrl('route.list')
                , where: postData
                , cellMinWidth: 80 //全局定义常规单元格的最小宽度，layui 2.2.1 新增
                , cols: [[
                    {field: 'id', title: 'id(接口名+版本号)', width: 250}
                    , {field: 'uri', title: 'uri', width: 200}
                    , {field: 'path', title: 'path', width: 200}
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

            //监听单元格事件
            table.on('tool(routeTable)', function(obj) {
                var data = obj.data;
                if(obj.event === 'edit'){
                    //表单初始赋值
                    data.disabled = data.disabled + '';
                    data.ignoreValidate = data.ignoreValidate + '';
                    data.serviceId = currentServiceId;
                    data.profile = profile;
                    form.val('updateWinFilter', data)

                    layer.open({
                        type: 1
                        ,title: '修改路由'
                        ,area: ['500px', '400px']
                        ,content: $('#updateWin') //这里content是一个DOM，注意：最好该元素要存放在body最外层，否则可能被其它的相对元素所影响
                    });
                }
            });
        } else {
            routeTable.reload({
                where: postData
            })
        }
    }

    initTree();

});