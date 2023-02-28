<template>
  <div class="wrapper0">
    <tdesign-anim ani="layout"/>
    <t-divider style="margin-top: -8.32em; margin-bottom: 0"/>

    <div class="wrapper1">
      <a-page-header
          :title="$t('manage.list.open_courses')"
          :subtitle="$t('manage.list.open_courses1')"
          :show-back="false"
      />

      <div align="left" style="padding-left: 24px; padding-top: 25px; float: bottom">
        <t-button theme="primary" @click="formVisible = true">
          <icon-plus/>&nbsp;
          {{ $t("manage.list.add") }}
        </t-button>
      </div>
      <t-dialog
          :closeBtn="false"
          :footer="false"
          destroyOnClose
          :header="this.$t('manage.list.add')"
          showOverlay v-model:visible="formVisible"
          width="500px">

        <t-form :data="this.formData" :rules="this.formRules" scroll-to-first-error="smooth"
                labelWidth="100px" style="text-align: left" @submit="submitAdd">

          <t-form-item :label="this.$t('manage.add.code')" name="code">
            <t-input v-model="formData.code" placeholder="CS101"
                     :onChange="this.formData.code = this.formData.code.toUpperCase()"
            />
          </t-form-item>

          <t-form-item :label="this.$t('manage.add.name')" name="name">
            <t-input v-model="formData.name" placeholder="Introduction to Computer Science"/>
          </t-form-item>

          <t-form-item :label="this.$t('manage.add.lang')" name="lang">
            <t-radio-group variant="default-filled" v-model="formData.lang">
              <t-radio-button value="en">{{ $t("manage.add.lang_en") }}</t-radio-button>
              <t-radio-button value="zh">{{ $t("manage.add.lang_zh") }}</t-radio-button>
              <t-radio-button value="bi">{{ $t("manage.add.lang_bi") }}</t-radio-button>
            </t-radio-group>
          </t-form-item>

          <t-form-item :label="this.$t('manage.add.teacher')" name="teacher">
            <t-input v-model="formData.teacher" placeholder="JD Cheng"/>
          </t-form-item>

          <t-form-item :label="this.$t('manage.add.date')" name="date">
            <t-date-picker
                allowInput
                clearable
                format="YYYY/MM/DD"
                v-model="formData.date"
                :placeholder="this.$t('manage.add.date_prompt')"
                :disable-date="{
                  before: dayjs().add(-1, 'day').format(),
                }"
                style="width: 100%"
            />
          </t-form-item>

          <t-form-item :label="this.$t('manage.add.time')" name="time">
            <t-time-range-picker v-model="formData.time" clearable format="HH:mm" allow-input :steps="[1, 10, 1]"
                                 :placeholder="[this.$t('manage.add.time_st'), this.$t('manage.add.time_ed')]"
                                 :onChange="updateDuration"
            />
          </t-form-item>

          <t-form-item :label="this.$t('manage.add.duration')" name="duration">
            <t-input v-model="formData.time[0]" placeholder="08:00" style="width: 150px"
                     @input="formData.time[0] = formData.time[0].replace(/[^\d|:]/g,'')"
                     @blur="updateDuration"
            />
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            <t-input-number v-model="formData.duration" :max="3" :min="0" :step="0.1" :decimal-places="2"
                            @change="updateEndTime" @blur="updateDuration" :onchange="updateDuration"
                            :onblur="updateDuration"
            >
              <template #suffix><span>h</span></template>
            </t-input-number>
          </t-form-item>

          <t-form-item :label="this.$t('manage.add.location')" name="location">
            <t-select
                clearable
                filterable
                :filter="(search, option) => option.label.toLowerCase().includes(search.toLowerCase())"
                style="width: 100%"
                v-model="formData.location"
                :placeholder="this.$t('manage.add.location_prompt')"
            >
              <t-option key="t" label="Teaching Building No.1 Lecture Hall"
                        value="Teaching Building No.1 Lecture Hall"/>
              <t-option key="r" label="Research Building Lecture Hall"
                        value="Research Building Lecture Hall"/>
              <t-option key="l" label="Library Conference Hall and Activity Room"
                        value="Library Conference Hall and Activity Room"/>
            </t-select>
          </t-form-item>

          <t-space size="medium" style="display: flex; justify-content: end; padding-top: 20px">
            <t-button theme="default" variant="base" type="reset" @click="formVisible = false">{{
                $t("manage.add.cancel")
              }}
            </t-button>
            <t-button theme="primary" type="submit">{{ $t("manage.add.submit") }}</t-button>
          </t-space>

        </t-form>

      </t-dialog>

      <t-dialog
          :closeBtn="false"
          :footer="false"
          destroyOnClose
          :header="this.$t('manage.list.edit')"
          showOverlay v-model:visible="editttVisible"
          width="500px">

        <t-form :data="this.editData" :rules="this.formRules1" scroll-to-first-error="smooth"
                labelWidth="100px" style="text-align: left" @submit="submitEdit">

          <t-form-item :label="this.$t('manage.add.code')" name="code">
            <t-input v-model="editData.code"
                     :onChange="this.editData.code = this.editData.code.toUpperCase()"
            />
          </t-form-item>

          <t-form-item :label="this.$t('manage.add.name')" name="name">
            <t-input v-model="editData.name"/>
          </t-form-item>

          <t-form-item :label="this.$t('manage.add.lang')" name="lang">
            <t-radio-group variant="default-filled" v-model="editData.lang">
              <t-radio-button value="en">{{ $t("manage.add.lang_en") }}</t-radio-button>
              <t-radio-button value="zh">{{ $t("manage.add.lang_zh") }}</t-radio-button>
              <t-radio-button value="bi">{{ $t("manage.add.lang_bi") }}</t-radio-button>
            </t-radio-group>
          </t-form-item>

          <t-form-item :label="this.$t('manage.add.teacher')" name="teacher">
            <t-input v-model="editData.teacher"/>
          </t-form-item>

          <t-form-item :label="this.$t('manage.add.date')" name="date">
            <t-date-picker
                allowInput
                clearable
                format="YYYY/MM/DD"
                v-model="editData.date"
                :placeholder="this.$t('manage.add.date_prompt')"
                :disable-date="{
                  before: dayjs().add(-1, 'day').format(),
                }"
                style="width: 100%"
            />
          </t-form-item>

          <t-form-item :label="this.$t('manage.add.time')" name="time">
            <t-time-range-picker v-model="editData.time" clearable format="HH:mm" allow-input :steps="[1, 10, 1]"
                                 :placeholder="[this.$t('manage.add.time_st'), this.$t('manage.add.time_ed')]"
                                 :onChange="updateDuration1"
            />
          </t-form-item>

          <t-form-item :label="this.$t('manage.add.duration')" name="duration">
            <t-input v-model="editData.time[0]" placeholder="08:00" style="width: 150px"
                     @input="editData.time[0] = editData.time[0].replace(/[^\d|:]/g,'')"
                     @blur="updateDuration1"
            />
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            <t-input-number v-model="editData.duration" :max="3" :min="0" :step="0.1" :decimal-places="2"
                            @change="updateEndTime1" @blur="updateDuration1" :onchange="updateDuration1"
                            :onblur="updateDuration1"
            >
              <template #suffix><span>h</span></template>
            </t-input-number>
          </t-form-item>

          <t-form-item :label="this.$t('manage.add.location')" name="location">
            <t-select
                clearable
                filterable
                :filter="(search, option) => option.label.toLowerCase().includes(search.toLowerCase())"
                style="width: 100%"
                v-model="editData.location"
                :placeholder="this.$t('manage.add.location_prompt')"
            >
              <t-option key="t" label="Teaching Building No.1 Lecture Hall"
                        value="Teaching Building No.1 Lecture Hall"/>
              <t-option key="r" label="Research Building Lecture Hall"
                        value="Research Building Lecture Hall"/>
              <t-option key="l" label="Library Conference Hall and Activity Room"
                        value="Library Conference Hall and Activity Room"/>
            </t-select>
          </t-form-item>

          <t-space size="medium" style="display: flex; justify-content: end; padding-top: 20px">
            <t-button theme="default" variant="base" type="reset" @click="editttVisible = false">{{
                $t("manage.add.cancel")
              }}
            </t-button>
            <t-button theme="primary" type="submit">{{ $t("manage.add.submit") }}</t-button>
          </t-space>

        </t-form>

      </t-dialog>
    </div>

    <a-config-provider :locale="this.$i18n.locale === 'en' ? enUS : zhCN">
      <AQueryTable
          ref="tableRef"
          :request="request"
          :columns="this.columns"
          :loading="this.loading"
          :pagination="{ size: 'small', bordered: true, style: {'padding-right': '20px'} }"
          :hide-one-page="false"
          align="right"
      >
        <template #columns>
          <TableColumn :title="$t('manage.list.cols.code')" data-index="code"
                       :sortable="{ sortDirections: ['ascend', 'descend'] }"/>
          <TableColumn :title="$t('manage.list.cols.name')" data-index="name"
                       :sortable="{ sortDirections: ['ascend', 'descend'] }"/>
          <TableColumn :title="$t('manage.list.cols.lang')" data-index="lang"
                       :filterable="{
                          filters: [
                               { text: $t('manage.list.filter.lang_zh'), value: '中文 Chinese' },
                               { text: $t('manage.list.filter.lang_en'), value: '英文 English' },
                               { text: $t('manage.list.filter.lang_bi'), value: '双语 Bilingual' },
                          ],
                          filter: (value, record) => value.some(v => v.includes(record.lang)),
                          multiple: true
                        }"/>
          <TableColumn :title="$t('manage.list.cols.teacher')" data-index="teacher"
                       :sortable="{ sortDirections: ['ascend', 'descend'] }"/>
          <TableColumn :title="$t('manage.list.cols.date')" data-index="date"
                       :sortable="{ sortDirections: ['ascend', 'descend'] }"/>
          <TableColumn :title="$t('manage.list.cols.time')" data-index="time"
                       :sortable="{ sortDirections: ['ascend', 'descend'] }"/>
          <TableColumn :title="$t('manage.list.cols.loc')" data-index="location"
                       :filterable="{
                          filters:  [
                               { text: $t('Teaching Building No.1 Lecture Hall'), value: 'Teaching Building No.1 Lecture Hall' },
                               { text: $t('Research Building Lecture Hall'), value: 'Research Building Lecture Hall' },
                               { text: $t('Library Conference Hall and Activity Room'), value: 'Library Conference Hall and Activity Room' },
                          ],
                          filter: (value, record) => value.some(v => v.includes(record.location)),
                          multiple: true
                        }"/>
          <TableColumn :title="$t('manage.list.cols.duration')" data-index="duration"/>
          <TableColumn>
            <template #title>
              <div>{{ $t('manage.list.cols.operation') }}</div>
            </template>
            <template #cell="{ record }">
              <t-button @click="()=>{openedit(record);editttVisible=true}" theme="default">
                {{ $t("manage.list.op.edit") }}
              </t-button>
              &nbsp;
              <t-popconfirm v-model="visibleDelete[record.uuid]" theme="danger"
                            :content="$t('manage.del')"
                            :onConfirm="()=>{deleteCourse(record.uuid)}">
                <t-button theme="danger">{{ $t('manage.list.op.del') }}</t-button>
              </t-popconfirm>
            </template>
          </TableColumn>
        </template>
      </AQueryTable>
    </a-config-provider>
  </div>
