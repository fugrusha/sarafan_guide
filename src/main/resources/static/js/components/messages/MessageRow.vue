<template>

    <div>
        <i>{{ message.id }}</i> {{ message.text }}
        <span style="position: absolute; right: 0;">
            <input type="button" value="edit" @click="edit" />
            <input type="button" value="x" @click="del" />
        </span>
    </div>

</template>

<script>
    export default {
        props: ['message', 'editMessage', 'deleteMessage', 'messages'],
        methods: {
            edit() {
                this.editMessage(this.message)
            },
            del() {
                this.deleteMessage(this.message)
            }
        }
    }


</script>

<style>

</style>
