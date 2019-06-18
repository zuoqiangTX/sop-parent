(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-6a68a33e"],{1248:function(e,t,o){"use strict";o.r(t);var a=function(){var e=this,t=e.$createElement,o=e._self._c||t;return o("div",{staticClass:"app-container"},[o("el-container",[o("el-aside",{staticStyle:{"min-height":"300px",width:"250px"}},[o("el-button",{attrs:{type:"primary",plain:"",size:"mini",icon:"el-icon-plus"},on:{click:function(t){return t.stopPropagation(),e.addService(t)}}},[e._v("\n        新建服务\n      ")]),e._v(" "),o("el-input",{staticStyle:{"margin-bottom":"10px","margin-top":"10px"},attrs:{"prefix-icon":"el-icon-search",placeholder:"搜索服务...",size:"mini",clearable:""},model:{value:e.filterText,callback:function(t){e.filterText=t},expression:"filterText"}}),e._v(" "),o("el-tree",{ref:"serviceTree",staticClass:"filter-tree",attrs:{data:e.treeData,props:e.defaultProps,"filter-node-method":e.filterNode,"highlight-current":!0,"expand-on-click-node":!1,"empty-text":"无数据","node-key":"serviceId","default-expand-all":""},on:{"node-click":e.onNodeClick},scopedSlots:e._u([{key:"default",fn:function(t){t.node;var a=t.data;return o("span",{staticClass:"custom-tree-node"},[o("div",[o("el-tooltip",{directives:[{name:"show",rawName:"v-show",value:a.custom,expression:"data.custom"}],staticClass:"item",attrs:{content:"自定义服务",effect:"light",placement:"left"}},[o("i",{staticClass:"el-icon-warning-outline"})]),e._v(" "),a.label.length<e.serviceTextLimitSize?o("span",[e._v(e._s(a.label))]):o("span",[o("el-tooltip",{staticClass:"item",attrs:{content:a.label,effect:"light",placement:"right"}},[o("span",[e._v(e._s(a.label.substring(0,e.serviceTextLimitSize)+"..."))])])],1)],1),e._v(" "),o("span",[1===a.custom?o("el-button",{attrs:{type:"text",size:"mini",icon:"el-icon-delete",title:"删除服务"},on:{click:function(t){return t.stopPropagation(),function(){return e.onDelService(a)}()}}}):e._e()],1)])}}])})],1),e._v(" "),o("el-main",{staticStyle:{"padding-top":"0"}},[o("el-form",{staticClass:"demo-form-inline",attrs:{inline:!0,model:e.searchFormData,size:"mini"}},[o("el-form-item",{attrs:{label:"路由名称"}},[o("el-input",{attrs:{placeholder:"输入接口名或版本号"},model:{value:e.searchFormData.id,callback:function(t){e.$set(e.searchFormData,"id",t)},expression:"searchFormData.id"}})],1),e._v(" "),o("el-form-item",[o("el-button",{attrs:{type:"primary",icon:"el-icon-search"},on:{click:e.onSearchTable}},[e._v("查询")])],1)],1),e._v(" "),o("el-button",{directives:[{name:"show",rawName:"v-show",value:e.isCustomService,expression:"isCustomService"}],attrs:{type:"primary",size:"mini",icon:"el-icon-plus"},on:{click:function(t){return t.stopPropagation(),e.addRoute(t)}}},[e._v("\n        新建路由\n      ")]),e._v(" "),o("el-table",{staticStyle:{"margin-top":"10px"},attrs:{data:e.tableData,border:"","max-height":"500"}},[o("el-table-column",{attrs:{prop:"name",label:"接口名 (版本号)",width:"200"},scopedSlots:e._u([{key:"default",fn:function(t){return[e._v("\n            "+e._s(t.row.name+(t.row.version?" ("+t.row.version+")":""))+"\n          ")]}}])}),e._v(" "),o("el-table-column",{attrs:{prop:"uri",label:"LoadBalance",width:"350"},scopedSlots:e._u([{key:"default",fn:function(t){return[e._v("\n            "+e._s(t.row.uri+t.row.path)+"\n          ")]}}])}),e._v(" "),o("el-table-column",{attrs:{prop:"roles",label:"访问权限",width:"100"},scopedSlots:e._u([{key:"default",fn:function(t){return[o("span",{domProps:{innerHTML:e._s(e.roleRender(t.row))}})]}}])}),e._v(" "),o("el-table-column",{attrs:{prop:"ignoreValidate",label:"签名校验",width:"80"},scopedSlots:e._u([{key:"default",fn:function(t){return[0===t.row.ignoreValidate?o("span",{staticStyle:{color:"#67C23A"}},[e._v("校验")]):e._e(),e._v(" "),1===t.row.ignoreValidate?o("span",{staticStyle:{color:"#E6A23C"}},[e._v("不校验")]):e._e()]}}])}),e._v(" "),o("el-table-column",{attrs:{prop:"mergeResult",label:"统一格式输出",width:"120"},scopedSlots:e._u([{key:"default",fn:function(t){return[1===t.row.mergeResult?o("span",{staticStyle:{color:"#67C23A"}},[e._v("是")]):e._e(),e._v(" "),0===t.row.mergeResult?o("span",{staticStyle:{color:"#E6A23C"}},[e._v("否")]):e._e()]}}])}),e._v(" "),o("el-table-column",{attrs:{prop:"status",label:"状态",width:"80"},scopedSlots:e._u([{key:"default",fn:function(t){return[0===t.row.status?o("span",{staticStyle:{color:"#E6A23C"}},[e._v("待审核")]):e._e(),e._v(" "),1===t.row.status?o("span",{staticStyle:{color:"#67C23A"}},[e._v("已启用")]):e._e(),e._v(" "),2===t.row.status?o("span",{staticStyle:{color:"#F56C6C"}},[e._v("已禁用")]):e._e()]}}])}),e._v(" "),o("el-table-column",{attrs:{label:"操作",fixed:"right",width:"100"},scopedSlots:e._u([{key:"default",fn:function(t){return[o("el-button",{attrs:{type:"text",size:"mini"},on:{click:function(o){return e.onTableUpdate(t.row)}}},[e._v("修改")]),e._v(" "),t.row.permission?o("el-button",{attrs:{type:"text",size:"mini"},on:{click:function(o){return e.onTableAuth(t.row)}}},[e._v("授权")]):e._e(),e._v(" "),t.row.custom?o("el-button",{attrs:{type:"text",size:"mini"},on:{click:function(o){return e.onTableDel(t.row)}}},[e._v("删除")]):e._e()]}}])})],1)],1)],1),e._v(" "),o("el-dialog",{attrs:{title:e.routeDialogTitle,visible:e.routeDialogVisible,"close-on-click-modal":!1},on:{"update:visible":function(t){e.routeDialogVisible=t},close:e.onCloseRouteDialog}},[o("el-form",{ref:"routeDialogFormRef",attrs:{model:e.routeDialogFormData,rules:e.routeDialogFormRules,"label-width":"120px",size:"mini"}},[o("el-input",{directives:[{name:"show",rawName:"v-show",value:!1,expression:"false"}],model:{value:e.routeDialogFormData.id,callback:function(t){e.$set(e.routeDialogFormData,"id",t)},expression:"routeDialogFormData.id"}}),e._v(" "),o("el-form-item",{attrs:{label:"接口名",prop:"name"}},[o("el-input",{attrs:{placeholder:"接口名，如：product.goods.list",disabled:Boolean(e.routeDialogFormData.id)},model:{value:e.routeDialogFormData.name,callback:function(t){e.$set(e.routeDialogFormData,"name",t)},expression:"routeDialogFormData.name"}})],1),e._v(" "),o("el-form-item",{attrs:{label:"版本号",prop:"version"}},[o("el-input",{attrs:{placeholder:"版本号，如：1.0",disabled:Boolean(e.routeDialogFormData.id)},model:{value:e.routeDialogFormData.version,callback:function(t){e.$set(e.routeDialogFormData,"version",t)},expression:"routeDialogFormData.version"}})],1),e._v(" "),o("el-form-item",{attrs:{label:"uri",prop:"uri"}},[o("el-input",{attrs:{placeholder:"如：http://www.xx.com",disabled:e.routePropDisabled()},model:{value:e.routeDialogFormData.uri,callback:function(t){e.$set(e.routeDialogFormData,"uri",t)},expression:"routeDialogFormData.uri"}})],1),e._v(" "),o("el-form-item",{attrs:{label:"path",prop:"path"}},[o("el-input",{attrs:{placeholder:"如：/order/list",disabled:e.routePropDisabled()},model:{value:e.routeDialogFormData.path,callback:function(t){e.$set(e.routeDialogFormData,"path",t)},expression:"routeDialogFormData.path"}})],1),e._v(" "),o("el-form-item",{attrs:{label:"签名校验"}},[o("el-radio-group",{attrs:{disabled:e.routePropDisabled()},model:{value:e.routeDialogFormData.ignoreValidate,callback:function(t){e.$set(e.routeDialogFormData,"ignoreValidate",t)},expression:"routeDialogFormData.ignoreValidate"}},[o("el-radio",{attrs:{label:0,name:"ignoreValidate"}},[e._v("校验")]),e._v(" "),o("el-radio",{attrs:{label:1,name:"ignoreValidate"}},[e._v("不校验")])],1)],1),e._v(" "),o("el-form-item",{attrs:{label:"统一格式输出"}},[o("el-radio-group",{attrs:{disabled:e.routePropDisabled()},model:{value:e.routeDialogFormData.mergeResult,callback:function(t){e.$set(e.routeDialogFormData,"mergeResult",t)},expression:"routeDialogFormData.mergeResult"}},[o("el-radio",{attrs:{label:1,name:"mergeResult"}},[e._v("是")]),e._v(" "),o("el-radio",{attrs:{label:0,name:"mergeResult"}},[e._v("否")])],1)],1),e._v(" "),o("el-form-item",{attrs:{label:"状态"}},[o("el-radio-group",{model:{value:e.routeDialogFormData.status,callback:function(t){e.$set(e.routeDialogFormData,"status",t)},expression:"routeDialogFormData.status"}},[o("el-radio",{attrs:{label:1,name:"status"}},[e._v("启用")]),e._v(" "),o("el-radio",{staticStyle:{color:"#F56C6C"},attrs:{label:2,name:"status"}},[e._v("禁用")])],1)],1)],1),e._v(" "),o("div",{staticClass:"dialog-footer",attrs:{slot:"footer"},slot:"footer"},[o("el-button",{on:{click:function(t){e.routeDialogVisible=!1}}},[e._v("取 消")]),e._v(" "),o("el-button",{attrs:{type:"primary"},on:{click:e.onRouteDialogSave}},[e._v("保 存")])],1)],1),e._v(" "),o("el-dialog",{attrs:{title:"路由授权",visible:e.authDialogVisible,"close-on-click-modal":!1},on:{"update:visible":function(t){e.authDialogVisible=t}}},[o("el-form",{attrs:{model:e.authDialogFormData,"label-width":"120px",size:"mini"}},[o("el-form-item",{attrs:{label:"id"}},[o("el-input",{attrs:{readonly:"readonly"},model:{value:e.authDialogFormData.routeId,callback:function(t){e.$set(e.authDialogFormData,"routeId",t)},expression:"authDialogFormData.routeId"}})],1),e._v(" "),o("el-form-item",{attrs:{label:"角色"}},[o("el-checkbox-group",{model:{value:e.authDialogFormData.roleCode,callback:function(t){e.$set(e.authDialogFormData,"roleCode",t)},expression:"authDialogFormData.roleCode"}},e._l(e.roles,function(t){return o("el-checkbox",{key:t.roleCode,attrs:{label:t.roleCode}},[e._v(e._s(t.description))])}),1)],1)],1),e._v(" "),o("div",{staticClass:"dialog-footer",attrs:{slot:"footer"},slot:"footer"},[o("el-button",{on:{click:function(t){e.authDialogVisible=!1}}},[e._v("取 消")]),e._v(" "),o("el-button",{attrs:{type:"primary"},on:{click:e.onAuthDialogSave}},[e._v("保 存")])],1)],1),e._v(" "),o("el-dialog",{attrs:{title:"添加服务",visible:e.addServiceDialogVisible,"close-on-click-modal":!1},on:{"update:visible":function(t){e.addServiceDialogVisible=t},close:e.closeAddServiceDlg}},[o("el-form",{ref:"addServiceForm",attrs:{model:e.addServiceForm,rules:e.addServiceFormRules,"label-width":"200px"}},[o("el-form-item",{attrs:{label:"服务名（serviceId）",prop:"serviceId"}},[o("el-input",{attrs:{placeholder:"服务名，如：order-service"},model:{value:e.addServiceForm.serviceId,callback:function(t){e.$set(e.addServiceForm,"serviceId",t)},expression:"addServiceForm.serviceId"}})],1)],1),e._v(" "),o("span",{staticClass:"dialog-footer",attrs:{slot:"footer"},slot:"footer"},[o("el-button",{on:{click:function(t){e.addServiceDialogVisible=!1}}},[e._v("取 消")]),e._v(" "),o("el-button",{attrs:{type:"primary"},on:{click:e.onAddService}},[e._v("确 定")])],1)],1)],1)},i=[],r={data:function(){return{serviceTextLimitSize:20,filterText:"",treeData:[],tableData:[],serviceId:"",isCustomService:!1,searchFormData:{id:"",serviceId:""},defaultProps:{children:"children",label:"label"},routeDialogTitle:"修改路由",routeDialogFormData:{id:"",name:"",version:"1.0",uri:"",path:"",status:1,mergeResult:1,ignoreValidate:0},routeDialogFormRules:{name:[{required:!0,message:"不能为空",trigger:"blur"},{min:1,max:100,message:"长度在 1 到 100 个字符",trigger:"blur"}],version:[{required:!0,message:"不能为空",trigger:"blur"},{min:1,max:100,message:"长度在 1 到 100 个字符",trigger:"blur"}],uri:[{required:!0,message:"不能为空",trigger:"blur"},{min:1,max:100,message:"长度在 1 到 100 个字符",trigger:"blur"}],path:[{min:0,max:100,message:"长度不能超过 100 个字符",trigger:"blur"}]},routeDialogVisible:!1,roles:[],authDialogFormData:{routeId:"",roleCode:[]},authDialogVisible:!1,addServiceDialogVisible:!1,addServiceForm:{serviceId:""},addServiceFormRules:{serviceId:[{required:!0,message:"请输入服务名称",trigger:"blur"},{min:1,max:100,message:"长度在 1 到 100 个字符",trigger:"blur"}]}}},watch:{filterText:function(e){this.$refs.serviceTree.filter(e)}},created:function(){this.loadTree(),this.loadRouteRole()},methods:{loadTree:function(){this.post("service.list",{},function(e){var t=this,o=e.data;this.treeData=this.convertToTreeData(o,0),this.$nextTick(function(){t.serviceId&&t.$refs.serviceTree.setCurrentKey(t.serviceId)})})},filterNode:function(e,t){return!e||-1!==t.label.indexOf(e)},onNodeClick:function(e,t,o){e.parentId&&(this.serviceId=e.label,this.searchFormData.serviceId=this.serviceId,this.isCustomService=Boolean(e.custom),this.loadTable())},convertToTreeData:function(e,t){for(var o=[],a={label:"服务列表",parentId:t},i=[],r=0;r<e.length;r++)e[r].parentId=1,e[r].label=e[r].serviceId,i.push(e[r]);return a.children=i,o.push(a),o},loadTable:function(e){var t=e||this.searchFormData;this.post("route.list",t,function(e){this.tableData=e.data})},onSearchTable:function(){this.loadTable()},onTableUpdate:function(e){var t=this;this.routeDialogTitle="修改路由",this.routeDialogVisible=!0,this.$nextTick(function(){Object.assign(t.routeDialogFormData,e)})},onTableAuth:function(e){this.authDialogFormData.routeId=e.id;var t={id:e.id,serviceId:this.serviceId};this.post("route.role.get",t,function(e){for(var t=e.data,o=[],a=0;a<t.length;a++)o.push(t[a].roleCode);this.authDialogFormData.roleCode=o,this.authDialogVisible=!0})},onTableDel:function(e){this.confirm("确认要删除路由【".concat(e.id,"】吗？"),function(t){var o={serviceId:this.serviceId,id:e.id};this.post("route.del",o,function(){t(),this.tip("删除成功"),this.loadTable()})})},onCloseRouteDialog:function(){this.resetForm("routeDialogFormRef")},routePropDisabled:function(){return!!this.routeDialogFormData.id&&!this.isCustomService},loadRouteRole:function(){0===this.roles.length&&this.post("role.listall",{},function(e){this.roles=e.data})},addRoute:function(){var e=this;this.routeDialogTitle="新建路由",this.routeDialogVisible=!0,this.$nextTick(function(){Object.assign(e.routeDialogFormData,{id:""})})},roleRender:function(e){if(!e.permission)return"（公开）";for(var t=[],o=e.roles,a=0;a<o.length;a++)t.push(o[a].description);return t.length>0?t.join(", "):'<span class="x-red">未授权</span>'},onRouteDialogSave:function(){var e=this;this.$refs.routeDialogFormRef.validate(function(t){if(t){var o=e.routeDialogFormData.id?"route.update":"route.add";e.routeDialogFormData.serviceId=e.serviceId,e.post(o,e.routeDialogFormData,function(){this.routeDialogVisible=!1,this.loadTable()})}})},onAuthDialogSave:function(){this.post("route.role.update",this.authDialogFormData,function(){this.authDialogVisible=!1,this.loadTable()})},addService:function(){this.addServiceDialogVisible=!0},closeAddServiceDlg:function(){this.$refs.addServiceForm.resetFields()},onAddService:function(){var e=this;this.$refs.addServiceForm.validate(function(t){t&&e.post("service.custom.add",e.addServiceForm,function(e){this.addServiceDialogVisible=!1,this.tip("添加成功"),this.loadTree()})})},onDelService:function(e){var t=e.serviceId;this.confirm("确认要删除服务"+t+"吗，【对应的路由配置会一起删除】",function(e){var o={serviceId:t};this.post("service.custom.del",o,function(){e(),this.tip("删除成功"),this.loadTree()})})}}},l=r,s=(o("55a2"),o("2877")),n=Object(s["a"])(l,a,i,!1,null,null,null);t["default"]=n.exports},"55a2":function(e,t,o){"use strict";var a=o("b294"),i=o.n(a);i.a},b294:function(e,t,o){}}]);