<script>
import axios from "axios";

export default {
  data() {
    return {

      testResult:"",

      testModel:{
        plat:null,
        cmd:"",
        shPath:"/bin/bash",
      },

      activeName:"first",
      parsingRecord:{
        serviceProvider:null,
        serviceProviderName:"",
        serviceProviderId:"",
        serviceProviderSecret:"",
        recordType:null,
        getIpMode:null,
        domain:"",
        updateFrequency:null,
        cmd:"",
        ip:null,
        plat: null,
        shPath:"",
      },
      tableData:[
        {
          serviceProviderName:"腾讯云",
          recordTypeName:"AAAA",
          updateFrequencyName:"10分钟",
          domain:"www.baidu.com",
          ip:"127.0.0.1"

        }
      ],

      logList:[],

    }
  },

  props: {},

  methods: {

    addRecord(provider){

      this.parsingRecord.serviceProvider=provider
      axios.post("/addRecord",this.parsingRecord).then((res)=>{

        if(res.data.success){
          this.$message({
            showClose: true,
            message: '成功',
            type: 'success'
          });
          location.reload()

        }else {
          this.$message({
            showClose: true,
            message: '失败',
            type: 'error'
          });
        }


      })


    },
    getIp(){
      axios.post("/getIp",this.parsingRecord).then((res)=>{
        if(res.data.data)  this.parsingRecord=res.data.data

      })
    },
    test(){
      axios.post("/test",this.testModel).then((res)=>{

            this.testResult=res.data.data
      })



    },

    deleteRecord(index){
      axios.post("/deleteParsingRecords",this.tableData[index]).then((res)=>{

          if(res.data.success) location.reload()

      })

    }

  },
  created() {
    axios.post("/getParsingRecords",this.testModel).then((res)=>{

      this.tableData=res.data.data
    })


    axios.post("/log",this.testModel).then((res)=>{

      this.logList=res.data.data
    })
  }
}
</script>

<template>

  <div >
    <el-tabs  v-model="activeName" class="demo-tabs" >
      <el-tab-pane label="腾讯云" name="first">
        <el-form
            label-position="left"
            label-width="auto"
            :model="parsingRecord"
            style="max-width: 600px"
        >

          <el-form-item label="服务商ID" >
            <el-input v-model="parsingRecord.serviceProviderId" />
          </el-form-item>
          <el-form-item label="密钥" >
            <el-input v-model="parsingRecord.serviceProviderSecret" />
          </el-form-item>
          <el-form-item label="域名" >
            <el-input v-model="parsingRecord.domain" placeholder="主机名+域名 如www.baidu.com"/>
          </el-form-item>
          <el-form-item label="更新频率" >
            <el-select v-model="parsingRecord.updateFrequency">
              <el-option label="1分钟" :value="1"></el-option>
              <el-option label="5分钟" :value="2"></el-option>
              <el-option label="10分钟" :value="3"></el-option>
              <el-option label="30分钟" :value="4"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="解析方式" >
            <el-select v-model="parsingRecord.recordType">
              <el-option label="(A) Ipv4" :value="2"></el-option>
              <el-option label="(A.A.A.A) Ipv6" :value="1"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="IP地址获取方式" >
            <el-select v-model="parsingRecord.getIpMode">
              <el-option label="命令行" :value="1"></el-option>
              <el-option label="网卡" :value="2"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="命令" v-if="parsingRecord.getIpMode==1">
            <el-select v-model="parsingRecord.plat">
              <el-option label="linux" :value="0"></el-option>
              <el-option label="windows" :value="1"></el-option>
            </el-select>
            <el-form-item label="命令方式" v-if="parsingRecord.plat==0">
              <el-input  v-model="parsingRecord.shPath" ></el-input>
            </el-form-item>
            <el-input type="textarea" v-model="parsingRecord.cmd"></el-input>
          </el-form-item>
          <el-form-item label="ip地址">
            <el-input type="textarea" v-model="parsingRecord.ip" disabled></el-input>
            <el-button type="primary" @click="getIp">解析IP地址</el-button>
          </el-form-item>
          <el-form-item label=" ">
            <el-button type="primary" @click="addRecord(2)">添加记录</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>
      <el-tab-pane label="阿里云" name="second">开发中....</el-tab-pane>
      <el-tab-pane label="华为云" name="third">开发中....</el-tab-pane>
      <el-tab-pane label="说明" name="fourth">

          <h2>命令测试</h2>
          <el-form
              label-position="left"
              label-width="auto"
              :model="testModel"
              style="max-width: 600px"
          >
            <el-form-item label="平台" >
              <el-select v-model="testModel.plat" placeholder="linux or windows命令">
                <el-option label="linux" :value="0"></el-option>
                <el-option label="windows" :value="1"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="命令">
              <el-input type="textarea" v-model="testModel.cmd" placeholder="命令"></el-input>

            </el-form-item>
            <el-form-item label="命令方式" v-if="testModel.plat==0">
              <el-input  v-model="testModel.shPath" placeholder="linux脚本方式"></el-input>
            </el-form-item>
            <el-form-item label=" ">
              <el-button type="primary" @click="test">测试</el-button>
            </el-form-item>
          </el-form>
        <div style="width: 90%; min-height: 200px; border: 1px solid black; background-color: white;">

            {{testResult}}

        </div>

        <h2>日志</h2>
        <div style="border: 1px solid black; background-color: white;">
          <div v-for="item in logList" style="font-size: xx-small">{{item}}
            <el-divider></el-divider>
          </div>

        </div>
      </el-tab-pane>
    </el-tabs>
    <el-divider></el-divider>
    <h4>记录</h4>
    <el-table :data="tableData" style="width: 100%">
      <el-table-column fixed prop="serviceProviderName" label="服务商" width="150" />
      <el-table-column prop="recordTypeName" label="解析类型" width="120" />
      <el-table-column prop="updateFrequencyName" label="更新频率" width="120" />
      <el-table-column prop="ip" label="IP地址" width="120" />
      <el-table-column prop="domain" label="域名" width="600" />
      <el-table-column fixed="right" label="Operations" min-width="120">
        <template #default="scope">
          <el-button link type="primary" size="small"  @click.prevent="deleteRecord(scope.$index)">
            删除
          </el-button>
          <el-button link type="primary" size="small">修改</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>

</template>

<style scoped>

</style>
