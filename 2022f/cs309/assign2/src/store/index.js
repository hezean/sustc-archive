import Vuex from "vuex";
import types from "./types";
import user from "./modules/course";

const rootState = {
  website: {
    locale: navigator.language.substring(0, 2),
  },
};


const rootGetters = {
  locale: state => state.website.locale,
};


const rootMutations = {
  [types.CHANGE_LANGUAGE](state) {
    if (state.locale === "en") {
      state.locale = "zh";
    } else {
      state.locale = "en";
    }
    console.log(state)
  },
};


const rootActions = {
  changeLocale({ commit }) {
    commit(types.CHANGE_LANGUAGE)
  }
};


export default new Vuex.Store({
  modules: {
    user,
  },
  state: rootState,
  getters: rootGetters,
  mutations: rootMutations,
  actions: rootActions,
  strict: false
});

export { types };
