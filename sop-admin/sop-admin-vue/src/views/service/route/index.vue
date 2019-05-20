<template>
  <div class="app-container">
    <el-container>
      <el-aside style="min-height: 300px;width: 200px;">
        <el-input v-model="filterText" prefix-icon="el-icon-search" placeholder="搜索服务..." style="margin-bottom:20px;" size="mini" clearable />
        <el-tree
          ref="tree2"
          :data="treeData"
          :props="defaultProps"
          :filter-node-method="filterNode"
          :highlight-current="true"
          :expand-on-click-node="false"
          empty-text="无数据"
          node-key="id"
          class="filter-tree"
          default-expand-all
          @node-click="onNodeClick"
        >
          <span slot-scope="{ node, data }" class="custom-tree-node">
            <span v-if="data.label.length < 15">{{ data.label }}</span>
            <span v-else>
              <el-tooltip :content="data.label" class="item" effect="light" placement="right">
                <span>{{ data.label.substring(0, 15) + '...' }}</span>
              </el-tooltip>
            </span>
          </span>
        </el-tree>
      </el-aside>
      <el-main style="padding-top:0">
        <el-form :inline="true" :model="searchFormData" class="demo-form-inline" size="mini">
          <el-form-item label="路由名称">
            <el-input v-model="searchFormData.id" placeholder="输入接口名或版本号" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="el-icon-search" @click="onSearchTable">查询</el-button>
          </el-form-item>
        </el-form>
        <el-table
          :data="tableData"
          border
          max-height="500"
        >
          <el-table-column
            prop="name"
            label="接口名 (版本号)"
            width="200"
          >
            <template slot-scope="scope">
              {{ scope.row.name + (scope.row.version ? ' (' + scope.row.version + ')' : '') }}
            </template>
          </el-table-column>
          <el-table-column
            prop="uri"
            label="LoadBalance"
            width="350"
          >
            <template slot-scope="scope">
              {{ scope.row.uri + scope.row.path }}
            </template>
          </el-table-column>
          <el-table-column
            prop="roles"
            label="访问权限"
            width="100"
          >
            <template slot-scope="scope">
              <span v-html="roleRender(scope.row)"></span>
            </template>
          </el-table-column>
          <el-table-column
            prop="ignoreValidate"
            label="忽略验证"
            width="80"
          >
            <template slot-scope="scope">
              <span v-if="scope.row.ignoreValidate === 1" style="color:#67C23A">是</span>
              <span v-if="scope.row.ignoreValidate === 0" style="color:#909399">否</span>
            </template>
          </el-table-column>
          <el-table-column
            prop="mergeResult"
            label="合并结果"
            width="80"
          >
            <template slot-scope="scope">
              <span v-if="scope.row.mergeResult === 1" style="color:#67C23A">合并</span>
              <span v-if="scope.row.mergeResult === 0" style="color:#E6A23C">不合并</span>
            </template>
          </el-table-column>
          <el-table-column
            prop="status"
            label="状态"
            width="80"
          >
            <template slot-scope="scope">
              <span v-if="scope.row.status === 0" style="color:#909399">待审核</span>
              <span v-if="scope.row.status === 1" style="color:#67C23A">已启用</span>
              <span v-if="scope.row.status === 2" style="color:#F56C6C">已禁用</span>
            </template>
          </el-table-column>
          <el-table-column
            label="操作"
            fixed="right"
            width="100"
          >
            <template slot-scope="scope">
              <el-button type="text" size="mini" @click="onTableUpdate(scope.row)">修改</el-button>
              <el-button v-if="scope.row.permission" type="text" size="mini" @click="onTableAuth(scope.row)">授权</el-button>
            </template>
          </el-table-column>
        </el-table>
        <!-- route dialog -->
        <el-dialog title="修改路由" :visible.sync="routeDialogVisible" :close-on-click-modal="false">
          <el-form
            :model="routeDialogFormData"
            label-width="120px"
            size="mini"
          >
            <el-form-item label="id">
              <el-input v-model="routeDialogFormData.id" readonly="readonly" />
            </el-form-item>
            <el-form-item label="uri">
              <el-input v-model="routeDialogFormData.uri" />
            </el-form-item>
            <el-form-item label="path">
              <el-input v-model="routeDialogFormData.path" />
            </el-form-item>
            <el-form-item label="状态">
              <el-radio-group v-model="routeDialogFormData.status">
                <el-radio :label="1" name="status">启用</el-radio>
                <el-radio :label="2" name="status" style="color:#F56C6C">禁用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-form>
          <div slot="footer" class="dialog-footer">
            <el-button @click="routeDialogVisible = false">取 消</el-button>
            <el-button type="primary" @click="onRouteDialogSave">保 存</el-button>
          </div>
        </el-dialog>
        <!-- auth dialog -->
        <el-dialog title="路由授权" :visible.sync="authDialogVisible" :close-on-click-modal="false">
          <el-form
            :model="authDialogFormData"
            label-width="120px"
            size="mini"
          >
            <el-form-item label="id">
              <el-input v-model="authDialogFormData.routeId" readonly="readonly" />
            </el-form-item>
            <el-form-item label="角色">
              <el-checkbox-group v-model="authDialogFormData.roleCode">
                <el-checkbox v-for="item in roles" :key="item.roleCode" :label="item.roleCode">{{ item.description }}</el-checkbox>
              </el-checkbox-group>
            </el-form-item>
          </el-form>
          <div slot="footer" class="dialog-footer">
            <el-button @click="authDialogVisible = false">取 消</el-button>
            <el-button type="primary" @click="onAuthDialogSave">保 存</el-button>
          </div>
        </el-dialog>
      </el-main>
    </el-container>
  </div>
