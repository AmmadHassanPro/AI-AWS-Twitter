
function createSentimentChart(sentimentData) {
  google.charts.load('current', {packages: ['corechart', 'bar']});
  google.charts.setOnLoadCallback(drawBarColors);

  function drawBarColors() {
    var posWithNum = "positive - " + sentimentData.positive;
    var negWithNum = "negative - " + sentimentData.negative;
    var data = google.visualization.arrayToDataTable([
      ['sentiment', posWithNum, negWithNum],
      ['public opinion', sentimentData.positive, sentimentData.negative]
    ]);

    var options = {
      title: 'visual ratio between negative and positive sentiment',
      chartArea: {width: '50%'},
      colors: ['#b0120a', '#ffab91'],
      hAxis: {
        title: 'sentiment percentage',
        minValue: 0
      },
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
    var options = {'title':'Consumer Apathy',
                   'width':400,
                   'height':300};

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
      //display overall sentiment
      document.getElementById("overall-sentiment").innerHTML = data.overallSentiment.sentiment;

      var sentimentData = data.overallSentiment.sentimentScore;
      createApathyChart(sentimentData);
      createSentimentChart(sentimentData);
    }
  };
  //TODO: update with prod url
  var singleSentimentUrl = "http://localhost:8080/getOverallTwitterFeel?searchString=" + searchTerm;
  xhttp.open("GET", singleSentimentUrl, true);
  xhttp.send();
}
