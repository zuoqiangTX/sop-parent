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
          <el-form-item label="路由ID">
            <el-input v-model="searchFormData.routeId" placeholder="接口名，支持模糊查询" clearable />
          </el-form-item>
          <el-form-item label="AppKey">
            <el-input v-model="searchFormData.appKey" placeholder="AppKey，支持模糊查询" clearable />
          </el-form-item>
          <el-form-item label="IP">
            <el-input v-model="searchFormData.limitIp" placeholder="ip，支持模糊查询" clearable />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="el-icon-search" size="mini" @click="onSearchTable">查询</el-button>
          </el-form-item>
        </el-form>
        <el-button type="primary" size="mini" icon="el-icon-plus" style="margin-bottom: 10px;" @click="onAdd">新增限流</el-button>
        <el-table
          :data="pageInfo.list"
          border
        >
          <el-table-column
            prop="limitKey"
            label="限流维度"
            width="400"
          >
            <template slot-scope="scope">
              <div v-html="limitRender(scope.row)"></div>
            </template>
          </el-table-column>
          <el-table-column
            prop="limitType"
            label="限流策略"
            width="120"
          >
            <template slot="header" slot-scope>
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
            width="250"
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
            prop="orderIndex"
            label="排序"
            width="80"
          />
          <el-table-column
            prop="gmtCreate"
            label="创建时间"
            width="160"
          />
          <el-table-column
            prop="gmtModified"
            label="修改时间"
            width="160"
          />
          <el-table-column
            label="操作"
            fixed="right"
            width="80"
          >
            <template slot-scope="scope">
              <el-button type="text" size="mini" @click="onTableUpdate(scope.row)">修改</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-pagination
          background
          style="margin-top: 5px"
          :current-page="pageInfo.pageIndex"
          :page-sizes="[5, 10, 20, 40]"
          :page-size="pageInfo.pageSize"
          :total="pageInfo.total"
          layout="total, sizes, prev, pager, next"
          @size-change="onSizeChange"
          @current-change="onPageIndexChange"
        />
        <!-- dialog -->
        <el-dialog
          :title="dlgTitle"
          :visible.sync="limitDialogVisible"
          :close-on-click-modal="false"
          @close="onLimitDialogClose"
        >
          <el-form
            ref="limitDialogForm"
            :model="limitDialogFormData"
            :rules="rulesLimit"
            label-width="150px"
            size="mini"
          >
            <el-form-item label="限流维度" prop="typeKey">
              <el-checkbox-group v-model="limitDialogFormData.typeKey">
                <el-checkbox v-model="limitDialogFormData.typeKey[0]" :label="1" name="typeKey" @change="checked=>onLimitKeyTypeChange(checked, 'routeId')">路由ID</el-checkbox>
                <el-checkbox v-model="limitDialogFormData.typeKey[1]" :label="2" name="typeKey" @change="checked=>onLimitKeyTypeChange(checked, 'appKey')">AppKey</el-checkbox>
                <el-checkbox v-model="limitDialogFormData.typeKey[2]" :label="3" name="typeKey" @change="checked=>onLimitKeyTypeChange(checked, 'limitIp')">IP</el-checkbox>
              </el-checkbox-group>
            </el-form-item>
            <el-form-item v-show="checkTypeKey(1)" prop="routeId" label="路由ID" :rules="checkTypeKey(1) ? rulesLimit.routeId : []">
              <el-select v-model="limitDialogFormData.routeId" filterable placeholder="可筛选" style="width: 300px;">
                <el-option
                  v-for="item in routeList"
                  :key="item.id"
                  :label="item.id"
                  :value="item.id"
                >
                  <span style="float: left">{{ item.name }}</span>
                  <span style="float: right; color: #8492a6; font-size: 13px">{{ item.version }}</span>
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item v-show="checkTypeKey(2)" prop="appKey" label="AppKey" :rules="checkTypeKey(2) ? rulesLimit.appKey : []">
              <el-input v-model="limitDialogFormData.appKey" placeholder="需要限流的appKey" />
            </el-form-item>
            <el-form-item v-show="checkTypeKey(3)" label="限流IP" prop="limitIp" :rules="checkTypeKey(3) ? rulesLimit.ip : []">
              <el-input v-model="limitDialogFormData.limitIp" type="textarea" :rows="2" placeholder="多个用英文逗号隔开" />
            </el-form-item>
            <el-form-item label="限流策略">
              <el-radio-group v-model="limitDialogFormData.limitType">
                <el-radio :label="1">漏桶策略</el-radio>
                <el-radio :label="2">令牌桶策略</el-radio>
              </el-radio-group>
              <i class="el-icon-question limit-tip" @click="onLimitTypeTipClick"></i>
            </el-form-item>
            <el-form-item label="开启状态">
              <el-switch
                v-model="limitDialogFormData.limitStatus"
                active-color="#13ce66"
                inactive-color="#ff4949"
                :active-value="1"
                :inactive-value="0"
              >
              </el-switch>
            </el-form-item>
            <el-form-item label="排序" prop="orderIndex">
              <el-input-number v-model="limitDialogFormData.orderIndex" controls-position="right" :min="0" />
              <el-tooltip class="item" content="值小优先执行" placement="top">
                <i class="el-icon-question limit-tip"></i>
              </el-tooltip>
            </el-form-item>
            <el-form-item v-show="isLeakyType()" label="每秒可处理请求数" prop="execCountPerSecond" :rules="isLeakyType() ? rulesLimit.execCountPerSecond : []">
              <el-input-number v-model="limitDialogFormData.execCountPerSecond" controls-position="right" :min="1" />
            </el-form-item>
            <el-form-item v-show="isLeakyType()" label="错误码" prop="limitCode" :rules="isLeakyType() ? rulesLimit.limitCode : []">
              <el-input v-model="limitDialogFormData.limitCode" />
            </el-form-item>
            <el-form-item v-show="isLeakyType()" label="错误信息" prop="limitMsg" :rules="isLeakyType() ? rulesLimit.limitMsg : []">
              <el-input v-model="limitDialogFormData.limitMsg" />
            </el-form-item>
            <el-form-item v-show="isTokenType()" label="令牌桶容量" prop="tokenBucketCount" :rules="isTokenType() ? rulesLimit.tokenBucketCount : []">
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
      pageInfo: {
        list: [],
        pageIndex: 1,
        pageSize: 10,
        total: 0
      },
      routeList: [],
      defaultProps: {
        children: 'children',
        label: 'label'
      },
      // dialog
      dlgTitle: '设置限流',
      limitDialogVisible: false,
      limitDialogFormData: {
        id: 0,
        routeId: '',
        appKey: '',
        limitIp: '',
        limitKey: '',
        execCountPerSecond: 5,
        limitCode: '',
        limitMsg: '',
        tokenBucketCount: 5,
        limitStatus: 0, // 0: 停用，1：启用
        limitType: 1,
        orderIndex: 0,
        typeKey: []
      },
      rulesLimit: {
        typeKey: [
          { type: 'array', required: true, message: '请至少选择一个', trigger: 'change' }
        ],
        routeId: [
          { required: true, message: '不能为空', trigger: 'blur' },
          { min: 1, max: 100, message: '长度在 1 到 100 个字符', trigger: 'blur' }
        ],
        appKey: [
          { required: true, message: '不能为空', trigger: 'blur' },
          { min: 1, max: 100, message: '长度在 1 到 100 个字符', trigger: 'blur' }
        ],
        ip: [
          { required: true, message: '不能为空', trigger: 'blur' },
          { min: 1, max: 500, message: '长度在 1 到 500 个字符', trigger: 'blur' }
        ],
        // leaky
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
        ],
        // token
        tokenBucketCount: [
          { required: true, message: '不能为空', trigger: 'blur' }
        ],
        orderIndex: [
          { required: true, message: '不能为空', trigger: 'blur' }
        ]
      }

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
        this.loadRouteList(this.serviceId)
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
      this.post('config.limit.list', this.searchFormData, function(resp) {
        this.pageInfo = resp.data
      })
    },
    loadRouteList: function(serviceId) {
      this.post('route.list/1.2', { serviceId: serviceId }, function(resp) {
        this.routeList = resp.data
      })
    },
    onAdd: function() {
      if (!this.serviceId) {
        this.tip('请选择服务', 'info')
        return
      }
      this.dlgTitle = '新增限流'
      this.limitDialogFormData.id = 0
      this.limitDialogVisible = true
    },
    onSearchTable: function() {
      this.loadTable()
    },
    onTableUpdate: function(row) {
      this.dlgTitle = '修改限流'
      this.limitDialogVisible = true
      this.$nextTick(() => {
        Object.assign(this.limitDialogFormData, row)
        if (row.routeId) {
          this.limitDialogFormData.typeKey.push(1)
        }
        if (row.appKey) {
          this.limitDialogFormData.typeKey.push(2)
        }
        if (row.limitIp) {
          this.limitDialogFormData.typeKey.push(3)
        }
      })
    },
    resetForm(formName) {
      const frm = this.$refs[formName]
      frm && frm.resetFields()
    },
    onLimitDialogClose: function() {
      this.resetForm('limitDialogForm')
      this.limitDialogVisible = false
    },
    infoRender: function(row) {
      const html = []
      if (row.limitType === 1) {
        html.push('每秒可处理请求数：' + row.execCountPerSecond)
        html.push('<br>subCode：' + row.limitCode)
        html.push('<br>subMsg：' + row.limitMsg)
      } else if (row.limitType === 2) {
        html.push('令牌桶容量：' + row.tokenBucketCount)
      }
      return html.join('')
    },
    onLimitDialogSave: function() {
      this.$refs['limitDialogForm'].validate((valid) => {
        if (valid) {
          this.limitDialogFormData.serviceId = this.serviceId
          const uri = this.limitDialogFormData.id ? 'config.limit.update' : 'config.limit.add'
          this.post(uri, this.limitDialogFormData, function(resp) {
            this.limitDialogVisible = false
            this.loadTable()
          })
        }
      })
    },
    onLimitTypeTipClick: function() {
      const leakyRemark = '漏桶策略：每秒处理固定数量的请求，超出请求返回错误信息。'
      const tokenRemark = '令牌桶策略：每秒放置固定数量的令牌数，每个请求进来后先去拿令牌，拿到了令牌才能继续，拿不到则等候令牌重新生成了再拿。'
      const content = leakyRemark + '<br>' + tokenRemark
      this.$alert(content, '限流策略', {
        dangerouslyUseHTMLString: true
      })
    },
    onSizeChange: function(size) {
      this.searchFormData.pageSize = size
      this.loadTable()
    },
    onPageIndexChange: function(pageIndex) {
      this.searchFormData.pageIndex = pageIndex
      this.loadTable()
    },
    onLimitKeyTypeChange: function(checked, name) {
      if (!checked) {
        this.limitDialogFormData[name] = ''
      }
    },
    checkTypeKey: function(val) {
      return this.limitDialogFormData.typeKey.find((value, index, arr) => {
        return value === val
      })
    },
    isLeakyType: function() {
      return this.limitDialogFormData.limitType === 1
    },
    isTokenType: function() {
      return this.limitDialogFormData.limitType === 2
    },
    limitRender: function(row) {
      const html = []
      const val = []
      html.push('(')
      if (row.routeId) {
        val.push(row.routeId)
        html.push('路由ID')
      }
      if (row.appKey) {
        val.push(' + ' + row.appKey)
        html.push(' + AppKey')
      }
      if (row.limitIp) {
        val.push(' + ' + row.limitIp)
        html.push(' + IP')
      }
      html.push(')')
      return val.join('') + '<br>' + html.join('')
    }
  }
}
</script>
<style scoped>
  .limit-tip {
    cursor: pointer;
    margin-left: 10px;
  }
</style>
