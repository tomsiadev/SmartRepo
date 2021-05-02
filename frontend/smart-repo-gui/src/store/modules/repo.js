/* eslint-disable no-unused-vars */
import axios from "axios";
const defaultState = () => {
    return {
        files: [],
    };
};

const repoModule = {
    namespaced: true,
    state: () => {
        return {
            files: [],
        };
    },
    mutations: {
        UPDATE_FILES(state, files){
            state.files = files;
        },
        RESTART_FILES(state){
            state.files = [];
        }
    },
    actions: {
        uploadFiles(
            { commit, dispatch, rootGetters, getters, rootState, state }, file
        ) {
            axios.post("http://localhost:7777/upload", file,
                {
                    headers: {
                        'Content-Type': 'multipart/form-data'
                    }
                }
            )
                .then(async response => {
                    if (response.status === 200) {
                        console.log("Response: " + response.data)
                    } else {
                        console.log("ERROR: (" + response.status + ")")
                    }
                })
                .catch(error => {
                    console.error("An error occurred during receiving response!\n", error);
                })
        },
        loadFiles({ commit, dispatch, rootGetters, getters, rootState, state }, name){
            axios.get("http://localhost:7777/files", {params:{
                path:name?name:''
                }})
                .then(async response =>{
                    commit('UPDATE_FILES',response.data)
                })
                .catch(error =>{
                    console.error("An error occurred during receiving response!\n", error)
                })
        },
        resetFiles({ commit, dispatch, rootGetters, getters, rootState, state }){
            commit('RESTART_FILES');
        }
    },
    getters:{

    }
}
export default repoModule;