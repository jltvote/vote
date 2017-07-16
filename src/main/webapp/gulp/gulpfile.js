var pageJsArr = [
    '../report/page/js/customer/customer_tj.js',
    '../report/page/js/customer/customer_tj_chart.js',

    '../report/page/js/employee/employee_tj.js',
    '../report/page/js/employee/employee_yjmx.js',

    '../report/page/js/income_and_expenses/income_and_expenses_tj.js',
    '../report/page/js/income_and_expenses/income_and_expenses_tj_chart.js',
    '../report/page/js/income_and_expenses/income_and_expenses_tj_expenses.js',
    '../report/page/js/income_and_expenses/income_and_expenses_tj_income.js',

    '../report/page/js/member_card/member_card_tj.js',
    '../report/page/js/member_card/member_card_tj_chart.js',
    '../report/page/js/member_card/member_card_tj_hk_detail.js',
    '../report/page/js/member_card/member_card_tj_recharege_detail.js',

    '../report/page/js/product_analysis/product_tj.js',

    '../report/page/js/sales_analysis/sales_tj.js',
    '../report/page/js/sales_analysis/sales_tj_chart.js',
    '../report/page/js/sales_analysis/sales_tj_hk_detail.js',
    '../report/page/js/sales_analysis/sales_tj_recharege_detail.js',

    '../report/page/js/achievement/achievement_tj.js',
    '../report/page/js/achievement/achievement_tj_chart.js',
    '../report/page/js/achievement/achievement_tj_detail.js',
    '../report/page/js/achievement/achievement_tj_percentage.js',

    '../report/page/js/mt.app.js'
];

var libJsArr = [
    "../lib/js/lodash.min.js",
    "../lib/js/moment.min.js",
    "../lib/js/axios.min.js",
    "../lib/js/vue.js",
    "../lib/js/vue-router.js",
    "../lib/js/echarts.min.js",
    "../lib/js/vue-scroller.min.js",
    "../lib/js/axios.helper.js",
];

var pageCssArr = [
    '../report/page/css/custom.css'
];

var gulp = require('gulp'), //gulp主组件
    jshint = require('gulp-jshint'), //js语法检查
    concat = require('gulp-concat'), //多个文件合并为一个
    minifyCss = require('gulp-minify-css'), //压缩CSS为一行；
    uglify = require('gulp-uglify'), //js文件压缩
    rev = require('gulp-rev'), //对文件名加MD5后缀
    revCollector = require('gulp-rev-collector'), //路径替换
    replace = require('gulp-replace'), //文件名替换，参考：https://www.npmjs.com/package/gulp-replace
    gulpSequence = require('gulp-sequence'), //同步执行，参考：https://github.com/teambition/gulp-sequence
    clean = require('gulp-clean'), //清除文件插件，参考：https://github.com/teambition/gulp-clean
    htmlreplace = require('gulp-html-replace'),
    qn = require('gulp-qn'); //上传七牛

//上传七牛的配置信息    
var qiniu_options = {
    accessKey: '5jmUHQoqf_snEX0iMqNgFFsVsoWRIDJZJz4hEiRr',
    secretKey: '8VgYKGIBpnoTmaWYXd5PUmMRsqoPhQ32sFquI00o',
    bucket: 'mtcdn03',
    origin: 'http://mtcdn03.mtscrm.com'
};
/**
 * 设置构建输出目录
 */
var buildBasePath = 'build/';
/**
 * 复制需要变更的index.html
 */
