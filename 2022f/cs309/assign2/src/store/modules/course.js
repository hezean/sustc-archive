import types from '../types'
import { string } from "i/lib/util";

const state = {
  code: string,
  name: string,
  language: 'lang.zh' | 'lang.en' | 'lang.bi',
  teachers: [],
  date: Date,
  time: string,
  duration: 0,
  location: string,
};


const getters = {
  user: state => state.user,

  avatar: state => {
    let base64 = state.user.profile.avatar;
    if (base64) {
      return "data:image/png;base64," + base64;
    }
    let name = state.user.profile.nickname;
    return name.charAt(name.length - 1);
  },

  userId: state => state.user.profile.id,
  isAuthenticated: state => !!state.user.profile && !!state.user.profile.id,
};


const mutations = {
  [types.USER_UPDATE_INFO](state, { info }) {
    state.user = info;
    // if (!!info.preference && info.preference.language) {
    //   i18n.global.locale = info.preference.language
    // }
  }
};


const actions = {
  getUserInfo({ commit }) {

    commit(types.USER_UPDATE_INFO, {
      info: {
        profile: {
          // avatar:
        }
      }
    });
  },

  clearUserInfo({ commit }) {
    commit(types.USER_UPDATE_INFO, {
      info: {}
    });
  }
};


export default {
  state,
  getters,
  actions,
  mutations,
};