</template>

<script>
import TdesignAnim from "@/util/TdesignAnim";
import { AQueryTable } from "@dangojs/a-query-table";
import { TableColumn } from "@arco-design/web-vue";
import enUS from '@arco-design/web-vue/es/locale/lang/en-us';
import zhCN from '@arco-design/web-vue/es/locale/lang/zh-cn';
import { reactive, ref } from "vue";
import dayjs from "dayjs";

const loading = ref(false);
let tableData = [];
let formData;
let editData = ref({});
const formVisible = ref(false);

const locationList = [
  "Teaching Building No.1 Lecture Hall",
  "Research Building Lecture Hall",
  "Library Conference Hall and Activity Room",
]
const _locationList = []
locationList.forEach(i => _locationList.push({
  "text": i,
  "value": i,
}))

const teacherList = [];

let inputDurationMinutes;
let inputDurationMinutes1;

export default {
  inject: ['reload'],
  components: {
    AQueryTable,
    TdesignAnim
  },
  data() {
    return {
      enUS,
      zhCN,
      tableData,
      loading,
      formVisible,
      editData,
      dayjs
    }
  },
  computed: {
    columns() {
      return [
        {
          title: this.$t("manage.list.cols.code"),
          dataIndex: "code",
          sortable: {
            sortDirections: ['ascend', 'descend']
          }
        },
        {
          title: this.$t("manage.list.cols.name"),
          dataIndex: "name",
          sortable: {
            sortDirections: ['ascend', 'descend']
          }
        },
        {
          title: this.$t("manage.list.cols.lang"),
          dataIndex: "lang",
          filterable: {
            filters: [
              {
                text: this.$t("manage.list.filter.lang_zh"),
                value: '中文 Chinese',
              },
              {
                text: this.$t("manage.list.filter.lang_en"),
                value: '英文 English',
              },
              {
                text: this.$t("manage.list.filter.lang_bi"),
                value: '双语 Bilingual',
              },
            ],
            filter: (value, record) => value.some(v => v.includes(record.lang)),
            multiple: true
          }
        },
        {
          title: this.$t("manage.list.cols.teacher"),
          dataIndex: "teacher",
          filterable: {
            filters: teacherList,
            filter: (value, record) => value.some(v => v.includes(record.teacher)),
            multiple: true
          }
        },
        {
          title: this.$t("manage.list.cols.date"),
          dataIndex: "date",
          sortable: {
            sortDirections: ['ascend', 'descend']
          }
        },
        {
          title: this.$t("manage.list.cols.time"),
          dataIndex: "time",
          sortable: {
            sortDirections: ['ascend', 'descend']
          }
        },
        {
          title: this.$t("manage.list.cols.duration"),
          dataIndex: "duration",
        },
        {
          title: this.$t("manage.list.cols.loc"),
          dataIndex: "location",
          filterable: {
            filters: _locationList,
            filter: (value, record) => value.some(v => v.includes(record.location)),
            multiple: true
          }
        },
        {
          title: this.$t("manage.list.cols.operation"),
          slotName: "operation",
        },
      ];
    },
    formRules() {
      return {
        code: [
          { required: true, message: this.$t("manage.add.code_empty"), type: "error", trigger: "blur" },
          { required: true, message: this.$t("manage.add.code_empty"), type: "error", trigger: "change" },
          { pattern: /^[A-Za-z\d]+$/, message: this.$t("manage.add.code_err"), type: "error", trigger: "blur" },
          { pattern: /^[A-Za-z\d]+$/, message: this.$t("manage.add.code_err"), type: "error", trigger: "change" },
        ],
        name: [
          { required: true, message: this.$t("manage.add.name_empty"), type: "error", trigger: "blur" },
          { required: true, message: this.$t("manage.add.name_empty"), type: "error", trigger: "change" },
          { pattern: /^[A-Za-z\s]+$/, message: this.$t("manage.add.name_err"), type: "error", trigger: "blur" },
          { pattern: /^[A-Za-z\s]+$/, message: this.$t("manage.add.name_err"), type: "error", trigger: "change" },
          { pattern: /^\S/, message: this.$t("manage.add.space_err"), type: "error", trigger: "change" },
          { pattern: /\S$/, message: this.$t("manage.add.space_err"), type: "error", trigger: "change" },
        ],
        lang: [
          { required: true, message: this.$t("manage.add.lang_empty"), type: "error", trigger: "blur" },
          { required: true, message: this.$t("manage.add.lang_empty"), type: "error", trigger: "change" },
        ],
        teacher: [
          { required: true, message: this.$t("manage.add.teacher_empty"), type: "error", trigger: "blur" },
          { required: true, message: this.$t("manage.add.teacher_empty"), type: "error", trigger: "change" },
          { pattern: /^[A-Za-z\s]+$/, message: this.$t("manage.add.teacher_err"), type: "error", trigger: "blur" },
          { pattern: /^[A-Za-z\s]+$/, message: this.$t("manage.add.teacher_err"), type: "error", trigger: "change" },
          { pattern: /^\S/, message: this.$t("manage.add.space_err"), type: "error", trigger: "change" },
          { pattern: /\S$/, message: this.$t("manage.add.space_err"), type: "error", trigger: "change" },
        ],
        location: [
          { required: true, message: this.$t("manage.add.location_empty"), type: "error", trigger: "blur" },
          { required: true, message: this.$t("manage.add.location_empty"), type: "error", trigger: "change" },
        ],
        date: [
          { required: true, message: this.$t("manage.add.date_empty"), type: "error", trigger: "blur" },
          { required: true, message: this.$t("manage.add.date_empty"), type: "error", trigger: "change" },
          {
            validator: (val) => dayjs(val, "YYYY/MM/DD") >= dayjs().add(-1, 'day'),
            message: this.$t("manage.add.date_invalid"),
            type: "error",
            trigger: "change"
          },
          {
            validator: (val) => dayjs(val, "YYYY/MM/DD") >= dayjs().add(-1, 'day'),
            message: this.$t("manage.add.date_invalid"),
            type: "error",
            trigger: "blur"
          }
        ],
        time: [
          { required: true, message: this.$t("manage.add.time_empty"), type: "error", trigger: "blur" },
          { required: true, message: this.$t("manage.add.time_empty"), type: "error", trigger: "change" },
          {
            validator: () => inputDurationMinutes() > 0,
            message: this.$t("manage.add.time_err"),
            type: "error",
            trigger: "change"
          },
          {
            validator: () => inputDurationMinutes() > 0,
            message: this.$t("manage.add.time_err"),
            type: "error",
            trigger: "blur"
          },
          {
            validator: () => inputDurationMinutes() <= 4 * 60 * 60,
            message: this.$t("manage.add.time_err2"),
            type: "error",
            trigger: "change"
          },
          {
            validator: () => inputDurationMinutes() <= 4 * 60 * 60,
            message: this.$t("manage.add.time_err2"),
            type: "error",
            trigger: "blur"
          }
        ],
        duration: [
          {
            validator: () => formData.duration > 0,
            message: this.$t("manage.add.dur_err"),
            type: "error",
            trigger: "blur"
          },
          {
            validator: () => formData.duration > 0,
            message: this.$t("manage.add.dur_err"),
            type: "error",
            trigger: "change"
          },
        ]
      }
    },
    formRules1() {
      return {
        code: [
          { required: true, message: this.$t("manage.add.code_empty"), type: "error", trigger: "blur" },
          { required: true, message: this.$t("manage.add.code_empty"), type: "error", trigger: "change" },
          { pattern: /^[A-Za-z\d]+$/, message: this.$t("manage.add.code_err"), type: "error", trigger: "blur" },
          { pattern: /^[A-Za-z\d]+$/, message: this.$t("manage.add.code_err"), type: "error", trigger: "change" },
        ],
        name: [
          { required: true, message: this.$t("manage.add.name_empty"), type: "error", trigger: "blur" },
          { required: true, message: this.$t("manage.add.name_empty"), type: "error", trigger: "change" },
          { pattern: /^[A-Za-z\s]+$/, message: this.$t("manage.add.name_err"), type: "error", trigger: "blur" },
          { pattern: /^[A-Za-z\s]+$/, message: this.$t("manage.add.name_err"), type: "error", trigger: "change" },
          { pattern: /^\S/, message: this.$t("manage.add.space_err"), type: "error", trigger: "change" },
          { pattern: /\S$/, message: this.$t("manage.add.space_err"), type: "error", trigger: "change" },
        ],
        lang: [
          { required: true, message: this.$t("manage.add.lang_empty"), type: "error", trigger: "blur" },
          { required: true, message: this.$t("manage.add.lang_empty"), type: "error", trigger: "change" },
        ],
        teacher: [
          { required: true, message: this.$t("manage.add.teacher_empty"), type: "error", trigger: "blur" },
          { required: true, message: this.$t("manage.add.teacher_empty"), type: "error", trigger: "change" },
          { pattern: /^[A-Za-z\s]+$/, message: this.$t("manage.add.teacher_err"), type: "error", trigger: "blur" },
          { pattern: /^[A-Za-z\s]+$/, message: this.$t("manage.add.teacher_err"), type: "error", trigger: "change" },
          { pattern: /^\S/, message: this.$t("manage.add.space_err"), type: "error", trigger: "change" },
          { pattern: /\S$/, message: this.$t("manage.add.space_err"), type: "error", trigger: "change" },
        ],
        location: [
          { required: true, message: this.$t("manage.add.location_empty"), type: "error", trigger: "blur" },
          { required: true, message: this.$t("manage.add.location_empty"), type: "error", trigger: "change" },
        ],
        date: [
          { required: true, message: this.$t("manage.add.date_empty"), type: "error", trigger: "blur" },
          { required: true, message: this.$t("manage.add.date_empty"), type: "error", trigger: "change" },
          {
            validator: (val) => dayjs(val, "YYYY/MM/DD") >= dayjs().add(-1, 'day'),
            message: this.$t("manage.add.date_invalid"),
            type: "error",
            trigger: "change"
          },
          {
            validator: (val) => dayjs(val, "YYYY/MM/DD") >= dayjs().add(-1, 'day'),
            message: this.$t("manage.add.date_invalid"),
            type: "error",
            trigger: "blur"
          }
        ],
        time: [
          { required: true, message: this.$t("manage.add.time_empty"), type: "error", trigger: "blur" },
          { required: true, message: this.$t("manage.add.time_empty"), type: "error", trigger: "change" },
          {
            validator: () => inputDurationMinutes1() > 0,
            message: this.$t("manage.add.time_err"),
            type: "error",
            trigger: "change"
          },
          {
            validator: () => inputDurationMinutes1() > 0,
            message: this.$t("manage.add.time_err"),
            type: "error",
            trigger: "blur"
          },
          {
            validator: () => inputDurationMinutes1() <= 4 * 60 * 60,
            message: this.$t("manage.add.time_err2"),
            type: "error",
            trigger: "change"
          },
          {
            validator: () => inputDurationMinutes1() <= 4 * 60 * 60,
            message: this.$t("manage.add.time_err2"),
            type: "error",
            trigger: "blur"
          }
        ],
        duration: [
          {
            validator: () => editData.value.duration > 0,
            message: this.$t("manage.add.dur_err"),
            type: "error",
            trigger: "blur"
          },
          {
            validator: () => editData.value.duration > 0,
            message: this.$t("manage.add.dur_err"),
            type: "error",
            trigger: "change"
          },
        ]
      }
    },
    data() {
      return reactive(tableData);
    },
    locationList() {
      console.log(_locationList)
      return _locationList;
    }
  },
  created() {
    tableData = JSON.parse(localStorage.getItem("list")) || [];
  },
  setup() {
    const rowSelection = reactive({
      type: 'checkbox',
      showCheckedAll: true,
      onlyCurrent: false,
    });
    const pagination = { pageSize: 5 }

    return {
      rowSelection,
      pagination
    }
  },
  methods: {
    request: async ({ pageNum, pageSize }) => {
      loading.value = true;
      const startIndex = (pageNum - 1) * pageSize;
      const endIndex = pageNum * pageSize;
      loading.value = false;
      const res = [];
      for (const i of tableData.slice(startIndex, endIndex)) {
        res.push(Object.assign({}, i));
      }
      res.forEach(i => {
        switch (i.lang) {
          case "zh":
            i.lang = localStorage.getItem("locale") === "zh" ? "中文" : "Chinese";
            break;
          case "en":
            i.lang = localStorage.getItem("locale") === "zh" ? "英文" : "English";
            break;
          case "bi":
            i.lang = localStorage.getItem("locale") === "zh" ? "双语" : "Bilingual";
            break;
        }
        i.duration += "h"
        i.time = i.time[0]
      });
      console.log(res)
      return {
        success: true,
        data: res,
        total: tableData.length,
      };
    }
  },
}
</script>

