<template>

    <v-layout row>
        <v-text-field
                label="New Message"
                placeholder="You can share something"
                v-model="text" />
        <v-btn @click="save">Send</v-btn>
    </v-layout>

</template>

<script>
    import { sendMessage } from '../../util/ws'

    export default {
        props: ['messages', 'messageAttr'],
        data() {
            return {
                text: '',
                id: ''
            }
        },
        watch: {
            messageAttr: function(newVal, oldVal) {
                this.text = newVal.text
                this.id = newVal.id
            }
        },

        methods: {
            save() {
                sendMessage({id: this.id, text: this.text})
                this.text = ''
                this.id = ''
            }
        }
    }


</script>

<style>


</style>