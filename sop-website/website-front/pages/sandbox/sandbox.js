
docEvent.bind(function (docItem,layui) {
    selectItem(docItem, layui);
});

layui.use('form', function(){
    var form = layui.form;
    //监听提交
    form.on('submit(formSend)', function(data){
        doTest();
        return false;
    });
});

var $body = $('body');
var treetable;
var currentItem;

function selectItem(docItem, layui) {
    currentItem = docItem;
    resetResultDiv();
    var nameVersion = docItem.nameVersion;
    treetable = treetable || layui.treetable;
    $('.sop-name').text(docItem.name);
    $('.sop-version').text(docItem.version);
    $('.sop-summary').text(docItem['summary']);
    $('.sop-description').text(docItem.description || docItem['summary']);

    createRequestParameter(docItem);

    var $li = $('#docItemTree').find('li[nameversion="'+nameVersion+'"]');
    $li.addClass('layui-this').siblings().removeClass('layui-this');
    InputCache.init();
}

function createRequestParameter(docItem) {
    var data = buildTreeData(docItem.requestParameters);
    createTreeTable('treeTableReq', data);
}

function buildTreeData(parameters, parentId) {
    var data = [];
    parentId = parentId || 0;
    for (var i = 0; i < parameters.length; i++) {
        var parameter = parameters[i];
        parameter.id = parentId * 100 + (i + 1);
        parameter.parentId = parentId;
        data.push(parameter);
        var refs = parameter.refs;
        if (refs && refs.length > 0) {
            var childData = buildTreeData(refs, parameter.id);
            data = data.concat(childData);
        }
    }
    return data;
}

function createTreeTable(id, data) {
    var el = '#' + id;
    treetable.render({
        elem: el,
        treeColIndex: 0,
        treeSpid: 0,
        treeIdName: 'id',
        treePidName: 'parentId',
        treeDefaultClose: false,
        treeLinkage: false,
        data: data,
        page: false,
        firstTemplet: function (row) {
            var required = row.required ? '<span style="color: red;">*</span> ' : '';
            return required + row.name;
        },
        cols: [[
            {field: 'name', title: '参数',width: 200}
            ,{field: 'val', title: '值', width: 300, templet:function (row) {
                var id = currentItem.nameVersion + '-' + row.name;
                var requiredTxt = row.required ? 'required  lay-verify="required"' : '';
                var module = row.module;
                var attrs = [
                    'id="' + id + '"'
                    , 'name="'+row.name+'"'
                    , 'class="layui-input test-input"'
                    , 'type="text"'
                    , requiredTxt
                    , 'module="'+module+'"'
                ];

                return !row.refs ? '<input ' + attrs.join(' ') + '/>' : '';
            }}
            ,{field: 'description', title: '描述'}
        ]]
    });
}

function doTest() {
    var method = currentItem.name;
    var version = currentItem.version;
    var data = {
        appId: $('#appId').val(),
        privateKey: $('#privateKey').val(),
        method: method,
        version: version
    };
    var $inputs = $body.find('.test-input');
    var bizContent = {};
    $inputs.each(function () {
        var module = $(this).attr('module');
        if (module) {
            if (!bizContent[module]) {
                bizContent[module] = {};
            }
            var moduleObj = bizContent[module];
            moduleObj[this.name] = this.value;
        } else {
            bizContent[this.name] = this.value;
        }
    });
    data.bizContent = JSON.stringify(bizContent);
    $.ajax({
        url: SopConfig.url + '/sandbox/test'
        , dataType: 'json'
        , data: data
        , method: 'post'
        , success: function (resp) {
            setReqInfo(resp);
            showRespnfo(resp.apiResult);
        }
        , error: function (xhr,status,error) {
            // {"timestamp":"2019-06-19 15:57:36","status":500,"error":"Internal Server Error","message":"appId不能为空","path":"/sandbox/test"}
            var errorData = xhr.responseJSON;
            if (errorData) {
                setReqInfo('');
                showRespnfo(errorData.message);
            }
        }
    });
}

function showRespnfo(info) {
    var json = formatJson(info);
    setRespInfo(json);
    $('#resultDiv').show();
}

function setReqInfo(resp) {
    var txt = '';
    if (resp) {
        var html = [];
        html.push('【请求参数】：' + resp.params);
        html.push('【待签名内容】：' + resp.beforeSign);
        html.push('【签名(sign)】：' + resp.sign);
        txt = html.join('\r\n')
    }
    $('#req-info-result').val(txt);
}

function setRespInfo(info) {
    $('#resp-info-result').text(info);
}

function resetResultDiv() {
    setReqInfo({
        beforeSign: '',
        params: '',
        privateKey: '',
        sign: ''
    });
    setRespInfo('');
    $('.resp-info-content')
        .addClass('layui-show')
        .siblings().removeClass('layui-show');

    $('.resp-info')
        .addClass('layui-this')
        .siblings().removeClass('layui-this');

    $('#resultDiv').hide();
}