<script setup>
import { v4 as uuid4 } from 'uuid';
import { MessagePlugin } from "tdesign-vue-next";

formData = reactive({
  code: '',
  name: null,
  lang: null,
  teacher: null,
  date: null,
  time: ref([null, null]),
  duration: 0,
  location: null,
})

const visibleDelete = ref({});

const tableRef = ref();

const submitAdd = ({ validateResult, firstError }) => {
  updateEndTime()
  updateDuration()
  if (validateResult !== true) {
    MessagePlugin.warning(firstError);
  } else if (checks()) {
    MessagePlugin.warning(checks());
  } else {
    MessagePlugin.success(localStorage.getItem("locale") === "zh" ? "Succeed!" : "成功提交");
    formData.duration = Math.round(formData.duration * 100) / 100.0;
    formData.uuid = uuid4();
    visibleDelete[formData.uuid] = false;
    console.log(Object.assign({}, formData))
    tableData.push(Object.assign({}, formData));
    console.log(tableData, 'pushed')
    tableRef.value.reload();
    formVisible.value = false;
    localStorage.setItem("list", JSON.stringify(tableData));
    resetForm();
  }
}

const submitEdit = ({ validateResult, firstError }) => {
  updateEndTime1()
  updateDuration1()
  if (validateResult !== true) {
    MessagePlugin.warning(firstError);
  } else {
    MessagePlugin.success(localStorage.getItem("locale") === "zh" ? "Succeed!" : "成功修改");
    editData.value.duration = Math.round(editData.value.duration * 100) / 100.0;
    const res = [];
    for (const c of tableData) {
      if (c.uuid != editData.value.uuid) {
        res.push(c)
      } else {
        res.push(editData.value)
      }
    }
    tableData = res
    tableRef.value.reload();
    editttVisible.value = false;
    localStorage.setItem("list", JSON.stringify(tableData));
    resetForm();
  }
}

