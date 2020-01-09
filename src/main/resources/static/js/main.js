import Vue from 'vue'
import Vuetify from 'vuetify'
import '@babel/polyfill'
import 'api/resource'
import router from 'router/router'
import App from 'pages/App.vue'
import { connect } from './util/ws'
import store from './store/store'
import 'vuetify/dist/vuetify.min.css'

if (frontendData.profile) {
    connect()
}

Vue.use(Vuetify)


new Vue({
    el: '#app',
    store: store,
    router: router,
    render: a => a(App),
    vuetify: new Vuetify({}),
})