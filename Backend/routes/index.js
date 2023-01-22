var express = require('express');
var router = express.Router();

// answer_service.addAnswer({
//   content: '做毕设好难!',
//   questionId: 3,
//   questionTitle: '健健康康的',
//   authorId: '0BC7CBCC17D3D4943D5243D422103B68',
//   authorName: '孔Bob',
//   authorIcon: 'http://thirdqq.qlogo.cn/g?b=oidb&k=rNqWdrSQcD4jUlkyoE6tnA&s=100',
//   likeCount: 0,
//   commentCount: 0
// });

 /* GET home page. */
 router.get('/', function(req, res, next) {
   res.render('index', { title: 'Hello AR Localizer!' });
 });

module.exports = router;
