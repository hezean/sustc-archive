<template>
  <div class="wrapper">
    <header>
      <nav-bar/>
    </header>

    <div id="main" class="layout main" style="flex: 1">
      <router-view/>
    </div>

    <footer>
      <tis-footer/>
    </footer>
  </div>
</template>


<script>
import NavBar from "@/components/NavBar";
import TisFooter from "@/components/TisFooter";

export default {
  components: {
    NavBar,
    TisFooter,
  },
  provide () {
    return {
      reload: this.reload
    }
  },
}
</script>


<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;

  position: absolute;
  top: 0;
  bottom: 0;
  left: 0;
  right: 0;
}

#header {
  flex: 0;
}

#footer {
  flex: 0;
}

html, body {
  height: 100%;
  margin: 0;
}

.wrapper {
  display: flex;
  flex-direction: column;
  height: 100%;
}
</style>
