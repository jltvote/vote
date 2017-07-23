  var message = (function() {
      var timer = null;
      var id = "$messageId";
      var cssId = "$cssId";
      var styleContent = ".prompt{width:50%;position:fixed;left:25%;height:auto;background:#000;border-radius:5px;top:200px;text-align:center;color:#fff;padding:15px;box-shadow:1px 1px 5px rgba(0,0,0,.5);z-index:99999}@media(min-width:992px){.prompt{top:20px}}";
      var createStyleElement = function() {
          if (isExist(cssId)) {
              return false
          }
          var style = document.createElement("style");
          style.type = "text/css";
          style.id = cssId;
          style.innerHTML = styleContent;
          document.getElementsByTagName("HEAD").item(0).appendChild(style)
      };
      var createMsgElement = function(msg, isClose) {
          if (isExist(id)) {
              clearTimeOut()
          }
          remove();
          var div = document.createElement("div");
          div.id = id;
          div.className = "prompt";
          div.innerHTML = msg;
          document.body.appendChild(div);
          if (!isClose) {
              autoClose()
          }
      };
      var isExist = function(_id) {
          return document.getElementById(_id) ? true : false
      };
      var remove = function() {
          var node = document.getElementById(id);
          if (node) document.body.removeChild(node)
      };
      var clearTimeOut = function() {
          if (timer) window.clearTimeout(timer)
      };
      var autoClose = function() {
          timer = setTimeout(function() {
              clearTimeOut();
              remove()
          }, 3000)
      };
      createStyleElement();
      return {
          msg: function(msg, isClose) {
              createMsgElement(msg, isClose)
          },
          remove: function() {
              if (isExist(id)) {
                  clearTimeOut()
              }
              remove()
          }
      }
  })();
  var giftModule = (function() {
      var userId = '';
      var chainId = '';
      var giftList = [];
      var cur = null;
      var orderId = -1;
      var gift = '<li class="gift-li-cls" did="#{giftId}"  onclick="giftModule.giftClick(\'#{giftId}\',this)">\
                        <div class="product-img"><img src="#{giftpic}"> </div>\
                        <div class="product-title">#{giftName}</div>\
                        <div class="product-price"><span class="cl-red">#{giftPoint}</span>点 </div>\
                    </li>';
      var opt = {
          format: function(c, a) {
              c = String(c);
              var b = Array.prototype.slice.call(arguments, 1),
                  d = Object.prototype.toString;
              if (b.length) {
                  b = b.length == 1 ? (a !== null && (/\[object Array\]|\[object Object\]/.test(d.call(a))) ? a : b) : b;
                  return c.replace(/#\{(.+?)\}/g, function(f, h) {
                      var g = b[h];
                      if ("[object Function]" == d.call(g)) {
                          g = g(h);
                      }
                      return ("undefined" == typeof g ? "" : g);
                  });
              }
              return c;
          },
          getQueryString: function(param) {
              var query = window.location.search;
              var iLen = param.length;
              var iStart = query.indexOf(param);
              if (iStart == -1)
                  return "";
              iStart += iLen + 1;
              var iEnd = query.indexOf("&", iStart);
              if (iEnd == -1)
                  return query.substring(iStart);
              return query.substring(iStart, iEnd);
          },
          downImg: function(url, callback) {
              if (url == '') {
                  return false;
              }
              var img = new Image();
              img.onload = function() {
                  img.onload = null;
                  if (callback) {
                      callback();
                  }
              };
              img.src = url;
          },
          loading: {
              show: function() { document.getElementById('divLoading').style.display = ''; },
              hide: function() { document.getElementById('divLoading').style.display = 'none'; }
          },
          hasClass: function(obj, cls) {
              return obj.className.match(new RegExp('(\\s|^)' + cls + '(\\s|$)'));
          },
          addClass: function(obj, cls) {
              if (!this.hasClass(obj, cls)) obj.className += " " + cls;
          },
          removeClass: function(obj, cls) {
              if (this.hasClass(obj, cls)) {
                  var reg = new RegExp('(\\s|^)' + cls + '(\\s|$)');
                  obj.className = obj.className.replace(reg, ' ');
              }
          },
          queryGiftList: function() {
              var _this = this;
              giftList = [];
              mtAjax.get('gift', '', function(res) {
                  var data = res.data;
                  if (data.status) {
                      var arr = [];
                      giftList = data.data;
                      _.forEach(data.data, function(item) {
                          item.giftpic = 'http://pic.jilunxing.com/vote/b.jpg';
                          arr.push(opt.format(gift, { giftName: item.giftName, giftPoint: item.giftPoint, giftId: item.giftId, giftpic: item.giftpic }));
                      });
                      document.getElementById("ulGiftList").innerHTML = arr.join('');
                      opt.loading.hide();
                  } else {
                      message.msg(data.msg);
                      opt.loading.hide();
                  }
              }, function(err) { opt.loading.hide(); });
          },
          queryUserInfo: function(callback) {
              mtAjax.get('user', { userId: userId }, function(res) {
                  var data = res.data;
                  if (data.status) {
                      var user = data.data;
                      document.getElementById('divUserName').innerHTML = user.name || '';
                      document.getElementById('divUserCode').innerHTML = user.number || '';
                      document.getElementById('divVoteCount').innerHTML = user.voteCount || '0';
                      document.getElementById('divViewCount').innerHTML = user.viewCount || '0';
                      opt.downImg(user.headPic, function() {
                          document.getElementById('imgHeadPic').src = user.headPic;
                      });
                      callback && callback();
                  } else {
                      message.msg(data.msg);
                      opt.loading.hide();
                  }
              }, function(err) {
                  opt.loading.hide();
              });
          },
          giftClick: function(giftId, _this) {
              var findItem = _.find(giftList, function(item) { return item.giftId == giftId; });
              if (findItem) {
                  var liArr = document.getElementsByClassName('gift-li-cls');
                  _.forEach(liArr, function(item) {
                      opt.removeClass(item, 'active');
                  });
                  opt.addClass(_this, 'active');
                  cur = findItem;
              }
          },
          divPrePay: function() {
              if (cur) {
                  opt.loading.show();
                  var param = {
                      chainId: chainId,
                      userId: userId,
                      giftId: cur.giftId,
                      giftCount: document.getElementById('selectCnt').value,
                      openid: openId
                  }
                  console.log(param);
                  vote.jqAjax('/vote/prepay', param, function(res) {
                      if (res.status) {
                          var item = res.data;
                          var payResult = JSON.parse(item.payResult);
                          console.log('payResult', ':', payResult);
                          var _appid = payResult.appId;
                          var _timeStamp = payResult.timeStamp;
                          var _nonceStr = payResult.nonceStr;
                          var _package = payResult.package;
                          var _signType = payResult.signType;
                          var _paySign = payResult.paySign;

                          WeixinJSBridge.invoke('getBrandWCPayRequest', {
                                  "appId": _appid,
                                  "timeStamp": _timeStamp,
                                  "nonceStr": _nonceStr,
                                  "package": _package,
                                  "signType": _signType,
                                  "paySign": _paySign
                              },
                              function(res) {
                                  opt.loading.hide();
                                  if (res.err_msg == "get_brand_wcpay_request:ok") {
                                      alert('支付成功');
                                      //window.location.href = CONFIG.WAP_PAY_URL + "/pay/v_suc?orderId=" + item.orderId + "&serviceType=" + payInfo.serviceType;
                                  } else if (res.err_msg == "get_brand_wcpay_request:cancel") {
                                      message.msg("交易已取消");
                                      orderId = -1;
                                  } else if (res.err_msg == "get_brand_wcpay_request:fail") {
                                      message.msg("支付失败");
                                      orderId = -1;
                                  } else {
                                      message.msg(res.err_msg || res.errMsg);
                                      orderId = -1;
                                  }
                              });

                      } else {
                          message.msg(data.msg);
                          opt.loading.hide();
                      }
                  }, function(err) {
                      opt.loading.hide();
                  }, 'POST', false);
              } else {
                  message.msg('请选择要购买的礼物.');
              }
          },
          build: function() {
              cur = null;
              openId = document.getElementById('inputOpenId').value;
              chainId = document.getElementById('inputChainId').value;
              userId = opt.getQueryString('userId');
              opt.loading.show();
              opt.queryUserInfo(function() {
                  opt.queryGiftList();
              });
          }
      };

      return {
          build: opt.build,
          giftClick: opt.giftClick,
          divPrePay: opt.divPrePay
      }

  })();

  giftModule.build()