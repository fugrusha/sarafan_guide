<template>
    <v-container v-if="!profile">Please login via
        <a href="/login">Google</a>
    </v-container>
</template>

<script>
    export default {
        name: 'Auth',
    }
</script>

<style>


</style>