<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch">
      <el-form-item label="标题" prop="title">
        <el-input v-model="queryParams.title" placeholder="请输入标题" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="门户发布" prop="publishPortal">
        <el-select v-model="queryParams.publishPortal" placeholder="全部" clearable style="width: 120px">
          <el-option label="是" value="1" />
          <el-option label="否" value="0" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
          v-hasPermi="['achievement:news:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['achievement:news:remove']">删除</el-button>
      </el-col>
    </el-row>

    <el-table v-loading="loading" :data="newsList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="编号" align="center" prop="newsId" width="80" />
      <el-table-column label="标题" align="left" prop="title" min-width="220" :show-overflow-tooltip="true" />
      <el-table-column label="门户发布" align="center" prop="publishPortal" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.publishPortal === '1' ? 'success' : 'info'" size="mini">
            {{ scope.row.publishPortal === '1' ? '是' : '否' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="权重" align="center" prop="sortWeight" width="90" />
      <el-table-column label="发布时间" align="center" prop="publishTime" width="170">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.publishTime, '{y}-{m}-{d} {h}:{i}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="浏览量" align="center" prop="viewCount" width="80" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="150">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['achievement:news:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
            v-hasPermi="['achievement:news:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <el-dialog :title="title" :visible.sync="open" width="860px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" maxlength="120" show-word-limit />
        </el-form-item>
        <el-form-item label="摘要" prop="summary">
          <el-input v-model="form.summary" type="textarea" :rows="3" maxlength="255" show-word-limit />
        </el-form-item>
        <el-form-item label="封面图" prop="coverImage">
          <image-upload v-model="form.coverImage" :limit="1" />
        </el-form-item>
        <el-form-item label="发布时间" prop="publishTime">
          <el-date-picker v-model="form.publishTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="请选择发布时间" style="width: 100%" />
        </el-form-item>
        <el-form-item label="发布到门户" prop="publishPortal">
          <el-switch v-model="form.publishPortal" active-value="1" inactive-value="0" />
        </el-form-item>
        <el-form-item label="排序权重" prop="sortWeight">
          <el-input-number v-model="form.sortWeight" :min="0" :max="9999" />
        </el-form-item>
        <el-form-item label="正文" prop="content">
          <editor v-model="form.content" :min-height="300" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="cancel">取 消</el-button>
        <el-button type="primary" @click="submitForm">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listNews, getNews, addNews, updateNews, delNews } from '@/api/achievement/news'

export default {
  name: 'AchievementNews',
  data() {
    return {
      loading: true,
      showSearch: true,
      ids: [],
      single: true,
      multiple: true,
      total: 0,
      newsList: [],
      title: '',
      open: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        title: undefined,
        publishPortal: undefined
      },
      form: {},
      rules: {
        title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
        content: [{ required: true, message: '请输入正文', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listNews(this.queryParams).then(response => {
        this.newsList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    cancel() {
      this.open = false
      this.reset()
    },
    reset() {
      this.form = {
        newsId: undefined,
        title: undefined,
        summary: undefined,
        coverImage: undefined,
        content: undefined,
        publishTime: undefined,
        publishPortal: '0',
        sortWeight: 0
      }
      this.resetForm('form')
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.newsId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    handleAdd() {
      this.reset()
      this.open = true
      this.title = '新增新闻'
    },
    handleUpdate(row) {
      this.reset()
      const newsId = row.newsId || this.ids[0]
      getNews(newsId).then(response => {
        this.form = response.data
        this.open = true
        this.title = '修改新闻'
      })
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (!valid) {
          return
        }
        const req = this.form.newsId ? updateNews(this.form) : addNews(this.form)
        req.then(() => {
          this.$modal.msgSuccess(this.form.newsId ? '修改成功' : '新增成功')
          this.open = false
          this.getList()
        })
      })
    },
    handleDelete(row) {
      const newsIds = row.newsId || this.ids
      this.$modal.confirm('是否确认删除新闻编号为"' + newsIds + '"的数据项？').then(() => {
        return delNews(newsIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('删除成功')
      }).catch(() => {})
    }
  }
}
</script>
