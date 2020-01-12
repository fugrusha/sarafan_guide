<template>
    <v-list-item>
        <router-link
                :to="`/user/${user.id}`"

        >
            <v-list-item-avatar
                    v-if="user && user.userpic"
                    :size="`${size || 36}px`"
            >
                <img
                        :src="user.userpic"
                        :alt="user.name"
                >
            </v-list-item-avatar>

            <v-list-item-avatar
                    v-else
                    :size="`${size || 36}px`"
                    color="indigo"
            >
                <v-icon dark>account_circle</v-icon>
            </v-list-item-avatar>
            <span class="px-2">
            {{ userName }}
            </span>
        </router-link>
    </v-list-item>

</template>

<script>
    export default {
        name: 'UserLink',
        props: ['user', 'size'],
        computed: {
            userName() {
                return this.user ? this.user.name : 'unknown'
            }
        },
    }
</script>

<style scoped>
    a {  text-decoration: none;}
</style>