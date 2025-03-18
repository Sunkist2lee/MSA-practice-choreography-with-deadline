<template>
    <div>
        <div v-if="editMode">
            <v-text-field v-model="value"
                :label="label" 
                @change="change"
                type="number"
                outlined
                dense
            />
        </div>
        <div v-else>
            {{label}} :  {{value}}
        </div>
    </div>
</template>

<script>
    export default {
        name: 'Number',
        props: {
            value:{
                type: Number,
                default: 0
            },
            editMode: Boolean,
            label: String
        },
        methods:{
            change(){
                this.$emit("input", Number(this.value));
            }
        }
    }
</script>
