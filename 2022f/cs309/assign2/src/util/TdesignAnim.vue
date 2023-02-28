<template>
  <iframe
      class="bg-animation"
      :src="computed.url()"
  />
</template>

<script>
export default {
  props: {
    /**
     * base               =  triangle + '=='
     * explain            =  code block
     * design/mode_light  =  moon
     * layout             =  circle + panel
     * navigation         =  panel with three squares, one blue
     * form               =  tick + switch
     * data               =  ball '3' + panel with three lines
     * message            =  window with '!' + circle
     */
    ani: String
  },
  data() {
    return {
      computed: {
        url: () => {
          return "https://tdesign.gtimg.com/site/spline/" + this.ani + "/index.html";
        }
      }
    };
  },
};
</script>

<style>
.bg-animation {
  transition: max-height 0.25s 0.2s;
  max-height: 280px;
  height: 251px;
  width: 100%;
  opacity: 1;
  border: 0;
  transform: scale(0.55);
  transform-origin: top right;
}
</style>