</template>

<script>
export default {
  data() {
    return {
      filterText: '',
      treeData: [],
      tableData: [],
      serviceId: '',
      searchFormData: {},
      defaultProps: {
        children: 'children',
        label: 'label'
      },
      // dialog
      routeDialogFormData: {
        status: 1
      },
      routeDialogVisible: false,
      roles: [],
      authDialogFormData: {
        routeId: '',
        roleCode: []
      },
      authDialogVisible: false
    }
  },
  watch: {
    filterText(val) {
      this.$refs.tree2.filter(val)
    }
  },
  created() {
    this.loadTree()
    this.loadRouteRole()
  },
  methods: {
    // 加载树
    loadTree: function() {
      this.post('service.list', {}, function(resp) {
        const respData = resp.data
        this.treeData = this.convertToTreeData(respData, 0)
      })
    },
    // 树搜索
    filterNode(value, data) {
      if (!value) return true
      return data.label.indexOf(value) !== -1
    },
    // 树点击事件
    onNodeClick(data, node, tree) {
      if (data.parentId) {
        this.serviceId = data.label
        this.searchFormData.serviceId = this.serviceId
        this.loadTable()
      }
    },
    /**
     * 数组转成树状结构
     * @param data 数据结构 [{
          "_parentId": 14,
          "gmtCreate": "2019-01-15 09:44:38",
          "gmtUpdate": "2019-01-15 09:44:38",
          "id": 15,
          "isShow": 1,
          "name": "用户注册",
          "orderIndex": 10000,
          "parentId": 14
      },...]
     * @param pid 初始父节点id，一般是0
     * @return 返回结果 [{
        label: '一级 1',
        children: [{
          label: '二级 1-1',
          children: [{
            label: '三级 1-1-1'
          }]
        }]
      }
     */
    convertToTreeData(data, pid) {
      const result = []
      const root = {
        label: '服务列表',
        parentId: pid
      }
      const children = []
      for (let i = 0; i < data.length; i++) {
        const item = { label: data[i].serviceId, parentId: 1 }
        children.push(item)
      }
      root.children = children
      result.push(root)
      return result
    },
    // table
    loadTable: function() {
      this.post('route.list', this.searchFormData, function(resp) {
        this.tableData = resp.data
      })
    },
    onSearchTable: function() {
      this.loadTable()
    },
    onTableUpdate: function(row) {
      Object.assign(this.routeDialogFormData, row)
      this.routeDialogVisible = true
    },
    onTableAuth: function(row) {
      this.authDialogFormData.routeId = row.id
      const searchData = { id: row.id, serviceId: this.serviceId }
      this.post('route.role.get', searchData, function(resp) {
        const roleList = resp.data
        const roleCodes = []
        for (let i = 0; i < roleList.length; i++) {
          roleCodes.push(roleList[i].roleCode)
        }
        this.authDialogFormData.roleCode = roleCodes
        this.authDialogVisible = true
      })
    },
    loadRouteRole: function() {
      if (this.roles.length === 0) {
        this.post('role.listall', {}, function(resp) {
          this.roles = resp.data
        })
      }
    },
    roleRender: function(row) {
      if (!row.permission) {
        return '（公开）'
      }
      const html = []
      const roles = row.roles
      for (let i = 0; i < roles.length; i++) {
        html.push(roles[i].description)
      }
      return html.length > 0 ? html.join(', ') : '<span class="x-red">未授权</span>'
    },
    onRouteDialogSave: function() {
      this.routeDialogFormData.serviceId = this.serviceId
      this.post('route.update', this.routeDialogFormData, function() {
        this.routeDialogVisible = false
        this.loadTable()
      })
    },
    onAuthDialogSave: function() {
      this.post('route.role.update', this.authDialogFormData, function() {
        console.log(this.authDialogFormData)
        this.authDialogVisible = false
        this.loadTable()
      })
    }
  }
}
</script>
