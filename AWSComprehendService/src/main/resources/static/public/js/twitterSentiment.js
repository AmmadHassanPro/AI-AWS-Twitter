
function createKeyPhraseList(keyPhrases){
  //first remove any previous child li
  var ulNode = document.getElementById('phrase-ul');
  while(ulNode.firstChild) {
    ulNode.removeChild(ulNode.firstChild);
  }
  for (i = 1; i <= keyPhrases.length; i++) {
    var node = document.createElement("LI");
    var textNode = document.createTextNode(i + ") " + keyPhrases[(i-1)].text);
    node.appendChild(textNode);
    document.getElementById('phrase-ul').appendChild(node);
  }
}

function createSentimentChart(sentimentData) {
  google.charts.load('current', {packages: ['corechart', 'bar']});
  google.charts.setOnLoadCallback(drawBarColors);

  function drawBarColors() {
    var posWithNum = "positive: " + sentimentData.positive;
    var negWithNum = "negative: " + sentimentData.negative;
    var data = google.visualization.arrayToDataTable([
      ['sentiment', posWithNum, negWithNum],
      ['public opinion', sentimentData.positive, sentimentData.negative]
    ]);

    var options = {
      title: 'visual comparison between negative and positive sentiment',
      chartArea: {width: '50%'},
      'colors': ['#4D971D', '#DC3B30'],
      vAxis: {
        title: 'sentiment'
      }
    };
    var chart = new google.visualization.BarChart(document.getElementById('sentiment-chart'));
    chart.draw(data, options);
  }
}

function createApathyChart(sentimentData) {
  google.charts.load('current', {'packages':['corechart']});
  google.charts.setOnLoadCallback(drawChart);
  function drawChart() {

    // Create the data table.
    var data = new google.visualization.DataTable();
    data.addColumn('string', 'sentiment');
    data.addColumn('number', 'percentage');
    data.addRows([
      ['positive', sentimentData.positive],
      ['negative', sentimentData.negative],
      ['neutral', sentimentData.neutral],
      ['mixed', sentimentData.mixed]
    ]);

    // Set chart options
    var options = {'title':'Apathy Chart: lower neutral % is more opinionated public. Higher neutral % is less opinionated public',
                   'width':400,
                   'height':300,
                   'colors': ['#4D971D', '#DC3B30', '#F09833', '#3466CC'],
                 };

    // Instantiate and draw our chart, passing in some options.
    var chart = new google.visualization.PieChart(document.getElementById('apathy-chart'));
      chart.draw(data, options);
  }
}


function populateTwitterData(searchTerm) {
  var data;
  var xhttp = new XMLHttpRequest();
  xhttp.onreadystatechange = function() {
    if (this.readyState == 4 && this.status == 200) {
      data = JSON.parse(this.responseText);

      document.getElementById("overall-sentiment").innerHTML = data.overallSentiment.sentiment;
      var sentimentData = data.overallSentiment.sentimentScore;
      createApathyChart(sentimentData);
      createSentimentChart(sentimentData);
      var keyPhrases = data.overallKeyPhrases.keyPhrases;
      createKeyPhraseList(keyPhrases)

      hideSpinner();

      //display content after recieved
      showTwitterResults();
    }
  };
  //TODO: update with prod url
  var singleSentimentUrl = "http://localhost:8080/getOverallTwitterFeel?searchString=" + searchTerm;
  xhttp.open("GET", singleSentimentUrl, true);
  xhttp.send();
}