const deleteCourse = (uuid) => {
  const res = []
  for (const c of tableData) {
    if (c.uuid !== uuid) {
      res.push(c)
    }
  }
  tableData = res
  localStorage.setItem("list", JSON.stringify(tableData))
  tableRef.value.reload();
}

const checks = () => {
  for (const c of tableData) {
    let ct = c.time || ["00:00", "00:00"];
    let m = formData.time || ["00:00", "00:00"];
    if (c.location === formData.location && c.date === formData.date
        && !(str2min(ct[1]) <= str2min(m[0]) || str2min(ct[0]) >= str2min(m[1]))) {
      return t("manage.add.room_conflict") + c.name;
    }
    if (c.teacher === formData.teacher && c.date === formData.date) {
      return localStorage.getItem("locale") === "zh"
          ? c.teacher + '已在 ' + c.date + `排课（${ c.name }）`
          : c.teacher + ' already take a course on ' + c.date + `(${ c.name })`
    }
    if (c.name === formData.name && c.date === formData.date) {
      return localStorage.getItem("locale") === "zh"
          ? c.name + '当日已排课'
          : c.name + ' already has a schedule on ' + c.date
    }
    if (c.name !== formData.name && c.code === formData.code) {
      return localStorage.getItem("locale") === "zh"
          ? `课程编号冲突：[${ c.code }] ${ c.name }`
          : `conflict on course code: [${ c.code }] ${ c.name }`
    }
  }
}

