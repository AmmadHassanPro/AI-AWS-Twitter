
function searchTerm() {
  var searchTerm = document.getElementById('term-input').value;

  //display currently searched term
  document.getElementById("search-term").innerHTML = searchTerm;
  populateTwitterData(searchTerm);
}

function showTwitterResults() {
  console.log("showtwitterresults hit");
  document.getElementById('key-phrases').style.display = "flex";
}

function showSpinner() {}

function hideSpinner() {}
