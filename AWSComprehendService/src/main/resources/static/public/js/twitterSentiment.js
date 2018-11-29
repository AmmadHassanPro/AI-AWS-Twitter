function populateTwitterData(searchTerm) {
  var data;
  var xhttp = new XMLHttpRequest();
  xhttp.onreadystatechange = function() {
    if (this.readyState == 4 && this.status == 200) {
      data = JSON.parse(this.responseText);

      //display overall sentiment
      document.getElementById("overall-sentiment").innerHTML = data.overallSentiment.sentiment;
    }
  };
  //TODO: update with prod url
  var singleSentimentUrl = "http://localhost:8080/getOverallTwitterFeel?searchString=" + searchTerm;
  xhttp.open("GET", singleSentimentUrl, true);
  xhttp.send();

}
