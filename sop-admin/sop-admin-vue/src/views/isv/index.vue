<template>
  <div class="app-container">
    <el-form :inline="true" :model="searchFormData" class="demo-form-inline" size="mini">
      <el-form-item label="appKey">
        <el-input v-model="searchFormData.appKey" :clearable="true" placeholder="appKey" style="width: 250px;" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="onSearchTable">查询</el-button>
      </el-form-item>
    </el-form>
    <el-button type="primary" size="mini" icon="el-icon-plus" style="margin-bottom: 10px;" @click="onAdd">新增ISV</el-button>
    <el-table
      :data="pageInfo.list"
      border
      fit
      highlight-current-row
    >
      <el-table-column
        prop="id"
        label="ID"
        width="80"
      />
      <el-table-column
        prop="appKey"
        label="appKey"
        width="250"
      />
      <el-table-column
        prop="secret"
        label="secret"
        width="80"
      >
        <template slot-scope="scope">
          <el-button v-if="scope.row.signType === 2" type="text" size="mini" @click="onShowSecret(scope.row)">查看</el-button>
        </template>
      </el-table-column>
      <el-table-column
        prop=""
        label="公私钥"
        width="80"
      >
        <template slot-scope="scope">
          <el-button v-if="scope.row.signType === 1" type="text" size="mini" @click="onShowPriPubKey(scope.row)">查看</el-button>
        </template>
      </el-table-column>
      <el-table-column
        prop="signType"
        label="签名类型"
        width="80"
      >
        <template slot-scope="scope">
          <span v-if="scope.row.signType === 1">RSA2</span>
          <span v-if="scope.row.signType === 2">MD5</span>
        </template>
      </el-table-column>
      <el-table-column
        prop="roleList"
        label="角色"
      >
        <template slot-scope="scope">
          <div v-html="roleRender(scope.row)"></div>
        </template>
      </el-table-column>
      <el-table-column
        prop="status"
        label="状态"
        width="80"
      >
        <template slot-scope="scope">
          <span v-if="scope.row.status === 1" style="color:#67C23A">已启用</span>
          <span v-if="scope.row.status === 2" style="color:#F56C6C">已禁用</span>
        </template>
      </el-table-column>
      <el-table-column
        prop="gmtCreate"
        label="添加时间"
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
        width="100"
      >
        <template slot-scope="scope">
          <el-button type="text" size="mini" @click="onTableUpdate(scope.row)">修改</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      background
      style="margin-top: 5px"
      :current-page="searchFormData.pageIndex"
      :page-size="searchFormData.pageSize"
      :page-sizes="[5, 10, 20, 40]"
      :total="pageInfo.total"
      layout="total, sizes, prev, pager, next"
      @size-change="onSizeChange"
      @current-change="onPageIndexChange"
    />
    <!-- dialog -->
    <el-dialog
      :title="isvDialogTitle"
      :visible.sync="isvDialogVisible"
      :close-on-click-modal="false"
      @close="onIsvDialogClose"
    >
      <el-form
        ref="isvForm"
        :rules="rulesIsvForm"
        :model="isvDialogFormData"
        label-width="120px"
        size="mini"
      >
        <el-form-item label="">
          <el-button size="mini" @click="onDataGen">一键生成数据</el-button>
        </el-form-item>
        <el-form-item prop="appKey" label="appKey">
          <el-input v-model="isvDialogFormData.appKey" size="mini" />
        </el-form-item>
        <el-form-item prop="signType" label="签名方式">
          <el-radio-group v-model="isvDialogFormData.signType">
            <el-radio :label="1" name="status">RSA2</el-radio>
            <el-radio :label="2" name="status">MD5</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-show="isvDialogFormData.signType === 2" prop="secret" label="secret">
          <el-input v-model="isvDialogFormData.secret" size="mini" />
        </el-form-item>
        <el-form-item v-show="isvDialogFormData.signType === 1" prop="pubKey" label="公钥">
          <el-input v-model="isvDialogFormData.pubKey" type="textarea" />
        </el-form-item>
        <el-form-item v-show="isvDialogFormData.signType === 1" prop="priKey" label="私钥">
          <el-input v-model="isvDialogFormData.priKey" type="textarea" />
        </el-form-item>
        <el-form-item label="角色">
          <el-checkbox-group v-model="isvDialogFormData.roleCode">
            <el-checkbox v-for="item in roles" :key="item.roleCode" :label="item.roleCode">{{ item.description }}</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="isvDialogFormData.status">
            <el-radio :label="1" name="status">启用</el-radio>
            <el-radio :label="2" name="status">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="isvDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="onIsvDialogSave">保 存</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data() {
    const validateSecret = (rule, value, callback) => {
      if (this.isvDialogFormData.signType === 2) {
        if (value === '') {
          callback(new Error('不能为空'))
        }
        if (value.length > 200) {
          callback(new Error('长度不能超过200'))
        }
      }
      callback()
    }
    const validatePubPriKey = (rule, value, callback) => {
      if (this.isvDialogFormData.signType === 1) {
        if (value === '') {
          callback(new Error('不能为空'))
        }
      }
      callback()
    }
    return {
      searchFormData: {
        appKey: '',
        pageIndex: 1,
        pageSize: 10
      },
      pageInfo: {
        list: [],
        total: 0
      },
      roles: [],
      // dialog
      isvDialogVisible: false,
      isvDialogTitle: '新增ISV',
      isvDialogFormData: {
        id: 0,
        appKey: '',
        secret: '',
        pubKey: '',
        priKey: '',
        signType: 1,
        status: 1,
        roleCode: []
      },
      rulesIsvForm: {
        appKey: [
          { required: true, message: '不能为空', trigger: 'blur' },
          { min: 1, max: 100, message: '长度在 1 到 100 个字符', trigger: 'blur' }
        ],
        secret: [
          { validator: validateSecret, trigger: 'blur' }
        ],
        pubKey: [
          { validator: validatePubPriKey, trigger: 'blur' }
        ],
        priKey: [
          { validator: validatePubPriKey, trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.loadTable()
    this.loadRouteRole()
  },
  methods: {
    loadTable() {
      this.post('isv.info.page', this.searchFormData, function(resp) {
        this.pageInfo = resp.data
      })
    },
    loadRouteRole: function() {
      if (this.roles.length === 0) {
        this.post('role.listall', {}, function(resp) {
          this.roles = resp.data
        })
      }
    },
    onShowSecret: function(row) {
      this.$alert(row.secret, 'secret')
    },
    onShowPriPubKey: function(row) {
      const pubKey = row.pubKey
      const priKey = row.priKey
      const content = '<div>公钥：<textarea style="width: 380px;height: 100px;" readonly="readonly">' + pubKey + '</textarea><br>' +
        '私钥：<textarea style="width: 380px;height: 100px;" readonly="readonly">' + priKey + '</textarea></div>'
      this.$alert(content, '公私钥', {
        dangerouslyUseHTMLString: true
      })
    },
    onSearchTable: function() {
      this.loadTable()
    },
    onTableUpdate: function(row) {
      this.isvDialogTitle = '修改ISV'
      this.isvDialogVisible = true
      this.$nextTick(() => {
        this.post('isv.info.get', { id: row.id }, function(resp) {
          const isvInfo = resp.data
          const roleList = isvInfo.roleList
          const roleCode = []
          for (let i = 0; i < roleList.length; i++) {
            roleCode.push(roleList[i].roleCode)
          }
          isvInfo.roleCode = roleCode
          Object.assign(this.isvDialogFormData, isvInfo)
        })
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
    onAdd: function() {
      this.isvDialogFormData.id = 0
      this.isvDialogTitle = '新增ISV'
      this.isvDialogVisible = true
    },
    onIsvDialogSave: function() {
      const that = this
      this.$refs['isvForm'].validate((valid) => {
        if (valid) {
          const uri = this.isvDialogFormData.id === 0 ? 'isv.info.add' : 'isv.info.update'
          that.post(uri, that.isvDialogFormData, function() {
            that.isvDialogVisible = false
            that.loadTable()
          })
        }
      })
    },
    onIsvDialogClose: function() {
      this.$refs.isvForm.resetFields()
      this.isvDialogVisible = false
    },
    roleRender: function(row) {
      const html = []
      const roleList = row.roleList
      for (let i = 0; i < roleList.length; i++) {
        html.push(roleList[i].description)
      }
      return html.join(', ')
    },
    onDataGen: function() {
      this.post('isv.form.gen', {}, function(resp) {
        const data = resp.data
        // 如果是新增状态
        if (this.isvDialogFormData.id === 0) {
          Object.assign(this.isvDialogFormData, data)
        } else {
          const signType = this.isvDialogFormData.signType
          // RSA2
          if (signType === 1) {
            Object.assign(this.isvDialogFormData, {
              pubKey: data.pubKey,
              priKey: data.priKey
            })
          } else if (signType === 2) {
            Object.assign(this.isvDialogFormData, {
              secret: data.secret
            })
          }
        }
      })
    }
  }
}
</script>
