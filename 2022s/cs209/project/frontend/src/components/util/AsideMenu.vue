<template>
  <el-menu :default-active="active_side"
           unique-opened
           router
           background-color="rgb(15,41,65)"
           text-color="white"
           active-text-color="wheat">
    <el-menu-item index="/github">
      <div style="font-size: 17px;left: 10px;position: absolute">
        Search repo
      </div>
    </el-menu-item>
    <el-menu-item index="/git2">
      <div style="font-size: 17px;left: 10px;position: absolute">
        Top repos analyze
      </div>
    </el-menu-item>
  </el-menu>

  <div>
    <live2d
        :style="style"
        :model="[3, 4]"
        :direction="direction"
        :size="size"
    ></live2d>
  </div>
</template>


<script>
import live2d from 'vue-live2d'

export default {
  name: 'AsideMenu',
  components: {
    live2d
  },
  data() {
    console.log(this.$route.path.substring(0, this.$route.path.length))
    return {
      active_side: this.$route.path.substring(0, this.$route.path.length),
      direction: 'right',
      style: 'position: fixed; bottom: 0; left: 0; zIndex: 1',
      width: 100,
      height: 100,
      size: 200,
    }
  },
}
</script>
