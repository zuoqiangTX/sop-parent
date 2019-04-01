lib.importJs('../../assets/js/routerole.js')
    .use(['element', 'table', 'tree', 'form'], function () {
    var ROUTE_STATUS = {
        '0': '待审核'
        ,'1': '<span class="x-green">已启用</span>'
        ,'2': '<span class="x-red">已禁用</span>'
    }
    var form = layui.form;
    var updateForm = layui.Form('updateForm');
    var addForm = layui.Form('addForm');
    var authForm = layui.Form('authForm');
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

    form.on('submit(authFormSubmitFilter)', function(data) {
        var params = authForm.getData();
        ApiUtil.post('route.role.update', params, function (resp) {
            layer.closeAll();
            routeTable.reload();
        })
        return false;
    });


    function initTree() {
        ApiUtil.post('service.list', {}, function (resp) {
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
                    name: '服务列表'
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
        smTitle = '[ <strong>serviceId：</strong>' + currentServiceId + ' ]';
    }

    /**
     * 查询表格
     * @param params
     */
    function searchTable(params) {
        var postData = {
            data: JSON.stringify(params)
        };
        if (!routeTable) {
            routeTable = renderTable(postData);
        } else {
            routeTable.reload({
                where: postData
            })
        }
    }

    function renderTable(postData) {
        var routeTable = table.render({
            elem: '#routeTable'
            , toolbar: '#toolbar'
            , url: ApiUtil.createUrl('route.list')
            , where: postData
            , cellMinWidth: 80 //全局定义常规单元格的最小宽度，layui 2.2.1 新增
            , cols: [[
                {field: 'id', title: 'id(接口名+版本号)'}
                , {field: 'uri', title: 'uri', width: 200}
                , {field: 'path', title: 'path'}
                , {field: 'roles', title: '访问权限', width: 100, templet: function (row) {
                        if (!row.permission) {
                            return '（公开）';
                        }
                        var html = [];
                        var roles = row.roles;
                        for (var i = 0; i < roles.length; i++) {
                            html.push(roles[i].description);
                        }
                        return html.length > 0 ? html.join(', ') : '<span class="x-red">未授权</span>';
                }}
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
                , {
                    fixed: 'right', title: '操作', width: 150, templet: function (row) {
                        var html = ['<a class="layui-btn layui-btn-xs layui-btn-normal" lay-event="edit">修改</a>'];
                        if (row.permission) {
                            html.push('<a class="layui-btn layui-btn-xs layui-btn-normal" lay-event="auth">授权</a>');
                        }
                        return html.join('');
                    }
                }
            ]]
        });

        //监听单元格事件
        table.on('tool(routeTableFilter)', function(obj) {
            var data = obj.data;
            var event = obj.event;
            if(event === 'edit'){
                //表单初始赋值
                data.serviceId = currentServiceId;

                updateForm.setData(data);

                layer.open({
                    type: 1
                    ,title: '修改路由' + smTitle
                    ,area: ['500px', '460px']
                    ,content: $('#updateWin') //这里content是一个DOM，注意：最好该元素要存放在body最外层，否则可能被其它的相对元素所影响
                });
            } else if (event === 'auth') {
                ApiUtil.post('route.role.get', {id: data.id}, function (resp) {
                    var roleList = resp.data;
                    var roleCode = [];
                    for (var i = 0; i < roleList.length; i++) {
                        roleCode.push(roleList[i].roleCode);
                    }
                    authForm.setData({
                        routeId: data.id
                        , roleCode: roleCode
                    })
                    layer.open({
                        type: 1
                        ,title: '路由授权'
                        ,area: ['500px', '260px']
                        ,content: $('#authWin')
                    });
                })
            }
        });
        table.on('toolbar(routeTableFilter)', function(obj) {
            if (obj.event === 'add') {
                var data = {};
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

    RouteRole.loadAllRole(form, 'roleArea');

});