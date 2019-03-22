lib.config({
    base: '../../assets/lib/layuiext/module/'
}).extend({
    treetable: 'treetable-lay/treetable'
}).use(['element', 'table', 'form', 'treetable'], function () {
    var layer = layui.layer;
    var form = layui.form;
    var treetable = layui.treetable;
    var serverTable;

    // 渲染表格
    var renderTable = function () {
        layer.load(2);
        serverTable = treetable.render({
            treeColIndex: 1,
            treeSpid: 0,
            treeIdName: 'id',
            treePidName: 'parentId',
            treeDefaultClose: true,
            treeLinkage: false,
            elem: '#treeTable',
            url: ApiUtil.createUrl('service.instance.list'),
            page: false,
            cols: [[
                {type: 'numbers'},
                {field: 'name', title: '服务名称', width: 200},
                {field: 'instanceId', title: 'instanceId', width: 220},
                {field: 'ipAddr', title: 'IP地址', width: 150},
                {field: 'serverPort', title: '端口号', width: 100},
                {field: 'status', title: 'status', width: 100},
                {field: 'updateTime', title: '最后更新时间', width: 150},
                {fixed: 'right', templet: '#optCol', title: '操作', width: 200}
            ]],
            done: function () {
                layer.closeAll('loading');
            }
        });
    };

    renderTable();


    form.on('submit(searchFilter)', function(data){
        var param = data.field;
        serverTable.reload({
            where: {
                data: JSON.stringify(param)
            }
        })
        return false;
    });

});