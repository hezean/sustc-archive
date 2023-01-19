<template>
  <div>
    <head-menu class="header"></head-menu>
  </div>
  <el-container style="font-family: Cambria,sans-serif;font-size: 20px">
    <el-aside width="200px" style="background-color: rgb(25,49,82)">
      <AsideMenu></AsideMenu>
    </el-aside>
    <el-main class="main_part">
      <el-row class="row_search">
        <el-col :span="60">
          <el-autocomplete
              id="searchBar"
              select-when-unmatched
              placeholder="search"
              :fetch-suggestions="getSearchSuggestions"
              @select="getData"
              v-model="state">
            <template #prefix>
              <el-icon class="el-autocomplete__icon">
                <search/>
              </el-icon>
            </template>
          </el-autocomplete>
        </el-col>
        <el-col>
          <el-button id="force" type="primary" @click="forceUpdateRepo">ForceUpdate</el-button>
        </el-col>
      </el-row>
      <br/>
      <br/>
      <el-row>
        <el-descriptions
            class="margin-top"
            :column="3"
            :size="size"
            border
        >
          <el-descriptions-item>
            <template #label>
              <div class="cell-item">
                Owner
              </div>
            </template>
            <div id="meta_owner_name"></div>
          </el-descriptions-item>
          <el-descriptions-item>
            <template #label>
              <div class="cell-item">
                Stars
              </div>
            </template>
            <div id="meta_stars"></div>
          </el-descriptions-item>
          <el-descriptions-item>
            <template #label>
              <div class="cell-item">
                Description
              </div>
            </template>
            <div id="meta_description"></div>
          </el-descriptions-item>

          <el-descriptions-item>
            <template #label>
              <div class="cell-item">
                Language
              </div>
            </template>
            <el-tag id="meta_lang" size="medium"></el-tag>
          </el-descriptions-item>

          <el-descriptions-item>
            <template #label>
              <div class="cell-item">
                Forks
              </div>
            </template>
            <div id="meta_forks"></div>
          </el-descriptions-item>

          <el-descriptions-item>
            <template #label>
              <div class="cell-item">
                Update at
              </div>
            </template>
            <div id="meta_update_at"></div>
          </el-descriptions-item>
        </el-descriptions>
        <img id="ava" width="120" height="120" src="" alt="Avatar"/>
      </el-row>
      <el-row class="rows">
        <br/>
        <br/>
        <div id="timeUnitsRadio" style="visibility: hidden">
          Time Units Selection:
          <br/><br/>
          <el-radio-group ref="timeUnit" v-model="timeUnits.unit" size="large" @change="changeTimeUnit">
            <el-radio-button label="Year"/>
            <el-radio-button label="Month"/>
            <el-radio-button label="Day"/>
          </el-radio-group>
        </div>
        <br/>
        <br/>
        <div style="position: relative;top: 30px; left: 600px">
          <el-button id="download_1" type="plain" @click="download_1(1)">
            Download Data
          </el-button>
        </div>
        <div id="main1" ref="ch_open_close" style="width: 1200px;height:400px;"></div>
      </el-row>
      <el-row><br/><br/><br/></el-row>
      <el-row class="rows">
        <div style="position: relative; left: 1000px">
          <el-button id="download_2" type="plain" @click="download_1(2)">
            Download Data
          </el-button>
        </div>
        <div id="main2" ref="ch_pred" style="width: 1200px;height:600px;"></div>
      </el-row>
      <el-row><br/><br/><br/></el-row>
      <el-row class="rows">
        <div style="position: relative; left: 1000px">
          <el-button id="download_3" type="plain" @click="download_1(3)">
            Download Data
          </el-button>
        </div>
        <div id="main4" ref="ch_pie" style="width: 1300px;height:300px;"></div>
        <div id="main3" ref="ch_label" style="width: 1300px;height:400px;"></div>
      </el-row>
      <br/>
      <el-row class="rows"><br/>
        <br/>
        <div id="nounVerbRadio" style="visibility: hidden">
          Nouns or Verbs Selection:
          <br/><br/>
          <el-radio-group ref="nounVerbs" v-model="nounVerb.unit" size="large" @change="changeNounVerb">
            <el-radio-button label="Noun"/>
            <el-radio-button label="Verb"/>
          </el-radio-group>
        </div>
        <div style="position: relative; top: 30px; left: 700px">
          <el-button id="download_4" type="plain" @click="download_1(4)">
            Download Data
          </el-button>
        </div>
        <br/>
        <div id="word_cloud" ref="w_c" style="width: 1200px;height: 600px;"></div>
      </el-row>

      <el-row><br/><br/><br/></el-row>
      <el-row class="rows">
        <div style="position: relative; left: 1000px">
          <el-button id="download_5" type="plain" @click="download_1(5)">
            Download Data
          </el-button>
        </div>
        <div id="world_map" ref="w_map" style="width: 1200px;height: 600px;"></div>
      </el-row>
      <el-row>
        commits without country info:
        <div id="NA"></div>
      </el-row>
    </el-main>
  </el-container>
