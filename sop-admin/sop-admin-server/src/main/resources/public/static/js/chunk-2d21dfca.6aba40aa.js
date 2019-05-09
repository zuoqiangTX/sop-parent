(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-2d21dfca"],{d448:function(t,i,e){"use strict";e.r(i);var l=function(){var t=this,i=t.$createElement,e=t._self._c||i;return e("div",{staticClass:"app-container"},[e("el-container",[e("el-aside",{staticStyle:{"min-height":"300px",width:"200px"}},[e("el-input",{staticStyle:{"margin-bottom":"20px"},attrs:{"prefix-icon":"el-icon-search",placeholder:"搜索服务...",size:"mini",clearable:""},model:{value:t.filterText,callback:function(i){t.filterText=i},expression:"filterText"}}),t._v(" "),e("el-tree",{ref:"tree2",staticClass:"filter-tree",attrs:{data:t.treeData,props:t.defaultProps,"filter-node-method":t.filterNode,"highlight-current":!0,"expand-on-click-node":!1,"empty-text":"无数据","node-key":"id","default-expand-all":""},on:{"node-click":t.onNodeClick},scopedSlots:t._u([{key:"default",fn:function(i){i.node;var l=i.data;return e("span",{staticClass:"custom-tree-node"},[l.label.length<15?e("span",[t._v(t._s(l.label))]):e("span",[e("el-tooltip",{staticClass:"item",attrs:{content:l.label,effect:"light",placement:"right"}},[e("span",[t._v(t._s(l.label.substring(0,15)+"..."))])])],1)])}}])})],1),t._v(" "),e("el-main",{staticStyle:{"padding-top":"0"}},[e("el-form",{staticClass:"demo-form-inline",attrs:{inline:!0,model:t.searchFormData}},[e("el-form-item",{attrs:{label:"路由名称"}},[e("el-input",{attrs:{placeholder:"输入接口名或版本号",size:"mini"},model:{value:t.searchFormData.id,callback:function(i){t.$set(t.searchFormData,"id",i)},expression:"searchFormData.id"}})],1),t._v(" "),e("el-form-item",[e("el-button",{attrs:{type:"primary",icon:"el-icon-search",size:"mini"},on:{click:t.onSearchTable}},[t._v("查询")])],1)],1),t._v(" "),e("el-table",{attrs:{data:t.tableData,border:"","max-height":"500"}},[e("el-table-column",{attrs:{prop:"name",label:"接口名 (版本号)",width:"200"},scopedSlots:t._u([{key:"default",fn:function(i){return[t._v("\n            "+t._s(i.row.name+" ("+i.row.version+")")+"\n          ")]}}])}),t._v(" "),e("el-table-column",{attrs:{prop:"limitType",label:"限流策略",width:"120"},scopedSlots:t._u([{key:"default",fn:function(i){return[1===i.row.limitType?e("span",[t._v("漏桶策略")]):t._e(),t._v(" "),2===i.row.limitType?e("span",[t._v("令牌桶策略")]):t._e()]}}])},[e("template",{slot:"header"},[t._v("\n            限流策略 "),e("i",{staticClass:"el-icon-question",staticStyle:{cursor:"pointer"},on:{click:t.onLimitTypeTipClick}})])],2),t._v(" "),e("el-table-column",{attrs:{prop:"info",label:"限流信息",width:"500"},scopedSlots:t._u([{key:"default",fn:function(i){return[e("span",{domProps:{innerHTML:t._s(t.infoRender(i.row))}})]}}])}),t._v(" "),e("el-table-column",{attrs:{prop:"limitStatus",label:"状态",width:"80"},scopedSlots:t._u([{key:"default",fn:function(i){return[1===i.row.limitStatus?e("span",{staticStyle:{color:"#67C23A"}},[t._v("已开启")]):t._e(),t._v(" "),0===i.row.limitStatus?e("span",{staticStyle:{color:"#909399"}},[t._v("已关闭")]):t._e()]}}])}),t._v(" "),e("el-table-column",{attrs:{label:"操作",width:"80"},scopedSlots:t._u([{key:"default",fn:function(i){return[e("el-button",{attrs:{type:"text",size:"mini"},on:{click:function(e){return t.onTableUpdate(i.row)}}},[t._v("修改")])]}}])})],1),t._v(" "),e("el-dialog",{attrs:{title:"设置限流",visible:t.limitDialogVisible,"close-on-click-modal":!1},on:{"update:visible":function(i){t.limitDialogVisible=i},close:t.onLimitDialogClose}},[e("el-form",{ref:"limitDialogFormMain",attrs:{model:t.limitDialogFormData}},[e("el-form-item",{attrs:{label:"id","label-width":t.formLabelWidth}},[e("el-input",{attrs:{readonly:"readonly"},model:{value:t.limitDialogFormData.routeId,callback:function(i){t.$set(t.limitDialogFormData,"routeId",i)},expression:"limitDialogFormData.routeId"}})],1),t._v(" "),e("el-form-item",{attrs:{label:"限流策略","label-width":t.formLabelWidth}},[e("el-radio-group",{model:{value:t.limitDialogFormData.limitType,callback:function(i){t.$set(t.limitDialogFormData,"limitType",i)},expression:"limitDialogFormData.limitType"}},[e("el-radio",{attrs:{label:1}},[t._v("漏桶策略")]),t._v(" "),e("el-radio",{attrs:{label:2}},[t._v("令牌桶策略")])],1)],1),t._v(" "),e("el-form-item",{attrs:{label:"开启状态","label-width":t.formLabelWidth}},[e("el-switch",{attrs:{"active-color":"#13ce66","inactive-color":"#ff4949","active-value":1,"inactive-value":0},model:{value:t.limitDialogFormData.limitStatus,callback:function(i){t.$set(t.limitDialogFormData,"limitStatus",i)},expression:"limitDialogFormData.limitStatus"}})],1)],1),t._v(" "),e("el-form",{directives:[{name:"show",rawName:"v-show",value:1===t.limitDialogFormData.limitType&&t.limitDialogFormData.limitStatus,expression:"limitDialogFormData.limitType === 1 && limitDialogFormData.limitStatus"}],ref:"limitDialogFormLeaky",attrs:{rules:t.rulesLeaky,model:t.limitDialogFormData}},[e("el-form-item",{attrs:{label:"每秒可处理请求数",prop:"execCountPerSecond","label-width":t.formLabelWidth}},[e("el-input-number",{attrs:{"controls-position":"right",min:1},model:{value:t.limitDialogFormData.execCountPerSecond,callback:function(i){t.$set(t.limitDialogFormData,"execCountPerSecond",i)},expression:"limitDialogFormData.execCountPerSecond"}})],1),t._v(" "),e("el-form-item",{attrs:{label:"错误码",prop:"limitCode","label-width":t.formLabelWidth}},[e("el-input",{model:{value:t.limitDialogFormData.limitCode,callback:function(i){t.$set(t.limitDialogFormData,"limitCode",i)},expression:"limitDialogFormData.limitCode"}})],1),t._v(" "),e("el-form-item",{attrs:{label:"错误信息",prop:"limitMsg","label-width":t.formLabelWidth}},[e("el-input",{model:{value:t.limitDialogFormData.limitMsg,callback:function(i){t.$set(t.limitDialogFormData,"limitMsg",i)},expression:"limitDialogFormData.limitMsg"}})],1)],1),t._v(" "),e("el-form",{directives:[{name:"show",rawName:"v-show",value:2===t.limitDialogFormData.limitType&&t.limitDialogFormData.limitStatus,expression:"limitDialogFormData.limitType === 2 && limitDialogFormData.limitStatus"}],ref:"limitDialogFormToken",attrs:{rules:t.rulesToken,model:t.limitDialogFormData}},[e("el-form-item",{attrs:{label:"令牌桶容量",prop:"tokenBucketCount","label-width":t.formLabelWidth}},[e("el-input-number",{attrs:{"controls-position":"right",min:1},model:{value:t.limitDialogFormData.tokenBucketCount,callback:function(i){t.$set(t.limitDialogFormData,"tokenBucketCount",i)},expression:"limitDialogFormData.tokenBucketCount"}})],1)],1),t._v(" "),e("div",{staticClass:"dialog-footer",attrs:{slot:"footer"},slot:"footer"},[e("el-button",{on:{click:function(i){t.limitDialogVisible=!1}}},[t._v("取 消")]),t._v(" "),e("el-button",{attrs:{type:"primary"},on:{click:t.onLimitDialogSave}},[t._v("保 存")])],1)],1)],1)],1)],1)},a=[],o={data:function(){return{filterText:"",treeData:[],tableData:[],serviceId:"",searchFormData:{},defaultProps:{children:"children",label:"label"},limitDialogFormData:{routeId:"",execCountPerSecond:5,limitCode:"",limitMsg:"",tokenBucketCount:5,limitStatus:0,limitType:1},rulesLeaky:{execCountPerSecond:[{required:!0,message:"不能为空",trigger:"blur"}],limitCode:[{required:!0,message:"不能为空",trigger:"blur"},{min:1,max:64,message:"长度在 1 到 64 个字符",trigger:"blur"}],limitMsg:[{required:!0,message:"不能为空",trigger:"blur"},{min:1,max:100,message:"长度在 1 到 100 个字符",trigger:"blur"}]},rulesToken:{tokenBucketCount:[{required:!0,message:"不能为空",trigger:"blur"}]},formLabelWidth:"150px",limitDialogVisible:!1}},watch:{filterText:function(t){this.$refs.tree2.filter(t)}},created:function(){this.loadTree()},methods:{loadTree:function(){this.post("service.list",{},function(t){var i=t.data;this.treeData=this.convertToTreeData(i,0)})},filterNode:function(t,i){return!t||-1!==i.label.indexOf(t)},onNodeClick:function(t,i,e){t.parentId&&(this.serviceId=t.label,this.searchFormData.serviceId=this.serviceId,this.loadTable())},convertToTreeData:function(t,i){for(var e=[],l={label:"服务列表",parentId:i},a=[],o=0;o<t.length;o++){var r={label:t[o].serviceId,parentId:1};a.push(r)}return l.children=a,e.push(l),e},loadTable:function(){this.post("route.limit.list",this.searchFormData,function(t){this.tableData=t.data})},onSearchTable:function(){this.loadTable()},onTableUpdate:function(t){var i=this;this.limitDialogVisible=!0,this.$nextTick(function(){Object.assign(i.limitDialogFormData,t)})},resetForm:function(t){var i=this.$refs[t];i&&i.resetFields()},onLimitDialogClose:function(){this.resetForm("limitDialogFormLeaky"),this.resetForm("limitDialogFormToken"),this.limitDialogVisible=!1},infoRender:function(t){if(!t.hasRecord)return"--";var i=[];return 1===t.limitType?(i.push("每秒可处理请求数："+t.execCountPerSecond),i.push("subCode："+t.limitCode),i.push("subMsg："+t.limitMsg)):2===t.limitType&&i.push("令牌桶容量："+t.tokenBucketCount),i.join("，")},onLimitDialogSave:function(){this.doValidate(function(){this.limitDialogFormData.serviceId=this.serviceId,this.post("route.limit.update",this.limitDialogFormData,function(t){this.limitDialogVisible=!1,this.loadTable()})})},doValidate:function(t){var i=this;0!==this.limitDialogFormData.limitStatus?1===this.limitDialogFormData.limitType?this.$refs["limitDialogFormLeaky"].validate(function(e){e&&t.call(i)}):this.$refs["limitDialogFormToken"].validate(function(e){e&&t.call(i)}):t.call(this)},onLimitTypeTipClick:function(){var t="漏桶策略：每秒处理固定数量的请求，超出请求返回错误信息。",i="令牌桶策略：每秒放置固定数量的令牌数，每个请求进来后先去拿令牌，拿到了令牌才能继续，拿不到则等候令牌重新生成了再拿。",e=t+"<br>"+i;this.$alert(e,"限流策略",{dangerouslyUseHTMLString:!0})}}},r=o,n=e("2877"),s=Object(n["a"])(r,l,a,!1,null,null,null);i["default"]=s.exports}}]);