var vote = (function() {

    var opt = {
        loading: {
            show: function() { document.getElementById('divLoading').style.display = ''; },
            hide: function() { document.getElementById('divLoading').style.display = 'none'; }
        }
    };

    return {
        loading: opt.loading
    }
})();