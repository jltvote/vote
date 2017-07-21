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
             }
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
         ajaxData: function() {
             var _this = this;
             mtAjax.get('users', { pageNo: _this.pagecfg.pageNo, pageSize: _this.pagecfg.pageSize }, function(res) {
                 var data = res.data;
                 console.log(res);

             }, function(err) {});
         }
     }
 });