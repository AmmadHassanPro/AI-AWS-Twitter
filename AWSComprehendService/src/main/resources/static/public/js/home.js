
function searchTerm() {
  var searchTerm = document.getElementById('term-input').value;

  //display currently searched term
  document.getElementById("search-term").innerHTML = searchTerm;
  document.getElementById('lists').style.display = "inline-flex";
  populateTwitterData(searchTerm);
}
