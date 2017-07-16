<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN""http://www.w3.org/TR/html4/loose.dtd">
    <html>

    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
        <link rel="stylesheet" href="../../res/page/fonts/iconfont.css">
        <link href="../../res/page/css/custom.css" rel="stylesheet">
        <title>首页</title>
    </head>

    <body>

        <div class="content">
            <div class="show-img"> <img src="img/img.png"> </div>
            <div class="show-info">
                <ul>
                    <li>
                        <div class="show-info-title"> <span class="iconfont icon-baoming"></span>已报名 </div>
                        <div class="show-info-num"> 187 </div>
                    </li>
                    <li>
                        <div class="show-info-title"> <span class="iconfont icon-like"></span>累计投票 </div>
                        <div class="show-info-num"> 128818 </div>
                    </li>
                    <li>
                        <div class="show-info-title"> <span class="iconfont icon-fangwenliang"></span>访问量 </div>
                        <div class="show-info-num"> 187 </div>
                    </li>
                </ul>
            </div>
            <div class="time">
                <div class="time-title"> 活动时间倒计时 </div>
                <div class="time-input"> <span class="time-box"> 0天 </span> <span class="time-box"> 0时 </span> <span class="time-box"> 00分 </span> <span class="time-box"> 00秒 </span> </div>
            </div>
            <div class="search-input">
                <div class="row">
                    <div class="col-md-12">
                        <form class="form-inline">
                            <div class="form-group">
                                <div class="col-xs-8" style="padding-right: 0;">
                                    <input type="text" class="form-control" placeholder="请输入编号或姓名">
                                </div>
                                <div class="col-xs-4">
                                    <button type="submit" class="btn btn-default btn-block">搜索</button>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
            <div class="wrapper">
                <div id="masonry">
                    <div class="item">
                        <div class="item-num">编号：001号</div>
                        <div class="item-name">张三
                            <div class="item-ticket-num"><span class="ticket-num">152</span>票</div>
                        </div>
                        <img src="img/imt1.jpg">

                        <a class="ticket-link">为TA拉票</a>
                        <div class="ticket-btn">
                            <a class="btn btn-block btn-red" href="member.html">给TA投票</a>
                        </div>
                    </div>
                    <div class="item">
                        <div class="item-num">编号：002号</div>
                        <div class="item-name">张三
                            <div class="item-ticket-num"><span class="ticket-num">152</span>票</div>
                        </div>
                        <img src="img/img2.jpg">

                        <a class="ticket-link">为TA拉票</a>
                        <div class="ticket-btn">
                            <a class="btn btn-block btn-red" href="member.html">给TA投票</a>
                        </div>
                    </div>

                    <div class="item">
                        <div class="item-num">编号：003号</div>
                        <div class="item-name">张三
                            <div class="item-ticket-num"><span class="ticket-num">152</span>票</div>
                        </div>
                        <img src="img/img3.jpg">

                        <a class="ticket-link">为TA拉票</a>
                        <div class="ticket-btn">
                            <a class="btn btn-block btn-red" href="member.html">给TA投票</a>
                        </div>
                    </div>

                    <div class="item">
                        <div class="item-num">编号：004号</div>
                        <div class="item-name">张三
                            <div class="item-ticket-num"><span class="ticket-num">152</span>票</div>
                        </div>
                        <img src="img/img4.jpg">

                        <a class="ticket-link">为TA拉票</a>
                        <div class="ticket-btn">
                            <a class="btn btn-block btn-red" href="member.html">给TA投票</a>
                        </div>
                    </div>

                </div>
            </div>
        </div>

        <div class="bar-tab">
            <ul>
                <li class="active">
                    <a href="index.html">
                        <div class="tab-icon"> <span class="iconfont icon-shouye"></span> </div>
                        <div class="tab-title"> 首页 </div>
                    </a>
                </li>
                <li>
                    <a href="gift.html">
                        <div class="tab-icon"> <span class="iconfont icon-jiangpin"></span> </div>
                        <div class="tab-title"> 奖品 </div>
                    </a>
                </li>
                <li>
                    <a href="list.html">
                        <div class="tab-icon"> <span class="iconfont icon-tubiao-"></span> </div>
                        <div class="tab-title"> 榜单 </div>
                    </a>
                </li>
            </ul>
        </div>

        <script src="../../res/lib/js/jquery.min.js"></script>
        <script src="../../res/lib/js/masonry-docs.min.js"></script>
        <script>
            $(document).ready(function() {
                var $container = $('#masonry');
                $container.imagesLoaded(function() {
                    $container.masonry({
                        itemSelector: '.item',
                        columnWidth: 5 //每两列之间的间隙为5像素
                    });
                });

            });
        </script>

    </body>

    </html>