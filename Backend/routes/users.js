var express = require('express');
var router = express.Router();

var users = [
  {name: 'Liz', password: 'hiLiz', cloudAnchor: {latitude: 0.0, longitude:0.0, altitude: 0.0}},
  {name: 'Ryan', password: 'hiRyan', cloudAnchor: {latitude: 0.0, longitude:0.0, altitude: 0.0}}
]

const resetInterval = 20 * 60 * 1000;
var lastRyanTime = 0;
var lastLizTime = 0;

router.get('/login',async function(req,res){
  res.writeHead(200, 'Content-Type', 'application/json');
  if(!checksParmas(req)){FAppCompatActivity
    return;
  }
  var name = req.query["username"];
  console.log(name)
  var password = req.query["password"];

  let ifExist = false;
  users.forEach(u => {
    if(u.name == name) {
      ifExist = true;
      if(u.password == password) {
        // login success
        res.end(JSON.stringify({
          success: true,
        }));
        u.isLogin = true;
      } else {
        res.end(JSON.stringify({
          success: false,
          err: "password incorrect!"
       }));
      }
      return;
    }
  })
    
  if(!ifExist) {
    console.log(users)
    res.end(JSON.stringify({
      success: false,
      err: "The user doesn't exist!"
   }));
  }
  

});

router.post('/navigate', async function(req, res){
  res.writeHead(200, 'Content-Type', 'application/json');
  if(!checksParmas(req)){
    return;
  }
  var name = req.body.username;
  var lat = req.body.latitude;
  var long = req.body.longitude;
  var alt = req.body.altitude;

  users.forEach(u => {
    if(name == "Liz") {
      users[0].cloudAnchor = {
        latitude: lat, longitude: long, altitude: alt
      }

      if ((new Date()).valueOf() - lastRyanTime > resetInterval) {
        users[1].cloudAnchor = {
          latitude: 0.0, longitude:0.0, altitude: 0.0
        }
      }
      res.end(JSON.stringify({
          success: true,
          cloudAnchor: users[1].cloudAnchor
      }));
      lastLizTime = (new Date()).valueOf();
    } else {
      users[1].cloudAnchor = {
        latitude: lat, longitude: long, altitude: alt
      }

      if ((new Date()).valueOf() - lastLizTime > resetInterval) {
        users[0].cloudAnchor = {
          latitude: 0.0, longitude:0.0, altitude: 0.0
        }
      }
      res.end(JSON.stringify({
          success: true,
          cloudAnchor: users[0].cloudAnchor
      }));
      lastRyanTime = (new Date()).valueOf();
    }
  })
  
});


router.post('/stopnavigate', async function(req, res){
  res.writeHead(200, 'Content-Type', 'application/json');
  if(!checksParmas(req)){
    return;
  }
  var name = req.body.username;

  users.forEach(u => {
    if(name == "Liz") {
      users[0].cloudAnchor = {latitude: 0.0, longitude:0.0, altitude: 0.0};
    } else {
      users[1].cloudAnchor = {latitude: 0.0, longitude:0.0, altitude: 0.0};
    }
  })

  res.end(JSON.stringify({
    success: true,
}));
  
});

function checksParmas(req){
    if(!req.body){
      req.end(JSON.stringify({
        success: false,
        err: {
          msg: 'lack of parameters'
        }
      }));
      return false;
    }
    return true;
  }

 

module.exports = router;
