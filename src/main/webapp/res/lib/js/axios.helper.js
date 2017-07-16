var reqJson = { accessKeyId: '', appId: '', accessToken: '', tenantIds: '' };

function setRightBtnCfg(shareBtn, tenantSelectBtn) {
    try {
        setTimeout(function() {
            MtscrmJsBridge.setRightBtnCfg(JSON.stringify({ "shareBtn": shareBtn, "tenantSelectBtn": tenantSelectBtn }));
        }, 1000);
    } catch (ex) {
        alert(ex.message);
    }
}

function setTenantIds(tenantIds) {
    try {
        reqJson.tenantIds = tenantIds;
        mtAjax.appCallBack && mtAjax.appCallBack();
    } catch (ex) {
        alert(ex.message);
    }
}

function getAppBaseInfoCallBack(response) {
    try {
        if (_.isString(response)) {
            response = JSON.parse(response);
        }
        reqJson.accessKeyId = response.accessKeyId;
        reqJson.appId = response.appId;
        reqJson.accessToken = response.accessToken;
        reqJson.tenantIds = response.tenantIds;
        mtAjax.appCallBack && mtAjax.appCallBack();
    } catch (ex) {
        alert(ex.message);
    }
}

function getAppBaseInfo() {
    try {
        MtscrmJsBridge.getAppInfoByKeys(JSON.stringify(reqJson), "getAppBaseInfoCallBack");
    } catch (error) {
        alert(error.message);
    }
}

var mtAjax = (function() {
    //全局设定
    axios.defaults.baseURL = 'https://web.mtscrm.com';
    axios.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded';
    var opt = {
        getCfg: function(url, method, params) {
            try {
                if (!url) { console.error('请求地址不可为空.'); return false; }
                if ((url.indexOf('?') >= 0) && params) { console.error('地址中带有参数且params也包含参数,这种情况不允许.'); return false; } //问号和params 同时存在进行报错处理

                var obj = {
                    url: url,
                    method: method || 'POST', //变更为默认为POST请求
                    headers: {
                        'X-Requested-With': 'XMLHttpRequest',
                        'x-mt-date': (new Date().toUTCString()),
                        'Authorization': reqJson.accessKeyId,
                        'x-app-id': reqJson.appId,
                        'x-access-token': reqJson.accessToken
                    },
                    validateStatus: function(status) {
                        return status < 500;
                    },
                };
                if (params) { //GET 或 POST 传递的参数的一种形式 GET的可以进行URL进行传递值 键值对的格式 
                    obj.params = params;
                }
                if (reqJson.tenantIds) {
                    obj.params.tenantIds = reqJson.tenantIds;
                }
                return obj;
            } catch (ex) {
                alert(ex.message);
            }
        },
        errHandle: function(err) {
            if (err.response) {
                console.log(err.response.data);
                console.log(err.response.status);
                console.log(err.response.headers);
            } else if (err.request) {
                console.log(err.request);
            } else {
                alert(err.message);
                console.log('Error', err.message);
            }
            console.log(err.config);
        },
        loading: {
            show: function() { document.getElementById('divLoading').style.display = ""; },
            hide: function() { document.getElementById('divLoading').style.display = "none"; }
        }
    };

    var _axios = {
        get: function(url, params, success, error, isHideLoading) {
            !isHideLoading && opt.loading.show();
            axios(opt.getCfg(url, 'GET', params))
                .then(function(res) {
                    if (res.data.errorCode) {
                        alert(res.data.errorMessage);
                    } else {
                        success && success(res);
                    }
                    opt.loading.hide();
                })
                .catch(function(err) {
                    opt.loading.hide();
                    opt.errHandle(err);
                    error && error(err);
                });
        },
        post: function(url, params, success, error) {
            opt.loading.show();
            axios(opt.getCfg(url, 'POST', params))
                .then(function(res) {
                    if (res.data.errorCode) {
                        alert(res.data.errorMessage);
                    } else {
                        success && success(res);
                    }
                    opt.loading.hide();
                })
                .catch(function(err) {
                    opt.errHandle(err);
                    error && error(err);
                    opt.loading.hide();
                });
        },
        put: function(url, params, success, error) {
            opt.loading.show();
            axios(opt.getCfg(url, 'PUT', params))
                .then(function(res) {
                    if (res.data.errorCode) {
                        alert(res.data.errorMessage);
                    } else {
                        success && success(res);
                    }
                    opt.loading.hide();
                })
                .catch(function(err) {
                    opt.errHandle(err);
                    error && error(err);
                    opt.loading.hide();
                });
        },
        all: function(reqMethodsArr, callback, error) {
            opt.loading.show();
            axios.all(reqMethodsArr)
                .then(axios.spread(callback)).catch(function(err) {
                    opt.errHandle(err);
                    opt.loading.hide();
                    error && error(err)
                });
        }
    };

    return {
        get: _axios.get,
        post: _axios.post,
        put: _axios.put,
        all: _axios.all,
    }
})();