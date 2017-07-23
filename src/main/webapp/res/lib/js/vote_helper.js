var vote = (function() {
    var opt = {
        loading: {
            show: function() { document.getElementById('divLoading').style.display = ''; },
            hide: function() { document.getElementById('divLoading').style.display = 'none'; }
        },
        jqAjax: function(url, data, success, error, type, flage) {
            if (!url) {
                message.msg("ajax请求未发现请求地址.");
                error && error();
            } else {
                var obj = {
                    type: (type || 'POST'),
                    url: url,
                    dataType: 'json',
                    async: true,
                    timeout: 0,
                    beforeSend: function(XMLHttpRequest) {},
                    complete: function() {},
                    success: function(json) {
                        if (json.status) {
                            success && success(json);
                        } else {
                            message.msg(json.message);
                            error && error(json);
                        }
                    },
                    error: function(jqXHR, textStatus, errorThrown) {
                        if (error) {
                            error(jqXHR);
                        }
                        try {
                            if (jqXHR.status == 401) {
                                message.error("无权限执行此操作.");
                            } else {
                                var xhr = jqXHR;
                                if (xhr && xhr.responseText) {
                                    var _json = JSON.parse(xhr.responseText);
                                    if (!_json.res) {
                                        _json.msg && message.msg(_json.msg);
                                    } else {
                                        message.msg(xhr.responseText);
                                    }
                                } else {
                                    message.msg("后端错误.");
                                }
                            }
                        } catch (ex) {
                            message.msg("后端错误.");
                        }
                    }
                };
                if (flage) {
                    if (data) {
                        obj.data = data;
                    }
                } else {
                    if ((typeof data) == 'object') {
                        obj.contentType = "application/json;charset=utf-8";
                        if (data) {
                            obj.data = JSON.stringify(data);
                        }
                    } else if ((typeof data) == 'string') {
                        if (data) {
                            obj.data = data;
                        }
                    }
                    $.ajax(obj);
                }
            }
        }
    }

    return {
        loading: opt.loading,
        jqAjax: opt.jqAjax
    }
})();