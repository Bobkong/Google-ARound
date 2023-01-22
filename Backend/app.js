var createError = require('http-errors');
var express = require('express');
var path = require('path');
var cookieParser = require('cookie-parser');
var logger = require('morgan');
var session = require('express-session');
var bodyParser = require('body-parser');
var config = require('./config');

var app = express();

// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'jade');

app.use(logger('dev'));
app.use(bodyParser.json({limit: '50mb'}));
app.use(bodyParser.urlencoded({ limit: '50mb', extended: true }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, config['dir.uploads'])));
app.set('trust proxy', 1);
app.use(session({
  secret: config['session.secret'],
  resave: true,
  store: new session.MemoryStore(),
  saveUninitialized: true,
  cookie: { secure: false }
}))

app.use('/',  require('./routes/index'));
app.use('/users',  require('./routes/users'));


// catch 404 and forward to error handler
app.use(function(req, res, next) {
  next(createError(404));
});

// error handler
app.use(function(err, req, res, next) {
  // set locals, only providing error in development
  res.locals.message = err.message;
  res.locals.error = req.app.get('env') === 'development' ? err : {};

  // render the error page
  res.status(err.status || 500);
  res.render('error');
});

var CACHETIME = 60 * 1000 * 60 * 24 * 30;
app.use(express.static(path.join(__dirname, 'public'), { maxAge: CACHETIME }));
app.use(express.static('public'));
module.exports = app;
