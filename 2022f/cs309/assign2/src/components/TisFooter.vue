<template>
  <div class="foot">
    <div class="copyright">
      <img src="@/assets/logo-footer.png" class="foot-logo">

      <p>
        &copy; 2022
        <a class="foot-link" href="https://github.com/0paradigm/codemesh">Chris</a>.
        All Rights Reserved.
      </p>
    </div>

    <div class="copyright">
      <a href="https://github.com/HeZean" class="foot-link">{{ $t('footer.about') }}</a>
      <t-divider layout="vertical"/>
      <a href="mailto:heza2020@mail.sustech.edu.cn" class="foot-link">{{ $t('footer.feedback') }}</a>
      <t-divider layout="vertical"/>
      <a href="https://cataas.com/cat/gif" class="foot-link">{{ $t('footer.lucky') }}</a>
    </div>
  </div>
</template>


<style>
.foot {
  background-color: rgba(211, 211, 211, 0.20);
  align-items: center;
  display: flex;
  justify-content: space-between;
}

.foot-logo {
  height: 20px;
  align-items: center;
  padding-right: 10px;
  filter: grayscale(95%);
  transition: all 0.3s;
}


.foot-logo:hover {
  height: 20px;
  align-items: center;
  padding-right: 10px;
}

.copyright {
  display: flex;
  padding: 20px 3%;
  color: rgba(0, 0, 0, 0.40);
  font-size: 90%;
  align-items: center;
}

.foot-link {
  text-decoration: none;
  color: rgba(0, 0, 0, 0.40);
}
</style>
