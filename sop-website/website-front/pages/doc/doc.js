layui.use(['element', 'form'], function(){ //加载code模块
    var form = layui.form;

    // key:module
    var docItemStore = {};
    
    function initDocModules() {
        $.getJSON(SopConfig.url + '/doc/getDocBaseInfo', function (baseInfo) {
            var html = [];
            var docInfoList = baseInfo.docInfoList;
            for (var i = 0; i < docInfoList.length; i++) {
                var docInfo = docInfoList[i];
                var selected = i === 0 ? 'selected="selected"' : '';
                var title = docInfo.title;
                html.push('<option value="' + title + '" ' + selected + '>' + title + '</option>');
            }
            $('#moduleList').html(html.join(''));
            $('.url-prod').text(baseInfo.urlProd);
            form.render('select');

            if (docInfoList && docInfoList.length > 0) {
                selectDocInfo(docInfoList[0].title);
            }
        })
    }
    
    function selectDocInfo(title) {
        $.getJSON(SopConfig.url + '/doc/docinfo/' + title, function (docInfo) {
            var moduleList = docInfo.docModuleList;
            var html = [];
            var firstItem;
            for (var j = 0; j < moduleList.length; j++) {
                var module = moduleList[j];
                var docItems = module.docItems;
                html.push('<li><h2>' + module.module + '</h2></li>');
                for (var i = 0; i < docItems.length; i++) {
                    var docItem = docItems[i];
                    var first = j == 0 && j == 0;
                    if (first) {
                        firstItem = docItem;
                    }
                    docItemStore[docItem.nameVersion] = docItem;
                    /*
                    <li class="site-tree-noicon layui-this">
                    <a href="/">
                        <cite>统一收单交易退款查询</cite>
                    </a>
                </li>
                     */
                    html.push('<li class="site-tree-noicon" nameversion="'+docItem.nameVersion+'">');
                    html.push('<a href="#"><cite>'+docItem.summary+'</cite></a>')
                }
            }

            $('#docItemTree').html(html.join(''));
            if (firstItem) {
                selectDocItem(firstItem.nameVersion);
            }
        })
    }

    function initEvent() {
        form.on('select(moduleListFilter)', function (data) {
            selectDocInfo(data.value);
        })
        $('#docItemTree').on('click', 'li', function () {
            var $li = $(this);
            selectDocItem($li.attr('nameversion'));
        })
    }

    function selectDocItem(nameVersion) {
        var docItem = docItemStore[nameVersion];
        $('.sop-name').text(docItem.name);
        $('.sop-version').text(docItem.version);
        $('.sop-summary').text(docItem.summary);
        $('.sop-description').text(docItem.description || docItem.summary);

        createRequestParameter(docItem);
        createResponseParameter(docItem);
        createResponseCode(docItem);

        var $li = $('#docItemTree').find('li[nameversion="'+nameVersion+'"]');
        $li.addClass('layui-this').siblings().removeClass('layui-this');
    }

    function createRequestParameter(docItem) {
        var html = createParameterBody(docItem.requestParameters);
        $('#requestTbody').html(html);
    }

    function createResponseParameter(docItem) {
        var html = createParameterBody(docItem.responseParameters);
        $('#responseTbody').html(html);
    }

    function createParameterBody(parameters) {
        /*
        <tr>
                    <th class="prop-name">参数</th>
                    <th class="prop-type">类型</th>
                    <th>是否必填</th>
                    <th>最大长度</th>
                    <th class="prop-desc">描述</th>
                    <th class="prop-example">示例值</th>
                </tr>
         */
        var html = [];
        for (var i = 0; i < parameters.length; i++) {
            var parameter = parameters[i];
            html.push('<tr>\n' +
                ' <th class="prop-name">'+parameter.name+'</th>\n' +
                ' <th class="prop-type">'+parameter.type+'</th>\n' +
                ' <th>'+(parameter.required ? '<span style="color:red;">是</span>' : '否')+'</th>\n' +
                ' <th>-</th>\n' +
                ' <th class="prop-desc">'+parameter.description+'</th>\n' +
                ' <th class="prop-example">' + (parameter.example || parameter['x-example']) +'</th>\n' +
                '</tr>')
        }
        return html.join('');
    }

    function createResponseCode(docItem) {
        var responseParameters = docItem.responseParameters;
        var method = docItem.name.replace(/\./g, '_');
        var result = [];
        for (var i = 0; i < responseParameters.length; i++) {
            var responseParameter = responseParameters[i];
            result.push('\"'+responseParameter.name+'\": \"' + responseParameter.example + '\"')
        }
        var bizResult = result.join(",");
        var json = '{\n' +
            '    "'+method+'_response": {\n' +
            '        "code": "10000",\n' +
            '        "msg": "Success",\n' +
            bizResult +
            '    },\n' +
            '    "sign": "xxxxxx"\n' +
            '}';
        json = formatJson(json);
        $('#responseExampleJson').text(json);

        var errorJson = '{\n' +
            '    "'+method+'_response": {\n' +
            '        "code": "20000",\n' +
            '        "msg": "Service is temporarily unavailable",\n' +
            '        "sub_code": "isp.unknow-error",\n' +
            '        "sub_msg": "服务暂不可用"\n' +
            '    },\n' +
            '    "sign": "xxxxxxx"\n' +
            '}';
        errorJson = formatJson(errorJson);
        $('#responseErrorJson').text(errorJson);
    }

    initDocModules();
    initEvent();
});