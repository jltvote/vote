 var IndexModel = Vue.extend({
     template: '#indexTemplateId',
     data: function() {
         return {
             top: {
                 signCount: 0,
                 viewCount: 0,
                 voteCount: 0
             }
         }
     },
     mounted: function() {
         var _this = this;
         _this.top.signCount = campaignDetail.signCount || 0;
         _this.top.viewCount = campaignDetail.viewCount || 0;
         _this.top.voteCount = campaignDetail.voteCount || 0;

     },
     methods: {
         ajaxData: function() {
             var _this = this;
             mtAjax.get('/v1/commission/employee', { st: st, et: et }, function(res) {
                 var data = res.data;


             }, function(err) {});
         }
     }
 });