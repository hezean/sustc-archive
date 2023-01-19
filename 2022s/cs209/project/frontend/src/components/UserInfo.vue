<template>
  <!--  private Date infoUpdatedAt;-->

  <!--  private Integer id;-->
  <!--  private String username;-->

  <!--  private String nickname;-->
  <!--  private HttpServletResponse avatar;-->

  <!--  private String company;-->
  <!--  private String blog;-->

  <!--  private String email;-->

  <!--  private Integer followers;-->
  <!--  private Integer following;-->

  <!--  private Integer publicRepos;-->
  <p>
    {{ updateTime }}<br>
    {{ userName }}
  </p>
</template>

<script>
import axios from "axios";

var date;
var name;

export default {
  name: "UserInfo",
  methods: {
    getUser: function () {
      axios.get('http://localhost:8085/api/demo_user')
          // .then(resp => resp.data)
          .then(resp => {
            // date = resp.infoUpdatedAt
            // name = resp.name
            // date = resp["infoUpdatedAt"]
            // name = resp["name"]
            date = new Date()
            name = resp
          })
    }
  },
  mounted() {
    this.methods.getUser()
  },
  data() {
    return {
      updateTime: date,
      userName: name,
    }
  }
}
</script>

<style scoped>

</style>