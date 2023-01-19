<template>
  <el-menu
      :default-active="activeIndex"
      router
      class="el-menu1"
      mode="horizontal"
      unique-opened
      background-color="rgb(29,171,255)"
      text-color="rgb(253,253,254)"
      active-text-color="rgb(149,213,243)"
      style="min-width: 1300px">

    <el-menu-item index="/github">
      <b style="font-size: 18px; font-family: Cambria,sans-serif">Github</b>
    </el-menu-item>

    <el-menu-item index="/stackoverflow">
      <b style="font-size: 18px; font-family: Cambria,sans-serif">StackOverflow</b>
    </el-menu-item>

    <span
        style="position: absolute;text-align: center;padding-top: 10px;right: 31%;font-size: 30px;font-weight: bold; color: white">
      IDK what you want to know about Java
    </span>

    <el-button style="position: absolute;right: 20px;top: 10px;font-family: Cambria,sans-serif" type="primary" plain>
      <a href="https://github.com/HeZean/sustech-cs209" style="font-family: Cambria,sans-serif;">❤️ Open Source</a>
    </el-button>
  </el-menu>
</template>

<script>
export default {
  name: 'HeadMenu',
  data() {
    console.log(this.$route.path.substring(0, this.$route.path.length))
    return {
      activeIndex: this.$route.path.substring(0, this.$route.path.length)
    }
  },
}
</script>

<style scoped>
a {
  text-decoration: none;
}

span {
  pointer-events: none;
}
</style>
