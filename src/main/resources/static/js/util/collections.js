    export function getIndex(list, id) {
        for (var i = 0; i < list.length; i++) {
            if (list[i].id === id) {
                return i
            }
        }
        return -1
    }


//
//                    const message = { text: this.text }
//
//                    if (this.id) {
//                        this.$resource('/message{/id}').update({id: this.id}, message).then(result =>
//                            result.json().then(data => {
//                                const index = getIndex(this.messages, data.id)
//                                this.messages.splice(index, 1, data)
//                                this.text = ''
//                                this.id = ''
//                            })
//                        )
//                    } else {
//                        this.$resource('/message{/id}').save({}, message).then(result =>
//                            result.json().then(data => {
//                                this.messages.push(data)
//                                this.text = ''
//                            })
//                        )
//                    }