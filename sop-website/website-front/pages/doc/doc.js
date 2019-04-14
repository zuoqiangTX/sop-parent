layui.use(['element', 'form'], function(){ //加载code模块
    var form = layui.form;
    
    function initDocModules() {
        $.getJSON(SopConfig.url + '/doc/getDocBaseInfo', function (baseInfo) {
            var html = [];
            var modules = baseInfo.docModuleVOList;
            for (var i = 0; i < modules.length; i++) {
                var docDefinition = modules[i];
                var module = docDefinition.module;
                var selected = i === 0 ? 'selected="selected"' : '';
                html.push('<option value="' + module + '" ' + selected + '>' + module + '</option>');
            }
            $('#moduleList').html(html.join(''));
            form.render('select');
            if (modules && modules.length > 0) {
                selectModule(modules[0].module);
            }
            $('.url-prod').text(baseInfo.urlProd);
        })
    }
    
    function selectModule(docModule) {
        $.getJSON(SopConfig.url + '/doc/module/' + docModule, function (module) {
            var docItems = module.docItems;
            var html = ['<li><h2>' + docModule + '</h2></li>'];
            for (var i = 0; i < docItems.length; i++) {
                var docItem = docItems[i];
                /*
                <li class="site-tree-noicon layui-this">
                <a href="/">
                    <cite>统一收单交易退款查询</cite>
                </a>
            </li>
                 */
                var selectedClass = i === 0 ? 'layui-this' : '';
                html.push('<li class="site-tree-noicon ' + selectedClass + '">');
                html.push('<a href="#" sopname="'+docItem.name+'" sopversion="'+docItem.version+'"><cite>'+docItem.summary+'</cite></a>')
            }
            $('#docItemTree').html(html.join(''));
            if (docItems && docItems.length > 0) {
                var firstItem = docItems[0];
                selectDocItem(firstItem.name, firstItem.version)
            }
        })
    }

    function initEvent() {
        form.on('select(moduleListFilter)', function (data) {
            selectModule(data.value);
        })
        $('#docItemTree').on('click', 'a', function () {
            var $tagA = $(this);
            selectDocItem($tagA.attr('sopname'), $tagA.attr('sopversion'));
            $tagA.parent().addClass('layui-this').siblings().removeClass('layui-this');
        })
    }

    function selectDocItem(name, version) {
        $.getJSON(SopConfig.url + '/doc/item/' + name + '/' + version + '/', function (docItem) {
            $('.sop-name').text(docItem.name);
            $('.sop-version').text(docItem.version);
            $('.sop-summary').text(docItem.summary);
            $('.sop-description').text(docItem.description || docItem.summary);

            createRequestParameter(docItem);
            createResponseParameter(docItem);
            createResponseCode(docItem);
        })
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
                ' <th class="prop-example">' + parameter.example +'</th>\n' +
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