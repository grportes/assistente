var express = require('express');

var port = 3000;

var app = express();

app.use(express.static('public'));

app.get('/', function(req, res) {
  res.json({status: 'Server is running!'})
});

app.listen(port, function() {
  console.log(`Server is running at localhost:${port}`)
});

