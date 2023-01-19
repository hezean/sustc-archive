<template>
  <div>
    <head-menu class="header"></head-menu>
  </div>

  <el-container style="font-family: Cambria,sans-serif; font-size:20px">
    <el-main class="main_part">

      <div style="text-align: center;">
        <el-select v-model="value" placeholder="Sort By" @change="updateTag">
          <el-option
              v-for="item in options"
              :key="item.value"
              :label="item.label"
              :value="item.value">
          </el-option>
        </el-select>
        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        <el-select v-model="valuep" placeholder="Load Pages" @change="updateTag">
          <el-option
              v-for="item in optionsp"
              :key="item"
              :label="item"
              :value="item">
          </el-option>
        </el-select>
      </div>

      <el-row class="rows">
        <div id="chart_1" ref="ch_word_cloud" style="width:1400px; height:400px;"></div>

        <div id="nounVerbRadio">
          <el-radio-group ref="nounVerbs" v-model="nv.unit" size="middle" @change="changeWc">
            <el-radio-button label="Noun"/>
            <el-radio-button label="Verb"/>
          </el-radio-group>
        </div>
      </el-row>

      <el-row class="rows">
        <el-col span="50">
          <div id="chart_2" ref="ch_lang" style="width:550px; height:550px;"></div>
        </el-col>
        <el-col span="50">
          <div id="chart_3" ref="ch_exception" style="width:550px; height:550px;"></div>
        </el-col>
      </el-row>

    </el-main>
  </el-container>
</template>

<script type="text/javascript">
import HeadMenu from './util/HeadMenu'
import * as echarts from "echarts"
import "echarts-wordcloud/dist/echarts-wordcloud"
import "echarts-wordcloud/dist/echarts-wordcloud.min"

const axios = require('axios');
const axiosRetry = require('axios-retry');

axiosRetry(axios, {
  retries: 3,
  retryDelay: (retryCount) => {
    return retryCount * 100;
  },
})


let chart1 = null, chart2 = null, chart3 = null

let wc_noun = [], wc_verb = []
let hint = ''

let lang_data = [], exception_data = []


