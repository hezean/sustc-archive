<template>
  <t-calendar :controller-config="controllerConfig" style="border: none" :global-config="this.lang">
    <template #head>
      <div style="text-align: left"><b>{{ $t('manage.list.open_courses') }}</b></div>
    </template>
  </t-calendar>
</template>

<script setup>
import { reactive } from 'vue';

const controllerConfig = reactive({
  visible: true,
  disabled: false,
  year: {
    visible: true,
    selectProps: {
      disabled: false,
    },
  },
  month: {
    visible: true,
    selectProps: {
      disabled: false,
    },
  },
  mode: {
    visible: false,

  },
  current: {
    visible: true,
    currentDayButtonProps: {
      disabled: false,
    },
  },
});
</script>

<script>
import enConfig from "tdesign-vue-next/es/locale/en_US";
import zhConfig from "tdesign-vue-next/es/locale/zh_CN";

export default {
  computed: {
    lang: () => {
      console.log(localStorage.getItem("locale"))
      return localStorage.getItem("locale") === "zh"
          ? zhConfig
          : enConfig;
    }
  }
}
</script>

<style scoped>
.course-table {
  width: 100%;
  height: 100%;
}
</style>
