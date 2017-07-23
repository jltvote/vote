<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
            <!DOCTYPE html>
            <html>

            <head>
                <meta charset="utf-8">
                <meta http-equiv="X-UA-Compatible" content="IE=edge">
                <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
                <link rel="stylesheet" href="/res/page/fonts/iconfont.css">
                <link href="/res/page/css/custom.css" rel="stylesheet">
                <title>首页</title>
                <style type="text/css">
                    @-webkit-keyframes v-pulseStretchDelay {
                        0%,
                        80% {
                            -webkit-transform: scale(1);
                            transform: scale(1);
                            -webkit-opacity: 1;
                            opacity: 1;
                        }
                        45% {
                            -webkit-transform: scale(0.1);
                            transform: scale(0.1);
                            -webkit-opacity: 0.7;
                            opacity: 0.7;
                        }
                    }
                    
                    @keyframes v-pulseStretchDelay {
                        0%,
                        80% {
                            -webkit-transform: scale(1);
                            transform: scale(1);
                            -webkit-opacity: 1;
                            opacity: 1;
                        }
                        45% {
                            -webkit-transform: scale(0.1);
                            transform: scale(0.1);
                            -webkit-opacity: 0.7;
                            opacity: 0.7;
                        }
                    }
                    
                    .loading-bg {
                        position: fixed;
                        top: 0;
                        right: 0;
                        bottom: 0;
                        left: 0;
                        background: rgba(0, 0, 0, .1);
                        z-index: 99;
                    }
                    
                    .loading {
                        position: fixed;
                        width: 100px;
                        height: 100px;
                        border-radius: 5px;
                        left: 50%;
                        margin-left: -50px;
                        top: 50%;
                        margin-top: -50px;
                        z-index: 100;
                    }
                </style>

            </head>

            <body>
                <script>
                    var campaignDetail = '${campaignDetail}';
                    campaignDetail = campaignDetail && JSON.parse('${campaignDetail}');
                </script>
                <div id="appContent">
                    <router-view></router-view>

                </div>

                <div class="loading-bg" id="divLoading" style="display:none;">
                    <div class="loading">
                        <div style="text-align: center;">
                            <div class="v-pulse v-pulse1" style="animation-fill-mode: both; animation-timing-function: cubic-bezier(0.2, 0.68, 0.18, 1.08); animation-iteration-count: infinite; animation-duration: 0.75s; animation-name: v-pulseStretchDelay; display: inline-block; border-radius: 100%; margin: 2px; height: 15px; width: 15px; background-color: rgb(58, 185, 130); animation-delay: 0.12s;">
                            </div>
                            <div class="v-pulse v-pulse2" style="animation-fill-mode: both; animation-timing-function: cubic-bezier(0.2, 0.68, 0.18, 1.08); animation-iteration-count: infinite; animation-duration: 0.75s; animation-name: v-pulseStretchDelay; display: inline-block; border-radius: 100%; margin: 2px; height: 15px; width: 15px; background-color: rgb(58, 185, 130); animation-delay: 0.24s;">
                            </div>
                            <div class="v-pulse v-pulse3" style="animation-fill-mode: both; animation-timing-function: cubic-bezier(0.2, 0.68, 0.18, 1.08); animation-iteration-count: infinite; animation-duration: 0.75s; animation-name: v-pulseStretchDelay; display: inline-block; border-radius: 100%; margin: 2px; height: 15px; width: 15px; background-color: rgb(58, 185, 130); animation-delay: 0.36s;">
                            </div>
                        </div>
                    </div>
                </div>

                <%@ include file="./template/index.jsp" %>
                    <%@ include file="./template/list.jsp" %>
                        <%@ include file="./template/member.jsp" %>
                            <%@ include file="./template/gif.jsp" %>

                                <!-- build:lib -->

                                <!-- endbuild -->

                                <!-- build:js -->
                                <script src="/res/lib/js/lodash.min.js"></script>
                                <script src="/res/lib/js/moment.min.js"></script>
                                <script src="/res/lib/js/axios.min.js"></script>
                                <script src="/res/lib/js/vue.min.js"></script>
                                <script src="/res/lib/js/vue-router.js"></script>
                                <script src="/res/lib/js/axios.helper.js?v=0004"></script>
                                <script src="/res/lib/js/vote_helper.js?v=0004"></script>

                                <script src="/res/lib/js/jquery.min.js"></script>
                                <script src="/res/lib/js/masonry-docs.min.js"></script>

                                <script src="/res/page/js/index/index.js?v=0004"></script>

                                <script src="/res/page/js/app.js?v=0004"></script>

                                <!-- endbuild -->

            </body>

            </html>