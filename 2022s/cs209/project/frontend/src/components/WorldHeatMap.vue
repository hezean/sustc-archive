<!--<template>-->
<!--  <div class="map">-->
<!--    <div ref="self" class="map__content"></div>-->
<!--  </div>-->
<!--</template>-->

<!--<script>-->
<!--import echarts from "echarts";-->
<!--import worldMapJson from "@/assets/world.json";-->
<!--import { worldNameMap, worldGeoMap } from "@/assets/worldNameMap";-->
<!--const icon = require("@/assets/img/risk-icon.png");-->

<!--export default {-->
<!--  data() {-->
<!--    return { worldNameMap, worldGeoMap };-->
<!--  },-->
<!--  mounted() {-->
<!--    this.initData();-->
<!--  },-->
<!--  methods: {-->
<!--    initData() {-->
<!--      let chartDom = echarts.init(this.$refs.self);-->
<!--      let riskData = [-->
<!--        { name: "中国", value: 120 },-->
<!--        { name: "澳大利亚", value: 120 },-->
<!--        { name: "巴西", value: 170 },-->
<!--        { name: "加拿大", value: 570 },-->
<!--        { name: "美国", value: 570 },-->
<!--        { name: "墨西哥", value: 570 },-->
<!--        { name: "古巴", value: 530 },-->
<!--        { name: "西班牙", value: 70 },-->
<!--      ];-->
<!--      let markPointData = [-->
<!--        {-->
<!--          name: "中国",-->
<!--          coord: this.worldGeoMap["中国"],-->
<!--          symbol: "image://" + icon,-->
<!--          symbolSize: 20,-->
<!--        },-->
<!--        {-->
<!--          name: "澳大利亚",-->
<!--          coord: this.worldGeoMap["澳大利亚"],-->
<!--          symbol: "image://" + icon,-->
<!--          symbolSize: 20,-->
<!--        },-->
<!--        {-->
<!--          name: "加拿大",-->
<!--          coord: this.worldGeoMap["加拿大"],-->
<!--          symbol: "image://" + icon,-->
<!--          symbolSize: 20,-->
<!--        },-->
<!--      ];-->
<!--      echarts.registerMap("world", worldMapJson);-->
<!--      let option = {-->
<!--        tooltip: {-->
<!--          trigger: "item",-->
<!--          backgroundColor: "#fff",-->
<!--          textStyle: {-->
<!--            color: "#666666",-->
<!--          },-->
<!--          borderWidth: 2,-->
<!--          borderColor: "#ECECEE",-->
<!--          padding: 10,-->
<!--          formatter: function (params) {-->
<!--            var value = params.value ? params.value : 0;-->
<!--            return "风险个数<br/>" + params.name + " : " + value + "个";-->
<!--          },-->
<!--        },-->
<!--        visualMap: {-->
<!--          type: "piecewise",-->
<!--          splitNumber: 3,-->
<!--          inverse: true,-->
<!--          right: 50,-->
<!--          top: 50,-->
<!--          pieces: [-->
<!--            {-->
<!--              min: 0,-->
<!--              max: 100,-->
<!--              color: "#dfe2f5",-->
<!--            },-->
<!--            {-->
<!--              min: 100,-->
<!--              max: 500,-->
<!--              color: "#b5c1eb",-->
<!--            },-->
<!--            {-->
<!--              min: 500,-->
<!--              max: 1000,-->
<!--              color: "#839aee",-->
<!--            },-->
<!--          ],-->
<!--          textStyle: {-->
<!--            color: "#6E7784",-->
<!--          },-->
<!--          itemWidth: 40,-->
<!--          itemHeight: 10,-->
<!--          align: "left",-->
<!--        },-->
<!--        series: [-->
<!--          {-->
<!--            type: "map",-->
<!--            map: "world",-->
<!--            nameMap: this.worldNameMap,-->
<!--            roam: true,-->
<!--            itemStyle: {-->
<!--              areaColor: "#dfe2f5",-->
<!--              borderWidth: 0,-->
<!--              emphasis: {-->
<!--                label: {-->
<!--                  show: false,-->
<!--                },-->
<!--                areaColor: "#feddb3",-->
<!--              },-->
<!--            },-->
<!--            markPoint: {-->
<!--              label: {-->
<!--                normal: {-->
<!--                  show: true,-->
<!--                },-->
<!--                emphasis: {-->
<!--                  show: true,-->
<!--                },-->
<!--              },-->
<!--              itemStyle: {-->
<!--                normal: {-->
<!--                  color: "rgba(72,150,128,1)",-->
<!--                },-->
<!--              },-->
<!--              data: markPointData,-->
<!--            },-->
<!--            data: riskData,-->
<!--          },-->
<!--        ],-->
<!--      };-->
<!--      chartDom && chartDom.setOption(option);-->
<!--      let sizeFun = () => {-->
<!--        chartDom.resize();-->
<!--      };-->
<!--      window.addEventListener("resize", sizeFun);-->
<!--      // eslint-disable-next-line vue/no-deprecated-events-api-->
<!--      this.$once("hook:beforeDestroy", function () {-->
<!--        echarts.dispose(chartDom);-->
<!--      });-->
<!--    },-->
<!--  },-->
<!--};-->
<!--</script>-->

<!--<style lang="less" scoped>-->
<!--.map {-->
<!--  position: relative;-->
<!--  &__content {-->
<!--    height: 360px;-->
<!--    margin-top: -60px;-->
<!--  }-->
<!--}-->
<!--</style>-->
