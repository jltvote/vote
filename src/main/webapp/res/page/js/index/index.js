 var IndexModel = Vue.extend({
     template: '#indexTemplateId',
     data: function() {
         return {
             top: {
                 signCount: 0,
                 viewCount: 0,
                 voteCount: 0
             },
             pagecfg: {
                 pageNo: 1,
                 pageSize: 10
             },
             queryKey: '',
             userList: []
         }
     },
     mounted: function() {
         var _this = this;
         _this.top.signCount = campaignDetail.signCount || 0;
         _this.top.viewCount = campaignDetail.viewCount || 0;
         _this.top.voteCount = campaignDetail.voteCount || 0;
         _this.ajaxData();
     },
     methods: {
         rearch: function() {
             _this.pagecfg.pageNo = 1;
             _this.ajaxData();
         },
         ajaxData: function() {

             var _this = this;
             var param = {
                 pageNo: _this.pagecfg.pageNo,
                 pageSize: _this.pagecfg.pageSize
             }
             param.queryKey && (param.queryKey = _this.queryKey);

             mtAjax.get('users', param, function(res) {
                     var data = res.data; // {states:1,msg:'',data:{}}
                     console.log(data)
                     if (data.status) {
                         if (_this.pagecfg.pageNo == 1) {
                             _this.userList.length > 0 && (_this.userList = []);
                             if (data.data.list.length < _this.pagecfg.pageSize) {
                                 _this.pagecfg.pageNo--;
                             }
                         } else {
                             _this.pagecfg.pageNo++;
                         }
                         _this.userList = _this.userList.concat(data.data.list);

                         var $container = $('#masonry');
                         $container.imagesLoaded(function() {
                             $container.masonry({
                                 itemSelector: '.item',
                                 columnWidth: 5 //每两列之间的间隙为5像素
                             });
                         });
                     }
                 },
                 function(err) {});
         }
     }
 });