export default {
  name: 'StackOverflow',
  components: {
    HeadMenu
  },
  mounted() {
    this.initLoad()

    this.getWc()
    this.getExps()
    this.getComp()
  },
  data() {
    return {
      nv: {
        unit: 'Noun',
        selectedUnit: 'Noun'
      },
      options: [{
        value: 'MostFrequent',
        label: 'Most Frequent'
      }, {
        value: 'MostVotes',
        label: 'Highest Score'
      }, {
        value: 'Newest',
        label: 'Newest'
      }, {
        value: 'RecentActivity',
        label: 'Recent Activity'
      }],
      value: 'MostFrequent',
      optionsp: [10, 20, 30],
      valuep: 10
    }
  },
  methods: {
    async getWc(tab = 'MostFrequent', pgs = 20) {
      let url = '/api/so/questionWC?api=/questions/tagged/java&tab=' + tab + '&pages=' + pgs + '&noun='
      hint = '[sort by: ' + tab + ', samples: ' + pgs * 50 + ']'

      axios.get(url + 'true').then(resp => {
        wc_noun = resp.data[0]
        this.initWC()
      }).catch(e => this.alertBox(e))

      axios.get(url + 'false').then(resp => wc_verb = resp.data[0])
          .catch(e => this.alertBox(e))
    },
    async getExps(tab = 'MostFrequent', pgs = 20) {
      axios.get('/api/so/exps?tab=' + tab + '&pages=' + pgs).then(resp => {
            exception_data = resp.data[0]
            this.initException()
          }
      ).catch(e => this.alertBox(e))
    },
    async getComp() {
      axios.get('/api/so/comp').then(
          resp => {
            lang_data = resp.data
            this.initLang()
          }
      ).catch(e => this.alertBox(e))
    },

    updateTag() {
      chart1.showLoading()
      chart3.showLoading()

      this.getWc(this.value, this.valuep)
      this.getExps(this.value, this.valuep)
    },

    initLoad() {
      chart1 = echarts.init(this.$refs.ch_word_cloud)
      chart2 = echarts.init(this.$refs.ch_lang)
      chart3 = echarts.init(this.$refs.ch_exception)

      chart1.showLoading()
      chart2.showLoading()
      chart3.showLoading()
    },

    changeWc() {
      if (this.nv.selectedUnit === 'Noun') {
        this.initWC(true)
      } else {
        this.initWC(false)
      }
    },

    alertBox(e) {
      this.$message({
        message: 'API call failed (' + e + ') after 3 retry attempts, please refresh.',
        type: 'warning'
      })
    },

    // FIXME
    initWC(noun = true) {
      let option = {
        title: [{
          text: 'Word Mentioned In Question Titles',
          subtext: (noun ? 'nouns  ' : 'verbs  ') + hint,
          left: 'center',
          backgroundColor: 'rgba(255, 255, 255, 0.6)'
        }],
        backgroundColor: 'white',
        tooltip: {
          show: true,
          formatter: '{b}: {c}'
        },
        series: [{
          type: 'wordCloud',
          gridSize: 4,
          data: noun ? wc_noun : wc_verb,
          sizeRange: [12, 70],
          rotationRange: [0, 0],
          layoutAnimation: true,
          textStyle: {
            fontWeight: 'bold',
            color: function () {
              let r, g, b;
              b = Math.round(Math.random() * 170 + 55);
              g = Math.round(Math.random() * 170 + 55);
              r = Math.round(Math.random() * 170 + 55);
              return 'rgb(' + [r, g, b].join(',') + ')';
            }
          },
          left: 'center',
          top: 'center'
        }],
      }

      chart1.setOption(option)
      chart1.hideLoading()
      chart1.on('dblclick', function (params) {
        window.open('https://stackoverflow.com/search?q=%5BJAVA%5D' + encodeURIComponent(params.name));
      })

      console.log(chart1.getOption())
    },

    initLang() {
      let dots = [], link_data = [];
      for (let i = 0; i < lang_data.length; i++) {
        lang_data[i]['opLang'] = lang_data[i]['opLang'].replace('%2b%2b', '++').replace('%23', '#')
        if (lang_data[i]['opLang'] !== 'java') {
          link_data.push({
            source: 'java',
            target: lang_data[i]['opLang'],
            lineStyle: {
              width: lang_data[i]['comp'] / 100 + 3,
              curveness: 0.2
            },
            value: lang_data[i]['comp']
          })
        }
        let v = lang_data[i]['self'] / 1600 + 10
        dots.push({
          name: lang_data[i]['opLang'],
          value: Math.round(lang_data[i]['self']),
          symbolSize: v,
          itemStyle: {
            shadowColor: 'rgba(0, 0, 0, 0.5)',
            shadowBlur: 10
          },
          label: {
            fontSize: 20,
            show: true
          }
        })
      }
      let option = {
        title: {
          text: "Java 🔗 Other Languages",
          subtext: "Questions tagged as 'java' and another language",
          top: "top",
          left: "center",
          backgroundColor: 'rgba(255, 255, 255, 0.6)'
        },
        tooltip: {
          formatter: '{b}: {c} questions'
        },
        legend: [{
          data: ["Frontend", "Backend"]
        }],
        animationDuration: 1000,
        animationEasingUpdate: "quinticInOut",
        series: [{
          type: "graph",
          layout: "circular",
          data: dots,
          links: link_data,
          select: {
            disabled: false
          }
        }]
      }
      chart2.setOption(option)
      chart2.hideLoading()
    },

    initException() {
      let dots = [], link_data = [];

      let scale = 1
      for (let i = 0; i < exception_data.length; i++) {
        if (exception_data[i]['name'] === 'exception') {
          scale = exception_data[i]['value'] / 15
          break
        }
      }
      for (let i = 0; i < exception_data.length; i++) {
        link_data.push({
          source: 'exception',
          target: exception_data[i]['name'],
          lineStyle: {
            width: exception_data[i]['value'] / scale + 3,
            curveness: 0.2
          },
          value: exception_data[i]['value']
        })
        dots.push({
          name: exception_data[i]['name'],
          value: exception_data[i]['value'],
          symbolSize: exception_data[i]['value'] / scale + 1,
          itemStyle: {
            shadowColor: 'rgba(0, 0, 0, 0.3)',
            shadowBlur: 5
          },
          label: {
            fontSize: 14,
            shadowBlur: 1,
            show: true
          }
        })
      }
      let option = {
        title: {
          text: "Frequently Asked Java Exceptions",
          subtext: hint,
          top: "top",
          left: "center",
          backgroundColor: 'rgba(255, 255, 255, 0.6)'
        },
        tooltip: {
          formatter: '{b}: {c} questions'
        },
        animationDuration: 1000,
        animationEasingUpdate: "quinticInOut",
        series: [{
          type: "graph",
          layout: "circular",
          data: dots,
          links: link_data,
          select: {
            disabled: false
          }
        }]
      }
      chart3.setOption(option)
      chart3.hideLoading()
    }
  }
}
</script>


<style scoped>
.rows {
  border-radius: 15px;
  background-clip: padding-box;
  margin: 40px auto;
  width: 1200px;
  padding: 15px 15px 15px 15px;
  box-shadow: 0 0 5px #cac6c6;
}

.el-message .el-message__content {
  font-family: Cambria, sans-serif;
}
</style>
