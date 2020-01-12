import Vue from 'vue'
import Vuetify from 'vuetify'
import '@babel/polyfill'
import 'api/resource'
import router from 'router/router'
import App from 'pages/App.vue'
import { connect } from './util/ws'
import store from './store/store'
import 'vuetify/dist/vuetify.min.css'
import * as Sentry from '@sentry/browser'
import * as Integrations from '@sentry/integrations'

Sentry.init({
  dsn: 'https://b25310286f3e4da1ac5ff6f1c50914a4@sentry.io/1878611',
  integrations: [new Integrations.Vue({Vue, attachProps: true})],
});

Sentry.configureScope(scope =>
    scope.setUser({
        id: profile && profile.id,
        username: profile && profile.name
    })
)

if (profile) {
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