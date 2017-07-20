<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <template id="indexTemplateId">
      <div class="content">
        <div class="show-img"> 
          <img src="/res/page/img/img.png"> 
        </div>
        <div class="show-info">
          <ul>
            <li>
              <div class="show-info-title"> <span class="iconfont icon-baoming"></span>已报名 </div>
              <div class="show-info-num"> {{top.signCount}} </div>
            </li>
            <li>
              <div class="show-info-title"> <span class="iconfont icon-like"></span>累计投票 </div>
              <div class="show-info-num"> {{top.voteCount}} </div>
            </li>
            <li>
              <div class="show-info-title"> <span class="iconfont icon-fangwenliang"></span>访问量 </div>
              <div class="show-info-num">  {{top.viewCount}}  </div>
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
</template>