const t = (key) => {
  switch (key) {
    case "manage.add.room_conflict":
      return localStorage.getItem("locale") === "zh"
          ? "课室冲突："
          : "Location conflicts with "
  }
}

inputDurationMinutes = () => {
  formData.time = formData.time || ["00:00", "00:00"]
  let t1s = formData.time[0] || "00:00";
  let t2s = formData.time[1] || "00:00";
  t1s = t1s.split(":");
  t2s = t2s.split(":");
  const t1 = parseInt(t1s[0]) * 60 + parseInt(t1s[1]);
  const t2 = parseInt(t2s[0]) * 60 + parseInt(t2s[1]);
  return t2 - t1;
}
inputDurationMinutes1 = () => {
  editData.value.time = editData.value.time || ["00:00", "00:00"]
  let t1s = editData.value.time[0] || "00:00";
  let t2s = editData.value.time[1] || "00:00";
  t1s = t1s.split(":");
  t2s = t2s.split(":");
  const t1 = parseInt(t1s[0]) * 60 + parseInt(t1s[1]);
  const t2 = parseInt(t2s[0]) * 60 + parseInt(t2s[1]);
  return t2 - t1;
}

const str2min = (s) => {
  s = s || "00:00";
  let t1s = s.split(":");
  return parseInt(t1s[0]) * 60 + parseInt(t1s[1]);
}

