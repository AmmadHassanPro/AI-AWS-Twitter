
function createSentimentChart(data) {

}

function createApathyChart(data) {
  var sentimentData = data.overallSentiment.sentimentScore;
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

      createApathyChart(data);
    }
  };
  //TODO: update with prod url
  var singleSentimentUrl = "http://localhost:8080/getOverallTwitterFeel?searchString=" + searchTerm;
  xhttp.open("GET", singleSentimentUrl, true);
  xhttp.send();
}
