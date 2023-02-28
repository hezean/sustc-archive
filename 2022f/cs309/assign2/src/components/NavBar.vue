<template>
  <div class="nav-wrapper">
    <a href="/" style="display: flex">
      <img src="@/assets/logo.png" style="display: flex; height: 26px"/>
    </a>

    <t-head-menu class="nav-bar" :value="this.computed.activatePath()">
      <t-menu-item value="home" to="/home" style="min-width: 0; max-height: 32px">
        <div class="nav-item">{{ $t('navbar.home') }}</div>
      </t-menu-item>
      <t-menu-item value="manage" to="/manage" style="min-width: 0; max-height: 32px">
        <div class="nav-item">{{ $t('navbar.manage') }}</div>
      </t-menu-item>

      <span style="padding-left: 30px"></span>

      <t-menu-item :onclick="this.changeLocale" style="min-width: 32px; max-height: 32px">
        <t-tooltip :content="this.$t('navbar.language')"
                   showArrow
                   theme="light"
                   placement="bottom">
          <icon-language class="lang-icon" style="display: flex"/>
        </t-tooltip>
      </t-menu-item>

    </t-head-menu>
  </div>

  <t-divider style="margin: 0"/>
</template>


<script>
export default {
  data() {
    return {
      computed: {
        activatePath: () => {
          return this.$route.path.split('/')[1];
        }
      }
    }
  },
  methods: {
    changeLocale() {
      if (localStorage.getItem("locale") === "zh") {
        localStorage.setItem("locale", "en");
      } else {
        localStorage.setItem("locale", "zh");
      }
      this.$i18n.locale = localStorage.getItem("locale");
    }
  },
}
</script>

<style>
.nav-wrapper {
  display: flex;
  justify-content: space-between;
  margin-left: 3%;
  margin-right: 3%;
  align-items: center;
}

.nav-bar {
  display: flex;
  justify-content: flex-end;
  align-items: center;
}

.nav-item {
  padding-left: 13px;
  padding-right: 13px;
}

.lang-icon {
  font-size: 20px;
  stroke-linecap: round;
  stroke-linejoin: round;
  stroke-width: 3;
}
</style>
