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
        <el-form :inline="true" :model="searchFormData" class="demo-form-inline">
          <el-form-item label="路由名称">
            <el-input v-model="searchFormData.id" placeholder="输入接口名或版本号" size="mini" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="el-icon-search" size="mini" @click="onSearchTable">查询</el-button>
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
              {{ scope.row.name + ' (' + scope.row.version + ')' }}
            </template>
          </el-table-column>
          <el-table-column
            prop="limitType"
            label="限流策略"
            width="120"
          >
            <template slot-scope slot="header">
              限流策略 <i class="el-icon-question" style="cursor: pointer" @click="onLimitTypeTipClick"></i>
            </template>
            <template slot-scope="scope">
              <span v-if="scope.row.limitType === 1">漏桶策略</span>
              <span v-if="scope.row.limitType === 2">令牌桶策略</span>
            </template>
          </el-table-column>
          <el-table-column
            prop="info"
            label="限流信息"
            width="500"
          >
            <template slot-scope="scope">
              <span v-html="infoRender(scope.row)"></span>
            </template>
          </el-table-column>
          <el-table-column
            prop="limitStatus"
            label="状态"
            width="80"
          >
            <template slot-scope="scope">
              <span v-if="scope.row.limitStatus === 1" style="color:#67C23A">已开启</span>
              <span v-if="scope.row.limitStatus === 0" style="color:#909399">已关闭</span>
            </template>
          </el-table-column>
          <el-table-column
            label="操作"
            width="80"
          >
            <template slot-scope="scope">
              <el-button type="text" size="mini" @click="onTableUpdate(scope.row)">修改</el-button>
            </template>
          </el-table-column>
        </el-table>
        <!-- dialog -->
        <el-dialog
          title="设置限流"
          :visible.sync="limitDialogVisible"
          :close-on-click-modal="false"
          @close="onLimitDialogClose"
        >
          <el-form ref="limitDialogFormMain" :model="limitDialogFormData">
            <el-form-item label="id" :label-width="formLabelWidth">
              <el-input v-model="limitDialogFormData.routeId" readonly="readonly"/>
            </el-form-item>
            <el-form-item label="限流策略" :label-width="formLabelWidth">
              <el-radio-group v-model="limitDialogFormData.limitType">
                <el-radio :label="1">漏桶策略</el-radio>
                <el-radio :label="2">令牌桶策略</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="开启状态" :label-width="formLabelWidth">
              <el-switch
                v-model="limitDialogFormData.limitStatus"
                active-color="#13ce66"
                inactive-color="#ff4949"
                :active-value="1"
                :inactive-value="0"
              >
              </el-switch>
            </el-form-item>
          </el-form>
          <el-form
            v-show="limitDialogFormData.limitType === 1 && limitDialogFormData.limitStatus"
            ref="limitDialogFormLeaky"
            :rules="rulesLeaky"
            :model="limitDialogFormData"
          >
            <el-form-item label="每秒可处理请求数" prop="execCountPerSecond" :label-width="formLabelWidth">
              <el-input-number v-model="limitDialogFormData.execCountPerSecond" controls-position="right" :min="1" />
            </el-form-item>
            <el-form-item label="错误码" prop="limitCode" :label-width="formLabelWidth">
              <el-input v-model="limitDialogFormData.limitCode" />
            </el-form-item>
            <el-form-item label="错误信息" prop="limitMsg" :label-width="formLabelWidth">
              <el-input v-model="limitDialogFormData.limitMsg" />
            </el-form-item>
          </el-form>
          <el-form
            v-show="limitDialogFormData.limitType === 2 && limitDialogFormData.limitStatus"
            ref="limitDialogFormToken"
            :rules="rulesToken"
            :model="limitDialogFormData"
          >
            <el-form-item label="令牌桶容量" prop="tokenBucketCount" :label-width="formLabelWidth">
              <el-input-number v-model="limitDialogFormData.tokenBucketCount" controls-position="right" :min="1" />
            </el-form-item>
          </el-form>
          <div slot="footer" class="dialog-footer">
            <el-button @click="limitDialogVisible = false">取 消</el-button>
            <el-button type="primary" @click="onLimitDialogSave">保 存</el-button>
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
      limitDialogFormData: {
        routeId: '',
        execCountPerSecond: 5,
        limitCode: '',
        limitMsg: '',
        tokenBucketCount: 5,
        limitStatus: 0, // 0: 停用，1：启用
        limitType: 1
      },
      rulesLeaky: {
        execCountPerSecond: [
          { required: true, message: '不能为空', trigger: 'blur' }
        ],
        limitCode: [
          { required: true, message: '不能为空', trigger: 'blur' },
          { min: 1, max: 64, message: '长度在 1 到 64 个字符', trigger: 'blur' }
        ],
        limitMsg: [
          { required: true, message: '不能为空', trigger: 'blur' },
          { min: 1, max: 100, message: '长度在 1 到 100 个字符', trigger: 'blur' }
        ]
      },
      rulesToken: {
        tokenBucketCount: [
          { required: true, message: '不能为空', trigger: 'blur' }
        ]
      },
      formLabelWidth: '150px',
      limitDialogVisible: false

    }
  },
  watch: {
    filterText(val) {
      this.$refs.tree2.filter(val)
    }
  },
  created() {
    this.loadTree()
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
      this.post('route.limit.list', this.searchFormData, function(resp) {
        this.tableData = resp.data
      })
    },
    onSearchTable: function() {
      this.loadTable()
    },
    onTableUpdate: function(row) {
      this.limitDialogVisible = true
      this.$nextTick(() => {
        Object.assign(this.limitDialogFormData, row)
      })
    },
    resetForm(formName) {
      const frm = this.$refs[formName]
      frm && frm.resetFields()
    },
    onLimitDialogClose: function() {
      this.resetForm('limitDialogFormLeaky')
      this.resetForm('limitDialogFormToken')
      this.limitDialogVisible = false
    },
    infoRender: function(row) {
      if (!row.hasRecord) {
        return '--'
      }
      const html = []
      if (row.limitType === 1) {
        html.push('每秒可处理请求数：' + row.execCountPerSecond)
        html.push('subCode：' + row.limitCode)
        html.push('subMsg：' + row.limitMsg)
      } else if (row.limitType === 2) {
        html.push('令牌桶容量：' + row.tokenBucketCount)
      }
      return html.join('，')
    },
    onLimitDialogSave: function() {
      this.doValidate(function() {
        this.limitDialogFormData.serviceId = this.serviceId
        this.post('route.limit.update', this.limitDialogFormData, function(resp) {
          this.limitDialogVisible = false
          this.loadTable()
        })
      })
    },
    doValidate: function(callback) {
      const that = this
      if (this.limitDialogFormData.limitStatus === 0) {
        callback.call(this)
        return
      }
      if (this.limitDialogFormData.limitType === 1) {
        this.$refs['limitDialogFormLeaky'].validate((valid) => {
          if (valid) {
            callback.call(that)
          }
        })
      } else {
        this.$refs['limitDialogFormToken'].validate((valid) => {
          if (valid) {
            callback.call(that)
          }
        })
      }
    },
    onLimitTypeTipClick: function() {
      const leakyRemark = '漏桶策略：每秒处理固定数量的请求，超出请求返回错误信息。'
      const tokenRemark = '令牌桶策略：每秒放置固定数量的令牌数，每个请求进来后先去拿令牌，拿到了令牌才能继续，拿不到则等候令牌重新生成了再拿。'
      const content = leakyRemark + '<br>' + tokenRemark
      this.$alert(content, '限流策略', {
        dangerouslyUseHTMLString: true
      })
    }
  }
}
</script>