</template>

<script type="text/javascript">
import {Search} from '@element-plus/icons'
import HeadMenu from './util/HeadMenu'
import axios from "axios";
import * as echarts from "echarts";
import "echarts-wordcloud/dist/echarts-wordcloud";
import "echarts-wordcloud/dist/echarts-wordcloud.min";
import AsideMenu from "@/components/util/AsideMenu";
import Download from "/node_modules/downloadjs/download.js";

let search_query = [];

let month_openIssues, month_closeIssues,
    year_openIssues, year_closeIssues,
    day_openIssues, day_closeIssues,
    pred_open, real_open,
    noun_cloud = [], verb_cloud = [], cloud_chart_cache = null,
    label_raw = [], label_issues = [], labels_all = [], label_descrip = null;//, label_colors = null;
let label_series = [], pie_data = [];
let map_data = [], NAnum;
let open_close_Issues = [];
let pROpen = [];
let que;

let chart1 = null, chart2 = null, chart3 = null, chart4 = null, chart5 = null, chart6 = null;
let image = new Image();
image.src = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNDg0IiBoZWlnaHQ9IjIwOS45OTk5OTk5OTk5OTk5NyIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KIDwhLS0gQ3JlYXRlZCB3aXRoIE1ldGhvZCBEcmF3IC0gaHR0cDovL2dpdGh1Yi5jb20vZHVvcGl4ZWwvTWV0aG9kLURyYXcvIC0tPgogPGRlZnM+CiAgPGZpbHRlciBoZWlnaHQ9IjIwMCUiIHdpZHRoPSIyMDAlIiB5PSItNTAlIiB4PSItNTAlIiBpZD0ic3ZnXzFfYmx1ciI+CiAgIDxmZUdhdXNzaWFuQmx1ciBzdGREZXZpYXRpb249IjAiIGluPSJTb3VyY2VHcmFwaGljIi8+CiAgPC9maWx0ZXI+CiA8L2RlZnM+CiA8Zz4KICA8dGl0bGU+YmFja2dyb3VuZDwvdGl0bGU+CiAgPHJlY3QgZmlsbD0iI2ZmZiIgaWQ9ImNhbnZhc19iYWNrZ3JvdW5kIiBoZWlnaHQ9IjIxMiIgd2lkdGg9IjQ4NiIgeT0iLTEiIHg9Ii0xIi8+CiAgPGcgZGlzcGxheT0ibm9uZSIgb3ZlcmZsb3c9InZpc2libGUiIHk9IjAiIHg9IjAiIGhlaWdodD0iMTAwJSIgd2lkdGg9IjEwMCUiIGlkPSJjYW52YXNHcmlkIj4KICAgPHJlY3QgZmlsbD0idXJsKCNncmlkcGF0dGVybikiIHN0cm9rZS13aWR0aD0iMCIgeT0iMCIgeD0iMCIgaGVpZ2h0PSIxMDAlIiB3aWR0aD0iMTAwJSIvPgogIDwvZz4KIDwvZz4KIDxnPgogIDx0aXRsZT5MYXllciAxPC90aXRsZT4KICA8dGV4dCBmaWx0ZXI9InVybCgjc3ZnXzFfYmx1cikiIGZvbnQtd2VpZ2h0PSJib2xkIiBzdHJva2U9IiM3MjlDNjIiIHRyYW5zZm9ybT0ibWF0cml4KDkuMjY0NzU4NzUwMjU3NjcsMCwwLDguNzE1MTk5NDY1OTYwMDQsLTE1MDEuOTQ3MTUxMjI1NTE2OCwtNjgxLjIwMDA3MDUxMzg2MDQpICIgeG1sOnNwYWNlPSJwcmVzZXJ2ZSIgdGV4dC1hbmNob3I9InN0YXJ0IiBmb250LWZhbWlseT0iQXJ2bywgc2Fucy1zZXJpZiIgZm9udC1zaXplPSIyNCIgaWQ9InN2Z18xIiB5PSI5OC40MjA4NjkiIHg9IjE2Mi42MDQyMjkiIGZpbGw9IiM3MjlDNjIiPkphdmE8L3RleHQ+CiA8L2c+Cjwvc3ZnPg=='
export default {
  // eslint-disable-next-line vue/multi-word-component-names
  name: "Github",
  components: {
    AsideMenu, HeadMenu, Search
  },
  created() {
    chart1 = null
    chart2 = null
    chart3 = null
    chart4 = null
    chart5 = null
    chart6 = null
  },

  data() {
    return {
      active_side: 'github',
      owner: '',
      repo_name: '',
      search_suggestions: [],
      predict_issues: [],
      search_word_cloud: '',
      state: '',
      timeUnits: {
        unit: 'Month',
        selectedUnit: 'Month'
      },
      nounVerb: {
        unit: 'Noun',
        selectedUnit: 'Noun'
      }
    }
  },

  methods: {

    download_1(val) {
      let name = "open_close_by_" + this.timeUnits.selectedUnit.toLowerCase() + ".json"
      switch(val) {
        case 1:
          Download(JSON.stringify(open_close_Issues), name, "application/json")
          break
        case 2:
          Download(JSON.stringify(pred_open), "prediction.json", "application/json")
          Download(JSON.stringify(real_open), "real.json", "application/json")
          break
        case 3:
          Download(JSON.stringify(label_issues), "label_issues.json", "application/json")
          break
        case 4:
          Download(JSON.stringify(noun_cloud), "nouns.json", "application/json")
          Download(JSON.stringify(verb_cloud), "verbs.json", "application/json")
          break
        case 5: Download(JSON.stringify(map_data), "countries_data.json", "application/json")
          break
      }
    },

    forceUpdateRepo() {
      console.log('force')
      document.getElementById('force').disabled = true;
      console.log(que + "      is que ?")
      axios.get('/api/gh/forceUpdate1', {
        params: {
          fullName: que
        }
      }).then(() => {
            console.log('success')
            document.getElementById('force').disabled = false;
          }
      ).catch(e => {
        console.log(e)
        document.getElementById('force').loading = false;
      })
    },

    fill_both_word(resp_noun, resp_verb) {
      noun_cloud = []
      verb_cloud = []
      let temp = resp_noun.data
      for (let w in temp) {
        noun_cloud.push({'name': w, 'value': temp[w]})
      }
      temp = resp_verb.data
      for (let w in temp) {
        verb_cloud.push({'name': w, 'value': temp[w]})
      }
      if (chart3 == null)
        this.init_word_cloud()
      else
        this.update_word_cloud()
      document.getElementById('nounVerbRadio').style.visibility = 'visible';
    },

    changeNounVerb(val) {
      this.nounVerb.selectedUnit = val;
      this.use_cache_update_word_cloud();
    },

    changeTimeUnit(val) {
      if (val === 'Month')
        this.fill_open_close(month_openIssues, month_closeIssues);
      else if (val === 'Year')
        this.fill_open_close(year_openIssues, year_closeIssues);
      else
        this.fill_open_close(day_openIssues, day_closeIssues);
      this.timeUnits.selectedUnit = val;
      this.update_chart1_data();
    },

    async getMon(query) {
      que = query['value']
      return axios
          .get('/api/gh/issues1', {
            params: {
              fullName: query['value'],
              interval: "Month"
            }
          })
          .catch(e => {
            console.log(e)
            month_openIssues = [];
            month_closeIssues = [];
          })
    },
    async getYear(query) {
      return axios.get('/api/gh/issues1', {
        params: {
          fullName: query['value'],
          interval: "Year"
        }
      })
          .catch(e => {
            year_openIssues = [];
            year_closeIssues = [];
            console.log(" get year error " + e);
          })
    },
    async getDay(query) {
      return axios.get('/api/gh/issues1', {
        params: {
          fullName: query['value'],
          interval: "Day"
        }
      })
          .catch(e => {
            day_openIssues = [];
            day_closeIssues = [];
            console.log(" get day error " + e);
          })
    },

    init_update_on_got(resp_mon, resp_year, resp_day) {
      month_openIssues = resp_mon.data["openIssues"]
      month_closeIssues = resp_mon.data["closeIssues"]
      year_openIssues = resp_year.data["openIssues"]
      year_closeIssues = resp_year.data["closeIssues"]
      day_openIssues = resp_day.data["openIssues"]
      day_closeIssues = resp_day.data["closeIssues"]

      if (chart1 == null) {
        this.initEchart_1();
      } else {
        this.changeTimeUnit(this.timeUnits.selectedUnit);
      }
      document.getElementById("timeUnitsRadio").style.visibility = "visible";
    },

    async getData(query) {
      if (chart1 != null) {
        chart1.showLoading({
          text: 'loading',
          maskColor: 'rgba(255,255,255,0.5)'
        })
      }
      if (chart2 != null) {
        chart2.showLoading({
          text: 'loading',
          maskColor: 'rgba(255,255,255,0.5)'
        })
      }
      if (chart4 != null) {
        chart4.showLoading({
          text: 'loading',
          maskColor: 'rgba(255,255,255,0.5)'
        })
      }
      if (chart5 != null) {
        chart5.showLoading({
          text: 'loading',
          maskColor: 'rgba(255,255,255,0.5)'
        })
      }
      if (chart6 != null) {
        chart6.showLoading({
          text: 'loading',
          maskColor: 'rgba(255,255,255,0.5)'
        })
      }

      document.getElementById('meta_description').innerText = ''
      document.getElementById('meta_owner_name').innerText = ''
      document.getElementById('meta_stars').innerText = ''
      document.getElementById('meta_forks').innerText = ''
      document.getElementById('meta_lang').innerText = ''
      document.getElementById('meta_update_at').innerText = ''
      document.getElementById('ava').src = ''
      search_query = query['value'].toString().split('/');

      axios.get('/api/gh/metadata1', {
        params: {fullName: query['value']}
      }).then(resp => {
        let meta = resp.data
        let owner = meta['owner']
        document.getElementById('meta_description').innerText = meta['description']
        document.getElementById('meta_owner_name').innerText = owner['name']
        document.getElementById('meta_stars').innerText = meta['stars']
        document.getElementById('meta_forks').innerText = meta['forks']
        document.getElementById('meta_lang').innerText = meta['language']
        document.getElementById('meta_update_at').innerText = meta['updateAt']
        document.getElementById('ava').src = owner['avatar_url']
      })

      axios.all([this.getMon(query), this.getYear(query), this.getDay(query)])
          .then(axios.spread((resp_mon, resp_year, resp_day) => this.init_update_on_got(resp_mon, resp_year, resp_day)))

      axios.all([
        axios
            .get('/api/gh/issueTitleWC1', {
              params: {
                fullName: query['value'],
                noun: true,
              }
            }),
        axios
            .get('/api/gh/issueTitleWC1', {
              params: {
                fullName: query['value'],
                noun: false,
              }
            })
      ]).then(axios.spread((resp_noun, resp_verb) => this.fill_both_word(resp_noun, resp_verb)))


      axios.get('/api/gh/predict1', {
        params: {
          fullName: query['value']
        }
      }).then(resp => {
        console.log(" pred got")
        this.predict_issues = resp.data
        pred_open = this.predict_issues["pred"]
        real_open = this.predict_issues["real"]
        if (chart2 == null)
          this.initEchart_2()
        else
          this.update_chart2_data()
      }).catch(e => {
        console.log(e)
        pred_open = [];
        real_open = [];
      })

      axios.get('/api/gh/labels1', {
        params: {
          fullName: query['value']
        }
      })
          .then(resp => {
            label_raw = resp.data
            this.fill_label_data()

            if (chart4 == null)
              this.initLabelChart()
            else
              this.updateLabelChart()
          })

      axios.get('/api/gh/contributors1', {
        params: {fullName: query['value']}
      }).then(resp => {
        map_data = [];
        let ret = resp.data;
        console.log(ret)
        for (let i in ret) {
          if (i === 'N/A') {
            NAnum = ret[i]
            continue
          }
          map_data.push({
            'name': i,
            'value': ret[i]
          })
        }

        document.getElementById('NA').innerText = NAnum
        if (chart6 == null)
          this.initMap()
        else
          this.updateMap()
      }).catch(e => {
        console.log('map data error')
        console.log(e)
      })

    },

    updateMap() {
      let option = chart6.getOption()
      option.series[0].data = map_data;
      chart6.hideLoading()
      chart6.setOption(option)
    },

    initMap() {
      chart6 = echarts.init(this.$refs.w_map);
      let option = {
        title: {
          text: 'Contributors in the World',
          left: 'center',
          top: '0%'
        },
        tooltip: {
          show: true
        },
        series: [
          {
            type: 'pie',
            data: map_data,
            radius: ['40%', '70%']
          }
        ]

      };
      chart6.hideLoading()
      chart6.setOption(option);
    },

    init_word_cloud() {
      chart3 = echarts.init(document.getElementById('word_cloud'));
      let option_word = {
        title: {
          text: "Word Cloud of Issues Title",
          left: 'center',
          top: '0%'
        },
        backgroundColor: 'white',
        tooltip: {
          show: true,
          formatter: '{b}: {c}'
        },
        series: [{
          type: 'wordCloud',
          gridSize: 4,
          sizeRange: [12, 70],
          rotationRange: [-45, 0, 45, -90],
          maskImage: image,
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
          top: 'center',
          right: null,
          bottom: null,
          data: noun_cloud
        }],
      }
      chart3.setOption(option_word);
      chart3.on('dblclick', function (params) {
        window.open('https://github.com/' + encodeURIComponent(search_query[0]) + '/' + encodeURIComponent(search_query[1]) + '/issues?q=is%3Aissue+' + encodeURIComponent(params.name));
      });
    },

    use_cache_update_word_cloud() {
      let temp = chart3;
      if (cloud_chart_cache != null) {
        chart3 = cloud_chart_cache;
      } else {
        this.update_word_cloud();
      }
      cloud_chart_cache = temp;
    },

    update_word_cloud() {
      cloud_chart_cache = null
      let option3 = chart3.getOption();
      if (this.nounVerb.selectedUnit === 'Noun')
        option3.series[0].data = noun_cloud;
      else
        option3.series[0].data = verb_cloud;
      chart3.setOption(option3);
      console.log(" w c length = " + option3.series.data.length)
    },

    fill_open_close(openIssues, closeIssues) {
      open_close_Issues = null;
      open_close_Issues = [];
      for (let i = 0; i < openIssues.length; i++) {
        let oI = {"time": openIssues[i]['time'], "openNum": openIssues[i]['amount']};
        open_close_Issues.push(oI);
      }
      for (let j = 0; j < closeIssues.length; j++) {
        let cI = {"time": closeIssues[j]['time'], "closeNum": closeIssues[j]['amount']};
        open_close_Issues.push(cI);
      }
    },

    update_chart2_data() {
      chart2.showLoading({
        text: 'loading',
        maskColor: 'rgba(255,255,255,0.5)'
      })
      pROpen = [];
      for (let p in pred_open) {
        let pI = {"time": p, "predOpen": pred_open[p]};
        pROpen.push(pI);
      }
      for (let r in real_open) {
        let rI = {"time": r, "realOpen": real_open[r]};
        pROpen.push(rI);
      }
      let op2 = chart2.getOption();
      op2.dataset[0] = {
        dimensions: ['time', 'predOpen', 'realOpen'],
        source: pROpen
      };
      chart2.hideLoading();
      chart2.setOption(op2)
      console.log("update 2 pred data over--------")
    },

    update_chart1_data() {
      chart1.showLoading({
        text: 'loading',
        maskColor: 'rgba(255,255,255,0.5)'
      })
      let op1 = chart1.getOption();
      op1.dataset[0] = {
        dimensions: ['time', 'openNum', 'closeNum'],
        source: open_close_Issues
      };
      chart1.hideLoading();
      chart1.setOption(op1);
      console.log("update 1 data over-------------")
    },

    initEchart_2() {
      chart2 = echarts.init(this.$refs.ch_pred)
      chart2.showLoading({
        text: 'loading',
        maskColor: 'rgba(255,255,255,0.5)'
      })
      for (let p in pred_open) {
        let pI = {"time": p, "predOpen": pred_open[p]};
        pROpen.push(pI);
      }
      for (let r in real_open) {
        let rI = {"time": r, "realOpen": real_open[r]};
        pROpen.push(rI);
      }

      let ds_2 = {
        dimensions: ['time', 'predOpen', 'realOpen'],
        source: pROpen
      };
      let option2 = {
        title: {
          text: "Issues Open Linear Regression",
          left: 'center',
          top: '0%'
        },
        legend: {
          top: '5%'
        },
        xAxis: {
          type: 'time',
        },
        yAxis: {},
        dataZoom: [
          {
            xAxisIndex: 0
          },
          {
            yAxisIndex: 0
          },
          {
            type: 'inside'
          }
        ],
        tooltip: {
          trigger: 'item',
          axisPointer: {type: 'cross'},
          // show: true,
          // showContent: true
        },
        dataset: [
          ds_2,
          {
            transform: [
              {
                type: 'sort',
                config: [
                  {dimension: 'time', order: 'asc'}
                ]
              }
            ]
          }
        ],
        series: [
          {
            name: 'Regression Open',
            type: 'line',
            smooth: true,
            connectNulls: true,
            symbol: 'circle',
            // showSymbol: false,
            symbolSize: 1,
            datasetIndex: 1,
            encode: {
              x: 'time',
              y: 'predOpen'
            },
            itemStyle: {
              color: {
                type: 'linear',
                x: 0,
                y: 0,
                x2: 0,
                y2: 1,
                colorStops: [{
                  offset: 0, color: 'blue' // 0%
                }, {
                  offset: 1, color: 'blue' // 100%
                }],
                global: false
              }
            },
            emphasis: {
              itemStyle: {
                shadowBlur: 2,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            }
          },
          {
            name: 'Real Open',
            type: 'line',
            smooth: true,
            connectNulls: true,
            symbol: 'circle',
            // showSymbol: false,
            symbolSize: 3,
            datasetIndex: 1,
            encode: {
              x: 'time',
              y: 'realOpen'
            },
            itemStyle: {
              color: {
                type: 'linear',
                x: 0,
                y: 0,
                x2: 0,
                y2: 1,
                colorStops: [{
                  offset: 0, color: 'orange' // 0%
                }, {
                  offset: 1, color: 'orange' // 100%
                }],
                global: false
              }
            },
            emphasis: {
              itemStyle: {
                shadowBlur: 2,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            }
          }
        ]
      };
      chart2.hideLoading();
      chart2.setOption(option2);
    },

    initEchart_1() {
      console.log('update chart 1 -------------' + month_openIssues.length)
      chart1 = echarts.init(this.$refs.ch_open_close)
      chart1.showLoading({
        text: 'loading',
        maskColor: 'rgba(255,255,255,0.5)'
      })
      this.fill_open_close(month_openIssues, month_closeIssues);
      let ds_1 = {
        dimensions: ['time', 'openNum', 'closeNum'],
        source: open_close_Issues
      };
      let option1 = {
        title: {
          text: 'Number of Issues',
          left: 'center',
          top: '0%'
        },
        legend: {
          top: '5%'
        },
        xAxis: {
          type: 'time',
        },
        yAxis: {},
        dataZoom: [
          {
            xAxisIndex: 0
          },
          {
            yAxisIndex: 0
          },
          {
            type: 'inside'
          }
        ],
        tooltip: {
          trigger: 'item',
          axisPointer: {type: 'cross'}
        },
        dataset: [
          ds_1,
          {
            transform: [
              {
                type: 'sort',
                config: [
                  {dimension: 'time', order: 'asc'}
                ]
              }
            ]
          }
        ],
        series: [
          {
            name: 'openNum',
            type: 'bar',
            datasetIndex: 1,
            encode: {
              x: 'time',
              y: 'openNum'
            },
            itemStyle: {
              color: {
                type: 'linear',
                x: 0,
                y: 0,
                x2: 0,
                y2: 1,
                colorStops: [{
                  offset: 0, color: 'rgb(14,215,70)' // 0%
                }, {
                  offset: 1, color: 'rgb(97,255,233)' // 100%
                }],
                global: false
              }
            },
            emphasis: {
              itemStyle: {
                shadowBlur: 20,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            }
          },
          {
            name: 'closeNum',
            type: 'bar',
            datasetIndex: 1,
            encode: {
              x: 'time',
              y: 'closeNum'
            },
            itemStyle: {
              color: {
                type: 'linear',
                x: 0,
                y: 0,
                x2: 0,
                y2: 1,
                colorStops: [{
                  offset: 0, color: 'rgb(116,187,232)' // 0%
                }, {
                  offset: 1, color: 'rgb(35,76,129)' // 100%
                }],
                global: false
              }
            },
            emphasis: {
              itemStyle: {
                shadowBlur: 20,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            }
          }
        ]
      };
      chart1.hideLoading();
      chart1.setOption(option1);
      // chart1.on('dblclick', function (params) {
      //   window.open('https://www.baidu.com/s?wd=' + encodeURIComponent(params.name));
      // });
      console.log('init chart 1 over---------------' + month_openIssues.length)
    },

    fill_label_data() {
      label_issues = [];
      labels_all = ['time'];
      label_series = [];
      label_descrip = {};
      pie_data = [];
      // label_colors = {};

      for (let i = 0, l; i < label_raw.length; i++) {
        l = label_raw[i];
        if (l['data'].length < 5) continue;
        let lb = l['name']
        labels_all.push(lb)
        pie_data.push({
          name: lb,
          value: l['data3']
        })
        label_series.push({
          name: lb,
          type: 'line',
          smooth: true,
          connectNulls: true,
          itemStyle: {
            color: '#' + l['color']
          },
          // seriesLayoutBy: 'row',
          emphasis: {focus: 'series'},
          datasetIndex: 1,
          encode: {
            x: 'time',
            y: lb
          }
        });
        label_descrip[lb] = l['description']
        // label_colors[lb] = l['color']
        for (let j = 0; j < l['data'].length; j++) {
          let temp;
          temp = {'time': l['data'][j]['time']};
          temp[lb] = l['data'][j]['amount'];
          label_issues.push(temp);
        }
      }
      // label_series.push(
      //       {
      //         type: 'pie',
      //         id: 'pie',
      //         radius: '30%',
      //         center: ['50%', '25%'],
      //         emphasis: {
      //           focus: 'self'
      //         },
      //         label: {
      //           formatter: '{a}:{b}: {c}:  ({d}%)'
      //         },
      //         datasetIndex: 1,
      //         encode: {
      //           itemName: 'labels current percentage',
      //           value: labels_all[1],
      //           tooltip: labels_all[1]
      //         }
      //       }
      // )
      // console.log(label_issues)
    },

    updateLabelChart() {
      let option4 = chart4.getOption();
      option4.dataset[0].dimensions = labels_all;
      option4.dataset[0].source = label_issues;
      option4.series = label_series;

      let option5 = chart5.getOption();
      option5.series[0].data = pie_data;
      chart4.hideLoading();
      chart5.hideLoading();
      chart5.setOption(option5);
      chart4.setOption(option4);
    },

    initLabelChart() {
      chart4 = echarts.init(this.$refs.ch_label);
      chart5 = echarts.init(this.$refs.ch_pie);
      let option4 = {
        title: {
          text: 'Labels of Issues',
          left: 'center',
          top: '0%'
        },
        backgroundColor: 'rgb(69,73,74)',
        legend: {
          textStyle: {
            color: 'white'
          },
          top: '8%'
        },
        tooltip: {
          trigger: 'axis',
          showContent: false
        },
        dataZoom: [
          {
            xAxisIndex: 0
          },
          {
            yAxisIndex: 0
          },
          {
            type: 'inside'
          }
        ],
        dataset: [{
          dimensions: labels_all,
          source: label_issues
          //     [
          //   ['label', '2012', '2013', '2014', '2015', '2016', '2017'],
          //   ['bugs', 56.5, 82.1, 88.7, 70.1, 53.4, 85.1],
          //   ['a', 51.1, 51.4, 55.1, 53.3, 73.8, 68.7],
          //   ['b', 40.1, 62.2, 69.5, 36.4, 45.2, 32.5],
          //   ['c', 25.2, 37.1, 41.2, 18, 33.9, 49.1]
          // ]
        },
          {
            transform: [
              {
                type: 'sort',
                config: [
                  {dimension: 'time', order: 'asc'}
                ]
              }
            ]
          }
        ],
        xAxis: {type: 'time'},
        yAxis: {
          // gridIndex: 0
        },
        grid: {top: '45%'},
        series: label_series
        //     [
        //   {
        //     type: 'line',
        //     smooth: true,
        //     // seriesLayoutBy: 'row',
        //     emphasis: {focus: 'series'},
        //     encode: {
        //       x: 'time',
        //       y: 'ILP'
        //     }
        //   },
        //   {
        //     type: 'line',
        //     smooth: true,
        //     // seriesLayoutBy: 'row',
        //     emphasis: {focus: 'series'}
        //   },
        //   {
        //     type: 'line',
        //     smooth: true,
        //     // seriesLayoutBy: 'row',
        //     emphasis: {focus: 'series'}
        //   },
        //   {
        //     type: 'line',
        //     smooth: true,
        //     seriesLayoutBy: 'row',
        //     emphasis: {focus: 'series'}
        //   },
        //   {
        //     type: 'pie',
        //     id: 'pie',
        //     radius: '30%',
        //     center: ['50%', '25%'],
        //     emphasis: {
        //       focus: 'self'
        //     },
        //     label: {
        //       formatter: '{b}:  ({d}%)'
        //     },
        //     encode: {
        //       itemName: 'issue',
        //       value: labels_all[1],
        //       tooltip: labels_all[1]
        //     }
        //   }
        // ]
      };
      let option5 = {
        title: {
          text: 'Current Labels\nPercentage',
          left: 'center',
          top: 'center'
        },
        tooltip: {
          show: true
        },
        series: [
          {
            type: 'pie',
            data: pie_data,
            radius: ['40%', '70%']
          }
        ]
      };
      // chart4.on('updateAxisPointer', function (event) {
      //   const xAxisInfo = event.axesInfo[0];
      //   if (xAxisInfo) {
      //     const dimension = xAxisInfo.value;
      //     console.log( ' change?? ' + dimension)
      //     chart4.setOption({
      //       series: {
      //         id: 'pie',
      //         label: {
      //           // formatter: '{b}: {@[' + dimension + ']} ({d}%)'
      //         },
      //         encode: {
      //           value: dimension,
      //           tooltip: dimension
      //         }
      //       }
      //     });
      //   }
      // });
      chart4.hideLoading();
      chart5.hideLoading();

      chart4.group = 'group1'
      chart5.group = 'group1'

      echarts.connect('group1')

      chart5.setOption(option5);
      chart4.setOption(option4);
    },

    async getSearchSuggestions(queryString, cb) {
      axios
          .get('/api/gh/fuzzy', {
            params: {
              q: queryString
            }
          })
          .then(resp => {
            console.log(" suggestions got ")
            this.search_suggestions = resp.data
            cb(this.search_suggestions)
          })
          .catch(() => {
          })
    },
  }
}

</script>


<style scoped>

.rows {
  border-radius: 15px;
  background-clip: padding-box;
  margin: 40px auto;
  width: 1200px;
  padding: 15px 35px 15px 55px;
  /*border: 1px solid #eaeaea;*/
  border: 1px solid #eaeaea;
  box-shadow: 0 0 25px #cac6c6;
}

.row_search {
  border-radius: 15px;
  background-clip: padding-box;
  /*margin: 90px auto;*/
  /*width: 400px;*/
  padding: 15px 35px 15px 55px;
  /*border: 1px solid #eaeaea;*/
  border: 1px solid #eaeaea;
  box-shadow: 0 0 25px #cac6c6;
}

.row_search /deep/ .el-input {
  width: 160%;
}

</style>