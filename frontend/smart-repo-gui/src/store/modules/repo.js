/* eslint-disable no-unused-vars */
import axios from "axios";
import qs from "qs";

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
            isUploaded: false,
            breadcrumbItems: [
                {
                    text: 'Home',
                    disabled: false,
                    href: '/'
                },
            ],
        };
    },
    mutations: {
        UPDATE_FILES(state, files){
            state.files = files;
        },
        UPDATE_BREADCRUMB_ITEMS(state, items){
            state.breadcrumbItems = items;
        },
        FILES_UPLOADED(state){
            state.isUploaded = true;
            setTimeout(()=>{
                state.isUploaded = false;
            },3000)
        },
        RESTART_FILES(state){
            state.files = [];
            state.isUploaded=false;
        }
    },
    actions: {
        uploadFiles(
            { commit, dispatch, rootGetters, getters, rootState, state }, {file, path, retVal}
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
                        if (response.data.startsWith("FILE_ALREADY_EXISTS:")) {
                            retVal.exitCode = 3;
                            retVal.description = "File \"" + response.data.substring(21) + "\" already exists in this directory.";
                        } else {
                            commit('FILES_UPLOADED')
                            dispatch('loadFiles', path);
                            retVal.exitCode = 0;
                        }
                    } else {
                        console.log("ERROR: (" + response.status + ")")
                        retVal.exitCode = 2;
                    }
                })
                .catch(error => {
                    console.error("An error occurred during receiving response!\n", error);
                    retVal.exitCode = 1;
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
        },
        updateBreadcrumbItems({ commit, dispatch, rootGetters, getters, rootState, state }, item){
            const newBreadcrumbItems = []
            const newItems = item.split('/')
            const obj = {
                text: 'Home',
                disabled: false,
                href: '/'
            };
            newBreadcrumbItems.push(obj)
            newItems.shift()
            let path = ''
            newItems.forEach(item => {
                path= path + '/' + item
                const obj = {
                        text: item,
                        disabled: false,
                        href: path
                    };
                newBreadcrumbItems.push(obj)
            })
            commit('UPDATE_BREADCRUMB_ITEMS',newBreadcrumbItems);
        },
        fileDelete({ commit, dispatch, rootGetters, getters, rootState, state }, path){
            console.log(path)
            axios.delete("http://localhost:7777/files", {params:{
                path:path
                }})
                .then(async response =>{
                    console.log(response)
                    dispatch('loadFiles',path.split('/').slice(0, -1).join('/'));
                })
                .catch(error =>{
                    console.error("An error occurred during deleting file!\n", error)
                })
        },
        directoryPost({ commit, dispatch, rootGetters, getters, rootState, state }, path){
            console.log(path)
            axios.post("http://localhost:7777/files", {path:path})
                .then(async response =>{
                    console.log(response)
                    dispatch('loadFiles',path.split('/').slice(0, -1).join('/'));
                })
                .catch(error =>{
                    console.error("An error occurred during creating directory!\n", error)
                })
        },
        search({ commit, dispatch, rootGetters, getters, rootState, state }, { phrase, languages }){
            axios.post("http://localhost:7777/search", {
                    phrase:phrase?phrase:'',
                    languages:languages?languages:[],
            })
                .then(async response =>{
                    commit('UPDATE_FILES',response.data)
                })
                .catch(error =>{
                    console.error("An error occurred during receiving response!\n", error)
                })
        },
    },
    getters:{

    }
}
export default repoModule;