lib.importJs('../../assets/js/profile.js').use(['element', 'table', 'tree', 'form'], function () {
    var ROUTE_STATUS = {
        '0': '待审核'
        ,'1': '<span class="x-green">已启用</span>'
        ,'2': '<span class="x-red">已禁用</span>'
    }
    var profile = window.profile;
    var form = layui.form;
    var updateForm = layui.Form('updateForm');
    var addForm = layui.Form('addForm');
    var table = layui.table;

    var currentServiceId;
    var routeTable;
    var smTitle;


    form.on('submit(searchFilter)', function (data) {
        var param = data.field;
        param.serviceId = currentServiceId;
        searchTable(param)
        return false;
    });

    // 监听修改提交
    form.on('submit(updateWinSubmitFilter)', function(data) {
        ApiUtil.post('route.update', data.field, function (resp) {
            layer.closeAll();
            routeTable.reload();
        })
        return false;
    });

    // 监听保存提交
    form.on('submit(addWinSubmitFilter)', function(data) {
        ApiUtil.post('route.add', data.field, function (resp) {
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
        smTitle = '[ <strong>profile：</strong>' + profile + '&nbsp;&nbsp;&nbsp;&nbsp;<strong>serviceId：</strong>' + currentServiceId + ' ]';
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
            routeTable = initTable(postData);
        } else {
            routeTable.reload({
                where: postData
            })
        }
    }

    function initTable(postData) {
        var routeTable = table.render({
            elem: '#routeTable'
            , toolbar: '#toolbar'
            , url: ApiUtil.createUrl('route.list')
            , where: postData
            , cellMinWidth: 80 //全局定义常规单元格的最小宽度，layui 2.2.1 新增
            , cols: [[
                {field: 'id', title: 'id(接口名+版本号)', width: 250}
                , {field: 'uri', title: 'uri', width: 200}
                , {field: 'path', title: 'path', width: 200}
                , {
                    field: 'ignoreValidate', width: 80, title: '忽略验证', templet: function (row) {
                        return row.ignoreValidate ? '<span class="x-red">是</span>' : '<span>否</span>';
                    }
                }
                , {
                    field: 'mergeResult', title: '合并结果', width: 80, templet: function (row) {
                        return row.mergeResult ? '合并' : '<span class="x-yellow">不合并</span>';
                    }
                }
                , {
                    field: 'status', title: '状态', width: 80, templet: function (row) {
                        return ROUTE_STATUS[row.status + ''];
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
                data.profile = profile;
                data.serviceId = currentServiceId;

                updateForm.setData(data);

                // form.val('updateWinFilter', data);

                layer.open({
                    type: 1
                    ,title: '修改路由' + smTitle
                    ,area: ['500px', '460px']
                    ,content: $('#updateWin') //这里content是一个DOM，注意：最好该元素要存放在body最外层，否则可能被其它的相对元素所影响
                });
            }
        });
        table.on('toolbar(routeTable)', function(obj) {
            if (obj.event === 'add') {
                var data = {};
                data.profile = profile;
                data.serviceId = currentServiceId;
                data.id = '';
                // 新加的路由先设置成禁用
                data.status = 2;
                data.ignoreValidate = 0;
                data.mergeResult = 1;
                addForm.setData(data);
                layer.open({
                    type: 1
                    ,title: '添加路由' + smTitle
                    ,area: ['500px', '460px']
                    ,content: $('#addWin')
                });
            }
        });
        return routeTable;
    }

    initTree();

});