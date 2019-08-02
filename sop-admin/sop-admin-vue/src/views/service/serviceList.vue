<template>
  <div class="app-container">
    <el-form :inline="true" :model="searchFormData" class="demo-form-inline" size="mini">
      <el-form-item label="serviceId">
        <el-input v-model="searchFormData.serviceId" :clearable="true" placeholder="serviceId" style="width: 250px;" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="onSearchTable">查询</el-button>
      </el-form-item>
    </el-form>
    <el-table
      :data="tableData"
      style="width: 100%;margin-bottom: 20px;"
      border
      row-key="id"
    >
      <el-table-column
        prop="serviceId"
        label="服务名称"
        width="200"
      >
        <template slot-scope="scope">
          <span v-html="renderServiceName(scope.row)"></span>
        </template>
      </el-table-column>
      <el-table-column
        prop="ipPort"
        label="IP端口"
        width="250"
      />
      <el-table-column
        prop="metadata"
        label="当前环境"
        width="100"
      >
        <template slot-scope="scope">
          <el-tag v-if="scope.row.parentId > 0 && scope.row.metadata.env === 'pre'" type="warning">预发布</el-tag>
          <el-tag v-if="scope.row.parentId > 0 && scope.row.metadata.env === 'gray'" type="info">灰度</el-tag>
          <el-tag v-if="scope.row.parentId > 0 && !scope.row.metadata.env" type="success">线上</el-tag>
        </template>
      </el-table-column>
      <el-table-column
        prop="metadata"
        label="metadata"
        width="250"
      >
        <template slot-scope="scope">
          <span v-if="scope.row.parentId > 0">{{ JSON.stringify(scope.row.metadata) }}</span>
        </template>
      </el-table-column>
      <el-table-column
        prop="status"
        label="服务状态"
        width="100"
      >
        <template slot-scope="scope">
          <el-tag v-if="scope.row.parentId > 0 && scope.row.status === 'UP'" type="success">已启用</el-tag>
          <el-tag v-if="scope.row.parentId > 0 && scope.row.status === 'STARTING'" type="info">正在启动</el-tag>
          <el-tag v-if="scope.row.parentId > 0 && scope.row.status === 'UNKNOWN'">未知</el-tag>
          <el-tag v-if="scope.row.parentId > 0 && (scope.row.status === 'OUT_OF_SERVICE' || scope.row.status === 'DOWN')" type="danger">已禁用</el-tag>
        </template>
      </el-table-column>
      <el-table-column
        prop="updateTime"
        label="最后更新时间"
        width="160"
      />
      <el-table-column
        label="操作"
        width="200"
      >
        <template slot-scope="scope">
          <el-button v-if="scope.row.parentId > 0 && scope.row.metadata.env" type="text" size="mini" @click="onEnvOnline(scope.row)">上线</el-button>
          <el-button v-if="scope.row.parentId > 0 && !scope.row.metadata.env" type="text" size="mini" @click="onEnvPre(scope.row)">预发布</el-button>
          <el-button v-if="scope.row.parentId > 0 && !scope.row.metadata.env" type="text" size="mini" @click="onEnvGray(scope.row)">灰度发布</el-button>
          <el-button v-if="scope.row.parentId > 0 && scope.row.metadata.env === 'gray'" type="text" size="mini" @click="onUpdateUserkey(scope.row)">灰度设置</el-button>
          <el-button v-if="scope.row.parentId > 0 && scope.row.status === 'UP'" type="text" size="mini" @click="onDisable(scope.row)">禁用</el-button>
          <el-button v-if="scope.row.parentId > 0 && scope.row.status === 'OUT_OF_SERVICE'" type="text" size="mini" @click="onEnable(scope.row)">启用</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- dialog -->
    <el-dialog
      title="灰度设置"
      :visible.sync="grayDialogVisible"
      :close-on-click-modal="false"
      @close="resetForm('grayForm')"
    >
      <el-form
        ref="grayForm"
        :model="grayForm"
        :rules="grayFormRules"
        size="mini"
      >
        <el-form-item label="服务器实例">
          {{ grayForm.serviceId + ' (' + grayForm.ipPort + ')' }}
        </el-form-item>
        <el-tabs v-model="tabsActiveName" type="card">
          <el-tab-pane label="灰度用户" name="first">
            <el-alert
              title="可以是appId，或userId，多个用英文逗号隔开"
              type="info"
              :closable="false"
              style="margin-bottom: 20px;"
            />
            <el-form-item prop="userKeyContent">
              <el-input
                v-model="grayForm.userKeyContent"
                placeholder="可以是appId，或userId，多个用英文逗号隔开"
                type="textarea"
                :rows="6"
              />
            </el-form-item>
          </el-tab-pane>
          <el-tab-pane label="接口配置" name="second">
            <el-form-item>
              <el-button type="text" @click="addNameVersion">新增灰度接口</el-button>
            </el-form-item>
            <table cellpadding="0" cellspacing="0">
              <tr
                v-for="(grayRouteConfig, index) in grayForm.grayRouteConfigList"
                :key="grayRouteConfig.key"
              >
                <td>
                  <el-form-item
                    :key="grayRouteConfig.key"
                    :prop="'grayRouteConfigList.' + index + '.oldRouteId'"
                    :rules="{required: true, message: '不能为空', trigger: ['blur', 'change']}"
                  >
                    老接口：
                    <el-select
                      v-model="grayRouteConfig.oldRouteId"
                      style="margin-right: 10px;"
                      @change="onChangeOldRoute(grayRouteConfig)"
                    >
                      <el-option
                        v-for="route in routeList"
                        :key="route.id"
                        :label="route.name + '(' + route.version + ')'"
                        :value="route.id"
                      />
                    </el-select>
                  </el-form-item>
                </td>
                <td>
                  <el-form-item
                    :key="grayRouteConfig.key + 1"
                    :prop="'grayRouteConfigList.' + index + '.newVersion'"
                    :rules="{required: true, message: '不能为空', trigger: ['blur', 'change']}"
                  >
                    灰度接口：
                    <el-select
                      v-model="grayRouteConfig.newVersion"
                      no-data-text="无数据"
                    >
                      <el-option
                        v-for="routeNew in getGraySelectData(grayRouteConfig.oldRouteId)"
                        :key="routeNew.id"
                        :label="routeNew.name + '(' + routeNew.version + ')'"
                        :value="routeNew.version"
                      />
                    </el-select>
                  </el-form-item>
                </td>
                <td style="vertical-align: baseline;">
                  <el-button v-if="index > 0" type="text" @click.prevent="removeNameVersion(grayRouteConfig)">删除</el-button>
                </td>
              </tr>
            </table>
          </el-tab-pane>
        </el-tabs>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="grayDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="onAddUserKey">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data() {
    const regex = /^\w+(,\w+)*$/
    const userKeyContentValidator = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('不能为空'))
      } else {
        if (!regex.test(value)) {
          callback(new Error('格式不正确'))
        }
        callback()
      }
    }
    return {
      searchFormData: {
        serviceId: ''
      },
      grayDialogVisible: false,
      grayForm: {
        serviceId: '',
        instanceId: '',
        ipPort: '',
        userKeyContent: '',
        onlyUpdateGrayUserkey: false,
        grayRouteConfigList: [{
          oldRouteId: '',
          newVersion: '',
          key: Date.now()
        }]
      },
      tabsActiveName: 'first',
      routeList: [],
      selectNameVersion: [],
      grayFormRules: {
        userKeyContent: [
          { required: true, message: '不能为空', trigger: 'blur' },
          { validator: userKeyContentValidator, trigger: 'blur' }
        ]
      },
      tableData: []
    }
  },
  created() {
    this.loadTable()
  },
  methods: {
    loadTable: function() {
      this.post('service.instance.list', this.searchFormData, function(resp) {
        this.tableData = this.buildTreeData(resp.data)
      })
    },
    loadRouteList: function(serviceId) {
      if (this.routeList.length === 0) {
        this.post('route.list/1.2', { serviceId: serviceId.toLowerCase() }, function(resp) {
          this.routeList = resp.data
        })
      }
    },
    getGraySelectData: function(oldRouteId) {
      return this.routeList.filter(routeNew => {
        return oldRouteId !== routeNew.id && oldRouteId.indexOf(routeNew.name) > -1
      })
    },
    buildTreeData: function(data) {
      data.forEach(ele => {
        const parentId = ele.parentId
        if (parentId === 0) {
          // 是根元素 ,不做任何操作,如果是正常的for-i循环,可以直接continue.
        } else {
          // 如果ele是子元素的话 ,把ele扔到他的父亲的child数组中.
          data.forEach(d => {
            if (d.id === parentId) {
              let childArray = d.children
              if (!childArray) {
                childArray = []
              }
              childArray.push(ele)
              d.children = childArray
            }
          })
        }
      })
      // 去除重复元素
      data = data.filter(ele => ele.parentId === 0)
      return data
    },
    onSearchTable: function() {
      this.loadTable()
    },
    onDisable: function(row) {
      this.confirm('确定要禁用【' + row.serviceId + '】吗?', function(done) {
        this.post('service.instance.offline', row, function() {
          this.tip('下线成功')
          done()
        })
      })
    },
    onEnable: function(row) {
      this.confirm('确定要启用【' + row.serviceId + '】吗?', function(done) {
        this.post('service.instance.online', row, function() {
          this.tip('上线成功')
          done()
        })
      })
    },
    onEnvOnline: function(row) {
      this.confirm('确定要上线【' + row.serviceId + '】吗?', function(done) {
        this.post('service.instance.env.online', row, function() {
          this.tip('上线成功')
          done()
        })
      })
    },
    onEnvPre: function(row) {
      this.confirm('确定要预发布【' + row.serviceId + '】吗?', function(done) {
        this.post('service.instance.env.pre', row, function() {
          this.tip('预发布成功')
          done()
        })
      })
    },
    onEnvGray: function(row) {
      this.grayForm.onlyUpdateGrayUserkey = false
      this.openGrayDialog(row)
    },
    onUpdateUserkey: function(row) {
      this.grayForm.onlyUpdateGrayUserkey = true
      this.openGrayDialog(row)
    },
    openGrayDialog: function(row) {
      const serviceId = row.serviceId
      this.loadRouteList(serviceId)
      this.post('service.instance.gray.userkey.get', { instanceId: row.instanceId }, function(resp) {
        this.grayDialogVisible = true
        const data = resp.data
        Object.assign(this.grayForm, {
          serviceId: serviceId,
          instanceId: row.instanceId,
          ipPort: row.ipPort,
          userKeyContent: data.userKeyContent || '',
          grayRouteConfigList: this.createGrayRouteConfigList(data.nameVersionContent)
        })
      })
    },
    createGrayRouteConfigList: function(nameVersionContent) {
      if (!nameVersionContent) {
        return [{
          oldRouteId: '',
          newVersion: '',
          key: Date.now()
        }]
      }
      const list = []
      const arr = nameVersionContent.split(',')
      for (let i = 0; i < arr.length; i++) {
        const el = arr[i]
        const elArr = el.split('=')
        list.push({
          oldRouteId: elArr[0],
          newVersion: elArr[1],
          key: Date.now()
        })
      }
      return list
    },
    onAddUserKey: function() {
      this.$refs.grayForm.validate((valid) => {
        if (valid) {
          const nameVersionContents = []
          const grayRouteConfigList = this.grayForm.grayRouteConfigList
          for (let i = 0; i < grayRouteConfigList.length; i++) {
            const config = grayRouteConfigList[i]
            nameVersionContents.push(config.oldRouteId + '=' + config.newVersion)
          }
          this.grayForm.nameVersionContent = nameVersionContents.join(',')
          this.post('service.instance.env.gray', this.grayForm, function() {
            this.grayDialogVisible = false
            this.tip('灰度发布发成功')
          })
        }
      })
    },
    onChangeOldRoute: function(config) {
      config.newVersion = ''
    },
    addNameVersion: function() {
      this.grayForm.grayRouteConfigList.push({
        oldRouteId: '',
        newVersion: '',
        key: Date.now()
      })
    },
    removeNameVersion: function(item) {
      const index = this.grayForm.grayRouteConfigList.indexOf(item)
      if (index !== -1) {
        this.grayForm.grayRouteConfigList.splice(index, 1)
      }
    },
    renderServiceName: function(row) {
      let instanceCount = ''
      if (row.children && row.children.length > 0) {
        const onlineCount = row.children.filter(el => {
          return el.status === 'UP'
        }).length
        instanceCount = ` (${onlineCount}/${row.children.length})`
      }
      return row.serviceId + instanceCount
    }
  }
}
</script>
