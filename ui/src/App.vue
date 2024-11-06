<script setup>
import {ref} from "vue";
import Axios from "@/utils/request.js";
import {ElMessage, ElMessageBox} from "element-plus";


const studentList = ref([]);
const loading = ref(true);
const total = ref(0);

const title = ref("");
const open = ref(false);
const form = ref({});
const isCreate = ref(false);


const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
});

const rules = ref({
  id: [
    {required: true, message: "字典名称不能为空", trigger: "blur"}
  ],
  name: [
    {required: true, message: "字典名称不能为空", trigger: "blur"}
  ],
  birthday: [
    {required: true, message: "字典名称不能为空", trigger: "blur"}
  ],
  description: [
    {required: true, message: "字典名称不能为空", trigger: "blur"}
  ],
  avgScore: [
    {required: true, message: "字典名称不能为空", trigger: "blur"}
  ],
});

// 重置方法
const reset = () => {
  form.value = {
    name: undefined,
    birthday: undefined,
    description: undefined,
    avgScore: undefined
  };
};

const getList = () => {
  loading.value = true;
  Axios.get("/student", {params: queryParams.value})
      .then(res => {
        studentList.value = res.list;
        total.value = res.total;
        loading.value = false;
      })
};

let id = 0;

// 修改前获取原本数据
const handleUpdate = (row) => {
  reset();
  id = row.id;
  isCreate.value = false;
  open.value = true;
  title.value = "修改学生信息";
  form.value = row;
};

// 删除方法
const handleDelete = (row) => {
  ElMessageBox.confirm(
      '是否确认删除编号为"' + row.id + '"的数据项？',
      '删除所选数据',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
      .then(() => {
        Axios.delete("/student", {params: {id: row.id}}).then(response => {
          ElMessage({
            message: "删除成功",
            type: "success"
          });
          getList();
        })
      })
      .catch(() => {});
};

// 提交表单
const submitForm = () => {
  if (isCreate.value === false) {
    Axios.patch("student", form.value).then(response => {
      ElMessage.success("修改成功");
      open.value = false;
      getList();
    })
  } else {
    Axios.post("student", form.value).then(response => {
      ElMessage.success("添加成功");
      open.value = false;
      getList();
    })
  }
}

// 添加方法
const handleCreate = () => {
  reset();
  isCreate.value = true;
  open.value = true;
  title.value = "添加学生信息";
};

// 取消方法
const cancel = () => {
  open.value = false;
  reset();
};

const handleCurrentChange = (val) => {
  queryParams.value.pageNum = val;
  getList()
}

getList()

</script>

<template>
<div class="container">
  <el-button @click="handleCreate" type="primary">新增</el-button>
  <el-table v-loading="loading" border :data="studentList" stripe style="width: 100%; padding-top: 20px">
    <el-table-column prop="id" label="ID" width="180" align="center" header-align="center" />
    <el-table-column prop="name" label="姓名" width="180" align="center" header-align="center" />
    <el-table-column prop="birthday" label="出生日期" width="150" align="center" header-align="center" />
    <el-table-column prop="description" label="描述" width="400" align="center" header-align="center" />
    <el-table-column prop="avgScore" label="平均分" width="100" align="center" header-align="center" />
    <el-table-column label="操作" width="200" align="center" header-align="center">
      <template #default="scope">
        <el-button @click="handleUpdate(scope.row)" type="primary">修改</el-button>
        <el-button @click="handleDelete(scope.row)" type="danger">删除</el-button>
      </template>
    </el-table-column>
  </el-table>


  <el-pagination background
                 layout="prev, pager, next"
                 :total="total"
                 :current-page="queryParams.pageNum"
                 :page-size="queryParams.pageSize"
                 @current-change="handleCurrentChange"
  />

  <el-dialog :title="title" v-model="open" width="800px" append-to-body>
    <el-form :model="form" :rules="rules" ref="formRef" :inline="true" label-width="68px">
      <el-form-item label="ID" prop="name">
        <el-input :disabled="!isCreate" v-model="form.id" placeholder="ID"/>
      </el-form-item>
      <el-form-item label="姓名" prop="name">
        <el-input v-model="form.name" placeholder="学生姓名"/>
      </el-form-item>
      <el-form-item label-width="100px" label="出生日期" prop="birthday">
        <el-date-picker v-model="form.birthday" value-format="YYYY-MM-DD" placeholder="出生日期"/>
      </el-form-item>
      <el-form-item label="描述" prop="description">
        <el-input v-model="form.description" placeholder="描述"/>
      </el-form-item>
      <el-form-item label="平均分" prop="avgScore">
        <el-input-number v-model="form.avgScore" :min="0" :max="100" placeholder="平均分"/>
      </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button type="primary" @click="submitForm">确 定</el-button>
      <el-button @click="cancel">取 消</el-button>
    </div>
  </el-dialog>

</div>
</template>

<style scoped>
.container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}
</style>