const updateEndTime = () => {
  if (formData.duration < 0) {
    return
  }
  formData.time = formData.time || ["00:00", "00:00"]
  let t1s = formData.time[0] || "00:00";
  t1s = t1s.split(":");

  if (t1s.length === 1) {
    t1s = [t1s[0], 0]
  }
  t1s[0] = Math.min(23, Math.max(0, parseInt(t1s[0])))
  t1s[1] = Math.min(59, Math.max(0, parseInt(t1s[1])))
  const t1 = t1s[0] * 60 + t1s[1];
  const t2 = t1 + formData.duration * 60.0;
  let t2h = Math.floor(t2 / 60);
  let t2m = Math.round(t2 - t2h * 60);
  formData.time[0] = `${ t1s[0].toString().padStart(2, '0') }:${ t1s[1].toString().padStart(2, '0') }` || "00:00";
  formData.time[1] = `${ t2h.toString().padStart(2, '0') }:${ t2m.toString().padStart(2, '0') }`;
}

const updateDuration = () => {
  const dur = inputDurationMinutes();
  if (dur > 0) {
    formData.duration = dur / 60.0;
  }
}

const updateEndTime1 = () => {
  console.log(editData.value)
  if (editData.value.duration < 0) {
    return
  }
  editData.value.time = editData.value.time || ["00:00", "00:00"]
  let t1s = editData.value.time[0] || "00:00";
  t1s = t1s.split(":");

  if (t1s.length === 1) {
    t1s = [t1s[0], 0]
  }
  t1s[0] = Math.min(23, Math.max(0, parseInt(t1s[0])))
  t1s[1] = Math.min(59, Math.max(0, parseInt(t1s[1])))
  const t1 = t1s[0] * 60 + t1s[1];
  const t2 = t1 + editData.value.duration * 60.0;
  let t2h = Math.floor(t2 / 60);
  let t2m = Math.round(t2 - t2h * 60);
  editData.value.time[0] = `${ t1s[0].toString().padStart(2, '0') }:${ t1s[1].toString().padStart(2, '0') }` || "00:00";
  editData.value.time[1] = `${ t2h.toString().padStart(2, '0') }:${ t2m.toString().padStart(2, '0') }`;
}

const updateDuration1 = () => {
  const dur = inputDurationMinutes1();
  console.log(dur)
  if (dur > 0) {
    editData.value.duration = dur / 60.0;
  }
}

const resetForm = () => {
  formData.code = '';
  formData.name = null;
  formData.lang = null;
  formData.teacher = null;
  formData.date = null;
  formData.time = [null, null];
  formData.duration = 0;
  formData.location = null;
};

const editttVisible = ref(false);

const openedit = (record) => {
  console.log(record.uuid)
  for (const c of JSON.parse(localStorage.getItem('list'))) {
    if (c.uuid === record.uuid) {
      editData.value = c;
      console.log(editData)
      break;
    }
  }
}

</script>

<style scoped>
.wrapper0 {
  width: 100%;
  position: relative;
}

.wrapper1 {
  width: 100%;
  position: absolute;
  top: 0;
  left: 0;
}

.wrapper {
  width: 100%;
  display: flex;
  flex-direction: column;
}

</style>
