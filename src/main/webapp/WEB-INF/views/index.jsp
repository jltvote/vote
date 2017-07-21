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
            </head>

            <body>
                <script>
                    var campaignDetail = '${campaignDetail}';
                    campaignDetail = campaignDetail && JSON.parse('${campaignDetail}');
                </script>
                <div id="appContent">
                    <router-view></router-view>
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
                                <script src="/res/lib/js/axios.helper.js?v=0001"></script>



                                <script src="/res/page/js/index/index.js?v=0001"></script>

                                <script src="/res/page/js/app.js"></script>

                                <!-- endbuild -->

            </body>

            </html>