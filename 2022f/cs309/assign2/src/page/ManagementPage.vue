<template>
  <div class="page">
    <div style="height: 100%">
      <a-menu
          showCollapseButton
          autoScrollIntoView
          autoOpenSelected
          :style="{ width: '200px', height: '100%', minWidth: '20px', textAlign: 'left' }"
          :default-selected-keys="this.computed.activatePath()"
      >
        <a-sub-menu key="course">
          <template #icon>
            <IconLayers/>
          </template>
          <template #title>{{ $t('manage.nav.course.name') }}</template>
          <a-menu-item key="course/list" @click="this.goto('list')">{{
              $t('manage.nav.course.list')
            }}
          </a-menu-item>
          <a-menu-item key="course/table" @click="this.goto('table')">{{
              $t('manage.nav.course.table')
            }}
          </a-menu-item>
        </a-sub-menu>
        <a-sub-menu key="student">
          <template #icon>
            <IconUserGroup/>
          </template>
          <template #title>{{ $t('manage.nav.student.name') }}</template>
          <a-menu-item key="student/quit" disabled>{{ $t('manage.nav.student.quit') }}</a-menu-item>
          <a-menu-item key="student/fail" disabled>{{ $t('manage.nav.student.fail') }}</a-menu-item>
          <a-menu-item key="student/admin" disabled>{{ $t('manage.nav.student.admin') }}</a-menu-item>
        </a-sub-menu>
        <a-sub-menu key="canteen">
          <template #icon>
            <IconBug/>
          </template>
          <template #title>{{ $t('manage.nav.canteen.name') }}</template>
          <a-menu-item key="canteen/bug" disabled>{{ $t('manage.nav.canteen.debug') }}</a-menu-item>
          <a-menu-item key="canteen/battery" disabled>{{ $t('manage.nav.canteen.battery') }}</a-menu-item>
          <a-menu-item key="canteen/smoke" disabled>{{ $t('manage.nav.canteen.smoke') }}</a-menu-item>
        </a-sub-menu>
      </a-menu>
    </div>

    <t-divider layout="vertical"/>
    <div style="width: 100%">
      <router-view/>
<!--      <router-view v-if="isShow"/>-->
    </div>
  </div>
</template>

<script>
export default {
  provide () {
    return {
      reload: this.reload
    }
  },
  data() {
    return {
      isShow: true,

      computed: {
        activatePath: () => {
          const path = this.$route.path.split("/");
          const res = [];
          res.push(path[2]);
          let key = "";
          for (const p of path.slice(2)) {
            key += `/${ p }`;
          }
          res.push(key.slice(1));
          return res;
        }
      }
    }
  },
  methods: {
    goto(path) {
      this.$router.push(path);
    },
    reload () {
      this.isShow= false
      this.$nextTick(function () {
        this.isShow= true
      })
    }
  }
}
</script>

<style scoped>
.page {
  display: flex;
  flex-direction: row;
  height: 100%;
  margin-bottom: 0;
}

.t-divider--vertical {
  height: auto;
  margin: 0;
}
</style>
