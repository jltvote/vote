(function() {
    var routes = [
        { path: '/', component: IndexModel }, //设置默认路由
        { path: '/home', component: IndexModel }, //设置默认路由
    ]

    var router = new VueRouter({
        routes: routes
    })

    var app = new Vue({
        router: router
    }).$mount('#appContent');

})();