gulp.task('copy', function() {
    return gulp.src('../report/index.html')
        .pipe(gulp.dest(buildBasePath + 'report'));
});
//删除Build文件
gulp.task('clean:Build', function(cb) {
    return gulp.src(buildBasePath, { read: false })
        .pipe(clean());
});
//合并js文件之后压缩代码
gulp.task('minifyjs', function() {
    return gulp.src(pageJsArr)
        .pipe(concat('build.js')) //合成到一个js
        .pipe(gulp.dest(buildBasePath + 'js')) //输出到js目录
        .pipe(uglify()) //压缩js到一行
        .pipe(concat('build.min.js')) //压缩后的js
        .pipe(gulp.dest(buildBasePath + 'js'))
        .pipe(rev())
        .pipe(gulp.dest(buildBasePath + 'js'))
        .pipe(rev.manifest("js-md5.json"))
        .pipe(gulp.dest(buildBasePath + 'rev')); //输出到js目录
});

gulp.task('minifyLibjs', function() {
    return gulp.src(libJsArr)
        .pipe(concat('lib.js')) //合成到一个js
        .pipe(gulp.dest(buildBasePath + 'lib')) //输出到js目录
        .pipe(uglify()) //压缩js到一行
        .pipe(concat('lib.min.js')) //压缩后的js
        .pipe(gulp.dest(buildBasePath + 'lib'))
        .pipe(rev())
        .pipe(gulp.dest(buildBasePath + 'lib'))
        .pipe(rev.manifest("lib-js-md5.json"))
        .pipe(gulp.dest(buildBasePath + 'rev')); //输出到js目录
});
//合并CSS文件之后压缩代码
gulp.task('minifycss', function() {
    return gulp.src(pageCssArr)
        .pipe(concat('build.css')) //合成到一个css
        .pipe(gulp.dest(buildBasePath + 'css')) //输出到css目录
        .pipe(replace('../img/', '')) //处理图片的地址
        .pipe(minifyCss()) //压缩css到一样
        .pipe(concat('build.min.css')) //压缩后的css
        .pipe(gulp.dest(buildBasePath + 'css'))
        .pipe(rev())
        .pipe(gulp.dest(buildBasePath + 'css'))
        .pipe(rev.manifest("css-md5.json"))
        .pipe(gulp.dest(buildBasePath + 'rev')); //文件名加MD5后缀; //输出到css目录
});
//图片MD5
gulp.task('imgRev', function() {
    return gulp.src('../report/page/img/*.png')
        .pipe(rev())
        .pipe(gulp.dest(buildBasePath + 'img'))
        .pipe(rev.manifest("img-md5.json")) //文件名加MD5后缀; //输出到css目录
        .pipe(gulp.dest(buildBasePath + 'rev'));
});
//替换对应的文件内容
gulp.task('replaceJsCss', function() {
    return gulp.src(buildBasePath + '/report/index.html')
        .pipe(replace('page/img/', qiniu_options.origin + '/')) //处理图片的地址
        .pipe(htmlreplace({
            'lib': qiniu_options.origin + '/lib.min.js',
            'js': qiniu_options.origin + '/build.min.js',
            'css': qiniu_options.origin + '/build.min.css'
        }))
        .pipe(gulp.dest(buildBasePath + 'dist/'));
});

//替换对应的md5的数据
gulp.task('rev', function() {
    return gulp.src([buildBasePath + 'rev/*.json', buildBasePath + 'dist/*.html'])
        .pipe(revCollector())
        .pipe(gulp.dest(buildBasePath + '/result/'));
});
gulp.task('revCss', function() {
    return gulp.src([buildBasePath + 'rev/img-md5.json', buildBasePath + 'css/build-*.css'])
        .pipe(revCollector())
        .pipe(gulp.dest(buildBasePath + '/dist/'));
});

//上传七牛
gulp.task('qnUp', function() {
    return gulp.src([buildBasePath + 'js/build-*.js', buildBasePath + 'lib/lib-*.js', buildBasePath + 'dist/build-*.css', buildBasePath + 'img/*.png'])
        .pipe(qn({ qiniu: qiniu_options }));
});

gulp.task('gulp-sequence', gulpSequence('copy', 'minifyjs', 'minifyLibjs', 'imgRev', 'minifycss', 'replaceJsCss', 'rev', 'revCss', 'qnUp'));

gulp.task('default', ['gulp-sequence']);